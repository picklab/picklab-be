package picklab.backend.member.service

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import picklab.backend.auth.domain.OAuthUserInfo
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.job.domain.JobCategoryRepository
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.member.domain.MemberService
import picklab.backend.member.domain.entity.*
import picklab.backend.member.domain.enums.EmploymentType
import picklab.backend.member.domain.enums.NotificationType
import picklab.backend.member.domain.enums.SocialType
import picklab.backend.member.domain.enums.WithdrawalType
import picklab.backend.member.domain.repository.*
import picklab.backend.member.entrypoint.request.*
import picklab.backend.template.IntegrationTest
import java.time.LocalDateTime

class MemberServiceTest : IntegrationTest() {
    @Autowired
    lateinit var memberService: MemberService

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var socialLoginRepository: SocialLoginRepository

    @Autowired
    lateinit var memberInterestedJobCategoryRepository: InterestedJobCategoryRepository

    @Autowired
    lateinit var jobCategoryRepository: JobCategoryRepository

    @Autowired
    lateinit var memberVerificationRepository: MemberVerificationRepository

    @Autowired
    lateinit var memberAgreementRepository: MemberAgreementRepository

    @Autowired
    lateinit var notificationPreferenceRepository: NotificationPreferenceRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @BeforeEach
    fun setUp() {
        cleanUp.all()
    }

