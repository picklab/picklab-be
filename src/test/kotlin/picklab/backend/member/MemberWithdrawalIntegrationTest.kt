package picklab.backend.member

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import picklab.backend.auth.application.TokenUseCase
import picklab.backend.auth.domain.OAuthUserInfo
import picklab.backend.auth.infrastructure.AccessTokenProvider
import picklab.backend.auth.infrastructure.MemberWithdrawalListener
import picklab.backend.auth.infrastructure.RefreshTokenProvider
import picklab.backend.auth.infrastructure.WithdrawnMemberTokenBlocklist
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.MemberService
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.entity.SocialLogin
import picklab.backend.member.domain.enums.SocialType
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.member.domain.repository.SocialLoginRepository
import picklab.backend.member.domain.service.NotificationPreferenceService
import picklab.backend.notification.domain.service.SseEmitterService
import picklab.backend.template.IntegrationTest
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestPropertySource(properties = ["jwt.access.expiration=600000", "jwt.refresh.expiration=600000"])
class MemberWithdrawalIntegrationTest : IntegrationTest() {
    @Autowired
    lateinit var memberService: MemberService

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var socialLoginRepository: SocialLoginRepository

    @Autowired
    lateinit var blocklist: WithdrawnMemberTokenBlocklist

    @Autowired
    lateinit var accessTokenProvider: AccessTokenProvider

    @Autowired
    lateinit var refreshTokenProvider: RefreshTokenProvider

    @Autowired
    lateinit var tokenUseCase: TokenUseCase

    @Autowired
    lateinit var transactionManager: PlatformTransactionManager

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var sseEmitterService: SseEmitterService

    @Autowired
    lateinit var notificationPreferenceService: NotificationPreferenceService

    @BeforeAll
    fun setUp() {
        cleanUp.all()
    }

    @Test
    fun rejoiningCreatesANewMemberAndReleasesTheSocialIdentity() {
        val userInfo = userInfo()
        val member = memberService.loginOrSignup(SocialType.KAKAO, userInfo)
        member.nickname = "이전닉네임"
        member.isCompleted = true
        memberRepository.save(member)
        memberService.saveRefreshToken(member.id, "old-refresh-token")

        memberService.withdrawMember(member.id)
        val rejoined = memberService.loginOrSignup(SocialType.KAKAO, userInfo)

        val withdrawn = memberRepository.findByIdIgnoreDelete(member.id)!!
        assertThat(withdrawn.deletedAt).isNotNull()
        assertThat(withdrawn.refreshToken).isEmpty()
        assertThat(socialLoginRepository.findAllByMemberId(member.id)).isEmpty()
        assertThat(rejoined.id).isNotEqualTo(member.id)
        assertThat(rejoined.nickname).isEmpty()
        assertThat(rejoined.isCompleted).isFalse()
        assertThat(blocklist.isBlocked(member.id)).isTrue()
        assertThat(blocklist.isBlocked(rejoined.id)).isFalse()
        assertThat(memberService.loginOrSignup(SocialType.KAKAO, userInfo).id).isEqualTo(rejoined.id)

        memberService.withdrawMember(rejoined.id)
        assertThat(memberService.loginOrSignup(SocialType.KAKAO, userInfo).id).isNotIn(member.id, rejoined.id)
    }

    @Test
    fun withdrawalBlocksExistingTokensAndDisconnectsNotifications() {
        val member = memberService.loginOrSignup(SocialType.KAKAO, userInfo())
        val accessToken = accessTokenProvider.generateToken(member.id)
        val refreshToken = refreshTokenProvider.generateToken(member.id)
        memberService.saveRefreshToken(member.id, refreshToken)
        sseEmitterService.createEmitter(member.id)

        mockMvc
            .get("/v1/test/auth/filter") {
                header("Authorization", "Bearer $accessToken")
            }.andExpect { status { isOk() } }

        val result =
            mockMvc
                .delete("/v1/members") {
                    header("Authorization", "Bearer $accessToken")
                }.andExpect {
                    status { isOk() }
                }.andReturn()
        assertThat(result.response.getHeaders("Set-Cookie")).hasSize(2).allSatisfy { cookie ->
            assertThat(cookie).contains("Max-Age=0")
        }

        mockMvc
            .get("/v1/test/auth/filter") {
                header("Authorization", "Bearer $accessToken")
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.message") { value(ErrorCode.INVALID_MEMBER.message) }
            }
        assertThat(assertThrows<BusinessException> { tokenUseCase.refreshAccessToken(refreshToken) }.errorCode)
            .isEqualTo(ErrorCode.INVALID_MEMBER)
        assertThat(sseEmitterService.isUserConnected(member.id)).isFalse()
        assertThat(notificationPreferenceService.filterMembersWithBookmarkNotificationEnabled(listOf(member.id))).isEmpty()
        assertThat(notificationPreferenceService.getMembersWithPopularNotificationEnabledPaged(PageRequest.of(0, 100)).content)
            .doesNotContain(member.id)
    }

    @Test
    fun rollbackDoesNotBlockTokensOrReleaseTheSocialIdentity() {
        val userInfo = userInfo()
        val member = memberService.loginOrSignup(SocialType.KAKAO, userInfo)

        TransactionTemplate(transactionManager).executeWithoutResult { transaction ->
            memberService.withdrawMember(member.id)
            assertThat(blocklist.isBlocked(member.id)).isFalse()
            transaction.setRollbackOnly()
        }

        assertThat(blocklist.isBlocked(member.id)).isFalse()
        assertThat(memberService.findActiveMember(member.id).id).isEqualTo(member.id)
        assertThat(memberService.loginOrSignup(SocialType.KAKAO, userInfo).id).isEqualTo(member.id)
    }

    @Test
    fun restartingRestoresOnlyWithdrawalsWithinTheTokenLifetime() {
        val recent = memberRepository.save(Member(name = "최근탈퇴", email = "recent@example.com"))
        val expired = memberRepository.save(Member(name = "이전탈퇴", email = "expired@example.com"))
        val active = memberRepository.save(Member(name = "활성회원", email = "active@example.com"))
        jdbcTemplate.update("UPDATE member SET deleted_at = ? WHERE id = ?", LocalDateTime.now().minusMinutes(5), recent.id)
        jdbcTemplate.update("UPDATE member SET deleted_at = ? WHERE id = ?", LocalDateTime.now().minusHours(1), expired.id)
        val restored = WithdrawnMemberTokenBlocklist(Duration.ofMinutes(10))

        MemberWithdrawalListener(restored, memberRepository, sseEmitterService).afterSingletonsInstantiated()

        assertThat(restored.isBlocked(recent.id)).isTrue()
        assertThat(restored.isBlocked(expired.id)).isFalse()
        assertThat(restored.isBlocked(active.id)).isFalse()
    }

    @Test
    fun socialIdentityIsUniqueWithinEachProvider() {
        val userInfo = userInfo()
        val kakaoMember = memberService.loginOrSignup(SocialType.KAKAO, userInfo)
        val googleMember = memberService.loginOrSignup(SocialType.GOOGLE, userInfo)
        assertThat(googleMember.id).isNotEqualTo(kakaoMember.id)

        assertThrows<DataIntegrityViolationException> {
            socialLoginRepository.saveAndFlush(SocialLogin(googleMember, SocialType.KAKAO, userInfo.getSocialId()))
        }
    }

    private fun userInfo(): OAuthUserInfo =
        object : OAuthUserInfo {
            private val socialId = UUID.randomUUID().toString()

            override fun getSocialId(): String = socialId

            override fun getName(): String = "테스트회원"

            override fun getEmail(): String = "rejoin@example.com"

            override fun getProfileImage(): String = ""

            override fun getBirthdate() = null
        }
}