    @Nested
    @DisplayName("회원가입/로그인 서비스")
    inner class LoginOrSignupTests {
        // given
        @Test
        @DisplayName("[성공] 새로운 소셜 로그인 유저의 경우 회원가입이 된다.")
        fun successNewSocialUserTest() {
            // given
            val socialType = SocialType.KAKAO
            val userInfo =
                object : OAuthUserInfo {
                    override fun getSocialId() = "kakao12345"

                    override fun getName() = "신규테스트유저"

                    override fun getEmail() = "testuser@example.com"

                    override fun getProfileImage() = "http://img"

                    override fun getBirthdate() = null
                }

            // when
            val member = memberService.loginOrSignup(socialType, userInfo)

            // then
            assertThat(member.name).isEqualTo("신규테스트유저")
            assertThat(member.email).isEqualTo("testuser@example.com")
        }

        @Test
        @DisplayName("[성공] 이미 등록된 소셜 유저라면 기존 회원을 반환한다.")
        fun successExistMember() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "기존유저",
                        email = "test@example.com",
                    ),
                )

            socialLoginRepository.save(
                SocialLogin(
                    socialType = SocialType.KAKAO,
                    socialId = "kakao12345",
                    member = member,
                ),
            )

            val socialType = SocialType.KAKAO
            val userInfo =
                object : OAuthUserInfo {
                    override fun getSocialId() = "kakao12345"

                    override fun getName() = "기존유저"

                    override fun getEmail() = "test@example.com"

                    override fun getProfileImage() = "http://testimg.com/profile.jpg"

                    override fun getBirthdate() = null
                }

            // when
            val existMember = memberService.loginOrSignup(socialType, userInfo)

            // then
            assertThat(existMember.id).isEqualTo(member.id)
        }
    }

    @Nested
    @DisplayName("리프레쉬 토큰 저장")
    inner class SaveRefreshTokenTests {
        @Test
        @DisplayName("[성공] 회원의 리프레쉬 토큰을 저장한다.")
        fun successSaveRefreshToken() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val refreshToken = "test-refresh-token"

            // when
            memberService.saveRefreshToken(member.id, refreshToken)

            // then
            val updatedMember = memberRepository.findById(member.id).orElse(null)
            assertThat(updatedMember).isNotNull
            assertThat(updatedMember.refreshToken).isEqualTo(refreshToken)
        }

        @Test
        @DisplayName("[실패] 활성화하지 않은 유저를 조회할 경우 BusinessException 을 발생시킨다.")
        fun failSaveRefreshToken() {
            // when
            val exception =
                assertThrows<BusinessException> {
                    memberService.saveRefreshToken(1, "test-refresh-token")
                }

            // then
            assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_MEMBER)
        }
    }

    @Nested
    @DisplayName("회원 추가 정보 입력")
    inner class InsertAdditionalInfoTests {
        @Test
        @DisplayName("[성공] 회원의 추가 정보를 입력한다.")
        fun successInsertAdditionalInfo() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val jobCategories =
                listOf(
                    JobCategoryDto("PLANNING", "SERVICE_PLANNING"),
                )

            val additionalInfo =
                AdditionalInfoRequest(
                    nickname = "테스트닉네임",
                    educationLevel = "대학교(4년)",
                    school = "테스트학교",
                    graduationStatus = "졸업",
                    employmentStatus = "재직 중",
                    company = "테스트기업",
                    employmentType = EmploymentType.FULL_TIME,
                    interestedJobCategories = jobCategories,
                )

            // when
            val infoMember = memberService.insertAdditionalInfo(member.id, additionalInfo)

            // then
            assertThat(infoMember.nickname).isEqualTo(additionalInfo.nickname)
            assertThat(infoMember.educationLevel).isEqualTo(additionalInfo.educationLevel)
            assertThat(infoMember.school).isEqualTo(additionalInfo.school)
            assertThat(infoMember.graduationStatus).isEqualTo(additionalInfo.graduationStatus)
            assertThat(infoMember.employmentStatus).isEqualTo(additionalInfo.employmentStatus)
            assertThat(infoMember.company).isEqualTo(additionalInfo.company)
            assertThat(infoMember.employmentType).isEqualTo(additionalInfo.employmentType)
        }
    }

    @Nested
    @DisplayName("관심 직군 등록")
    inner class RegisterInterestedJobCategoriesTests {
        @Test
        @DisplayName("[성공] 회원의 관심 직군을 등록한다.")
        fun successRegisterInterestedJobCategories() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val jobCategory1 =
                jobCategoryRepository.save(
                    JobCategory(
                        jobGroup = JobGroup.PLANNING,
                        jobDetail = JobDetail.SERVICE_PLANNING,
                    ),
                )

            val jobCategory2 =
                jobCategoryRepository.save(
                    JobCategory(
                        jobGroup = JobGroup.DEVELOPMENT,
                        jobDetail = JobDetail.BACKEND,
                    ),
                )

            val jobCategories =
                listOf(
                    jobCategory1,
                    jobCategory2,
                )

            // when
            memberService.registerInterestedJobCategories(member, jobCategories)

            // then
            val interestedCategories =
                entityManager
                    .createQuery(
                        "SELECT i FROM InterestedJobCategory i JOIN FETCH i.jobCategory WHERE i.member = :member",
                        InterestedJobCategory::class.java,
                    ).setParameter("member", member)
                    .resultList

            assertThat(interestedCategories).hasSize(2)
            assertThat(interestedCategories[0].jobCategory.jobGroup).isEqualTo(JobGroup.PLANNING)
            assertThat(interestedCategories[0].jobCategory.jobDetail).isEqualTo(JobDetail.SERVICE_PLANNING)
            assertThat(interestedCategories[1].jobCategory.jobGroup).isEqualTo(JobGroup.DEVELOPMENT)
            assertThat(interestedCategories[1].jobCategory.jobDetail).isEqualTo(JobDetail.BACKEND)
        }
    }

    @Nested
    @DisplayName("회원 정보 수정")
    inner class UpdateMemberInfoTests {
        @Test
        @DisplayName("[성공] 회원 정보를 수정한다.")
        fun successUpdateMemberInfo() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val updateRequest =
                UpdateInfoRequest(
                    name = "테스트",
                    nickname = "수정된닉네임",
                    educationLevel = "대학교(4년)",
                    school = "수정된학교",
                    graduationStatus = "졸업",
                    employmentStatus = "재직 중",
                    company = "수정된기업",
                    employmentType = EmploymentType.FULL_TIME,
                )

            // when
            memberService.updateMemberInfo(member.id, updateRequest)

            // then
            val updatedMember = memberRepository.findById(member.id).orElse(null)
            assertThat(updatedMember).isNotNull
            assertThat(updatedMember.name).isEqualTo(updateRequest.name)
            assertThat(updatedMember.nickname).isEqualTo(updateRequest.nickname)
            assertThat(updatedMember.educationLevel).isEqualTo(updateRequest.educationLevel)
            assertThat(updatedMember.school).isEqualTo(updateRequest.school)
            assertThat(updatedMember.graduationStatus).isEqualTo(updateRequest.graduationStatus)
            assertThat(updatedMember.employmentStatus).isEqualTo(updateRequest.employmentStatus)
            assertThat(updatedMember.company).isEqualTo(updateRequest.company)
            assertThat(updatedMember.employmentType).isEqualTo(updateRequest.employmentType)
        }
    }

    @Nested
    @DisplayName("회원 관심 직군 전체 삭제")
    inner class ClearInterestedJobCategoriesTests {
        @Test
        @DisplayName("[성공] 회원의 관심 직군을 전체 삭제한다.")
        fun successClearInterestedJobCategories() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val jobCategory1 =
                jobCategoryRepository.save(
                    JobCategory(
                        jobGroup = JobGroup.PLANNING,
                        jobDetail = JobDetail.SERVICE_PLANNING,
                    ),
                )

            val jobCategory2 =
                jobCategoryRepository.save(
                    JobCategory(
                        jobGroup = JobGroup.DEVELOPMENT,
                        jobDetail = JobDetail.BACKEND,
                    ),
                )

            memberInterestedJobCategoryRepository.saveAll(
                listOf(
                    InterestedJobCategory(member, jobCategory1),
                    InterestedJobCategory(member, jobCategory2),
                ),
            )

            // when
            memberService.clearInterestedJobCategories(member.id)

            // then
            val interestedCategories =
                entityManager
                    .createQuery(
                        "SELECT i FROM InterestedJobCategory i JOIN FETCH i.jobCategory WHERE i.member = :member",
                        InterestedJobCategory::class.java,
                    ).setParameter("member", member)
                    .resultList

            assertThat(interestedCategories).isEmpty()
        }
    }

    @Nested
    @DisplayName("회원 프로필 이미지 수정")
    inner class UpdateProfileImageTests {
        @Test
        @DisplayName("[성공] 회원의 프로필 이미지를 수정한다.")
        fun successUpdateProfileImage() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val request =
                UpdateProfileImageRequest(
                    profileImage = "https://example.com/profile.jpg",
                )

            // when
            memberService.updateProfileImage(member, request.profileImage)

            // then
            val updatedMember = memberRepository.findById(member.id).orElse(null)
            assertThat(updatedMember).isNotNull
            assertThat(updatedMember.profileImageUrl).isEqualTo(request.profileImage)
        }
    }

    @Nested
    @DisplayName("회원 이메일 수정")
    inner class UpdateEmailTests {
        @Test
        @DisplayName("[성공] 회원의 이메일을 수정한다.")
        fun successUpdateEmail() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val newEmail = "example@test.com"

            // when
            memberService.updateEmail(member.id, newEmail)

            // then
            val updatedMember = memberRepository.findById(member.id).orElse(null)
            assertThat(updatedMember).isNotNull
            assertThat(updatedMember.email).isEqualTo(newEmail)
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드 추가")
    inner class AddEmailVerificationCodeTests {
        @Test
        @DisplayName("[성공] 회원의 이메일 인증 코드를 추가한다.")
        fun successAddEmailVerificationCode() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val memberId = member.id
            val email = member.email
            val code = "123456"

            // when
            memberService.addEmailVerificationCode(memberId, email, code)

            // then
            val verificationCode =
                memberVerificationRepository.findByMemberIdAndEmailAndDeletedAtIsNull(
                    memberId = memberId,
                    email = email,
                )

            assertThat(verificationCode).isNotNull
            assertThat(verificationCode?.email).isEqualTo(email)
            assertThat(verificationCode?.code).isEqualTo(code)
            assertThat(verificationCode?.member?.id).isEqualTo(memberId)
        }
    }

    @Nested
    @DisplayName("회원이 이전에 이미 발급받은 인증코드가 있는지 조회")
    inner class FindVerificationCodeTests {
        @Test
        @DisplayName("[성공] 회원의 이메일 인증 코드를 조회한다.")
        fun successFindVerificationCode() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val memberId = member.id
            val email = member.email

            memberVerificationRepository.save(
                MemberVerification(
                    email = email,
                    code = "123456",
                    expiredAt = LocalDateTime.now().plusMinutes(5),
                    member = member,
                ),
            )

            // when
            val verificationCode =
                memberService.findVerificationCode(memberId, email)

            // then
            assertThat(verificationCode).isNotNull
            assertThat(verificationCode?.email).isEqualTo(email)
            assertThat(verificationCode?.code).isEqualTo("123456")
            assertThat(verificationCode?.member?.id).isEqualTo(memberId)
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드 무효화")
    inner class InvalidateEmailVerificationCodeTests {
        @Test
        @DisplayName("[성공] 회원의 이메일 인증 코드를 무효화하고 조회하면 BusinessException을 발생시킨다.")
        fun successInvalidateEmailVerificationCode() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )
            val memberId = member.id
            val email = member.email
            val code = "123456"
            val verification =
                memberVerificationRepository.save(
                    MemberVerification(
                        email = email,
                        code = code,
                        expiredAt = LocalDateTime.now().plusMinutes(5),
                        member = member,
                    ),
                )

            // when
            memberService.invalidateVerificationCode(verification)

            // then
            val exception =
                assertThrows<BusinessException> {
                    memberService.findVerification(memberId, code)
                }

            assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_VERIFICATION_CODE)
        }
    }

    @Nested
    @DisplayName("회원의 유효한 이메일 인증 코드 조회")
    inner class FindVerificationTests {
        @Test
        @DisplayName("[성공] 회원의 유효한 이메일 인증 코드를 조회한다.")
        fun successFindVerification() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val memberId = member.id
            val email = member.email
            val code = "123456"
            memberVerificationRepository.save(
                MemberVerification(
                    email = email,
                    code = code,
                    expiredAt = LocalDateTime.now().plusMinutes(5),
                    member = member,
                ),
            )

            // when
            val result =
                memberService.findVerification(memberId, code)

            // then
            assertThat(result).isNotNull
            assertThat(result.email).isEqualTo(email)
            assertThat(result.code).isEqualTo(code)
            assertThat(result.member.id).isEqualTo(memberId)
        }

        @Test
        @DisplayName("[실패] 유효한 이메일 인증 코드가 없다면 BusinessException 을 발생시킨다")
        fun failFindVerification() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val memberId = member.id
            val code = "10101"

            // when
            val exception =
                assertThrows<BusinessException> {
                    memberService.findVerification(memberId, code)
                }

            // then
            assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_VERIFICATION_CODE)
        }
    }

    @Nested
    @DisplayName("이메일 수신 동의 여부 변경")
    inner class UpdateEmailAgreementTests {
        @Test
        @DisplayName("[성공] 회원의 이메일 수신 동의 여부를 변경한다.")
        fun successUpdateEmailAgreement() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            memberAgreementRepository.save(
                MemberAgreement(
                    member = member,
                    emailAgreement = false,
                    privacyAgreement = true,
                ),
            )

            val memberId = member.id
            val emailAgreement = true

            // when
            memberService.updateEmailAgreement(memberId, emailAgreement)

            // then
            val updatedAgreement =
                memberAgreementRepository.findByMemberId(memberId)

            assertThat(updatedAgreement).isNotNull
            assertThat(updatedAgreement?.emailAgreement).isEqualTo(emailAgreement)
        }
    }

    @Nested
    @DisplayName("회원의 소셜 로그인 정보 조회")
    inner class GetSocialLoginsTests {
        @Test
        @DisplayName("[성공] 회원의 소셜 로그인 정보를 조회한다.")
        fun successGetSocialLogins() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            socialLoginRepository.save(
                SocialLogin(
                    socialType = SocialType.KAKAO,
                    socialId = "kakao12345",
                    member = member,
                ),
            )

            socialLoginRepository.save(
                SocialLogin(
                    socialType = SocialType.GOOGLE,
                    socialId = "google12345",
                    member = member,
                ),
            )

            // when
            val result = memberService.getSocialLogins(member.id)

            // then
            assertThat(result.loginType).containsExactlyInAnyOrder("KAKAO", "GOOGLE")
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    inner class WithdrawMemberTests {
        @Test
        @DisplayName("[성공] 회원을 탈퇴시킨다.")
        fun successWithdrawMember() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            // when
            memberService.withdrawMember(member.id)

            // then
            val withdrawnMember = memberRepository.findByIdIgnoreDelete(member.id)
            assertThat(withdrawnMember).isNotNull
            assertThat(withdrawnMember!!.deletedAt).isNotNull
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 설문 제출")
    inner class SubmitWithdrawalSurveyTests {
        @Test
        @DisplayName("[성공] 회원 탈퇴 설문을 제출한다.")
        fun successSubmitWithdrawalSurvey() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            val request =
                MemberWithdrawalRequest(
                    reason = WithdrawalType.LACK_OF_CONTENT,
                    detail = null,
                )

            // when
            memberService.submitWithdrawalSurvey(member.id, request)

            // then
            val withdrawal =
                entityManager
                    .createQuery(
                        "SELECT mw FROM MemberWithdrawal mw WHERE mw.memberId = :memberId",
                        MemberWithdrawal::class.java,
                    ).setParameter("memberId", member.id)
                    .singleResult

            assertThat(withdrawal).isNotNull
            assertThat(withdrawal.withdrawalReason).isEqualTo(request.reason)
            assertThat(withdrawal.withdrawalReasonDetail).isNull()
        }

        @Test
        @DisplayName("[실패] 활성화되어 있지 않은 유저의 경우 BusinessException 을 발생시킨다.")
        fun failSubmitWithdrawalSurvey() {
            // given
            val request =
                MemberWithdrawalRequest(
                    reason = WithdrawalType.LACK_OF_CONTENT,
                    detail = null,
                )

            // when
            val exception =
                assertThrows<BusinessException> {
                    memberService.submitWithdrawalSurvey(1L, request)
                }

            // then
            assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_MEMBER)
        }
    }

    @Nested
    @DisplayName("회원 알림 설정 토글")
    inner class ToggleMemberNotificationTests {
        @Test
        @DisplayName("[성공] 회원의 알림 설정을 토글한다.")
        fun successToggleMemberNotification() {
            // given
            val member =
                memberRepository.save(
                    Member(
                        name = "테스트유저",
                        email = "test@example.com",
                    ),
                )

            notificationPreferenceRepository.save(
                NotificationPreference(
                    member = member,
                    notifyPopularActivity = false,
                    notifyBookmarkedActivity = false,
                ),
            )

            val memberId = member.id
            val type = NotificationType.POPULAR

            // when
            memberService.toggleMemberNotification(memberId, type)

            // then
            val updatedPreference =
                notificationPreferenceRepository.findByMemberId(memberId)

            assertThat(updatedPreference).isNotNull
            assertThat(updatedPreference.notifyPopularActivity).isTrue
        }
    }

    @Nested
    @DisplayName("닉네임 중복 조회")
    inner class CheckNicknameDuplicateTests {
        @Test
        @DisplayName("[성공] 닉네임이 중복되지 않는 경우 false를 반환한다.")
        fun successCheckNicknameNotDuplicate() {
            // given

            memberRepository.save(
                Member(
                    name = "테스트유저",
                    email = "test@example.com",
                    nickname = "testNickname",
                ),
            )

            // when
            val result = memberService.existByNickname("newNickname")

            // then
            assertThat(result).isFalse
        }

        @Test
        @DisplayName("[성공] 닉네임이 중복되는 경우 true를 반환한다.")
        fun successCheckNicknameDuplicate() {
            // given

            memberRepository.save(
                Member(
                    name = "테스트유저",
                    email = "test@example.com",
                    nickname = "testNickname",
                ),
            )

            // when
            val result = memberService.existByNickname("testNickname")

            // then
            assertThat(result).isTrue
        }
    }
}
