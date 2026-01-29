package picklab.backend.auth

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import picklab.backend.auth.application.TokenUseCase
import picklab.backend.auth.infrastructure.AccessTokenProvider
import picklab.backend.auth.infrastructure.RefreshTokenProvider
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.MemberService
import picklab.backend.member.domain.entity.Member

@ExtendWith(MockKExtension::class)
class TokenUseCaseTest {
    @MockK
    lateinit var memberService: MemberService

    @MockK
    lateinit var accessTokenProvider: AccessTokenProvider

    @MockK
    lateinit var refreshTokenProvider: RefreshTokenProvider

    @InjectMockKs
    lateinit var tokenUseCase: TokenUseCase

    @Test
    @DisplayName("유효한 리프레시 토큰으로 액세스 토큰 재발급 성공")
    fun refreshAccessToken_withValidToken_shouldReturnNewAccessToken() {
        // given
        val validRefreshToken = "valid.refresh.token"
        val memberId = 1L
        val newAccessToken = "new.access.token"
        val mockMember =
            mockk<Member> {
                every { id } returns memberId
                every { refreshToken } returns validRefreshToken
            }

        every { refreshTokenProvider.validateToken(validRefreshToken) } returns true
        every { refreshTokenProvider.getTokenType(validRefreshToken) } returns "refresh"
        every { refreshTokenProvider.getSubject(validRefreshToken) } returns memberId.toString()
        every { memberService.findActiveMember(memberId) } returns mockMember
        every { accessTokenProvider.generateToken(memberId) } returns newAccessToken

        // when
        val result = tokenUseCase.refreshAccessToken(validRefreshToken)

        // then
        assertThat(result.accessToken).isEqualTo(newAccessToken)
        assertThat(result.refreshToken).isEqualTo(validRefreshToken)
        verify(exactly = 1) { refreshTokenProvider.validateToken(validRefreshToken) }
        verify(exactly = 1) { refreshTokenProvider.getTokenType(validRefreshToken) }
        verify(exactly = 1) { memberService.findActiveMember(memberId) }
        verify(exactly = 1) { accessTokenProvider.generateToken(memberId) }
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰으로 요청 시 INVALID_TOKEN 예외 발생")
    fun refreshAccessToken_withInvalidToken_shouldThrowException() {
        // given
        val invalidToken = "invalid.token"
        every { refreshTokenProvider.validateToken(invalidToken) } returns false

        // when
        val exception =
            assertThrows<BusinessException> {
                tokenUseCase.refreshAccessToken(invalidToken)
            }

        // then
        assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_TOKEN)
        verify(exactly = 1) { refreshTokenProvider.validateToken(invalidToken) }
        verify(exactly = 0) { memberService.findActiveMember(any()) }
    }

    @Test
    @DisplayName("액세스 토큰 타입으로 요청 시 INVALID_TOKEN 예외 발생")
    fun refreshAccessToken_withAccessTokenType_shouldThrowException() {
        // given
        val accessToken = "access.token"
        every { refreshTokenProvider.validateToken(accessToken) } returns true
        every { refreshTokenProvider.getTokenType(accessToken) } returns "access"

        // when
        val exception =
            assertThrows<BusinessException> {
                tokenUseCase.refreshAccessToken(accessToken)
            }

        // then
        assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_TOKEN)
        verify(exactly = 1) { refreshTokenProvider.validateToken(accessToken) }
        verify(exactly = 1) { refreshTokenProvider.getTokenType(accessToken) }
        verify(exactly = 0) { memberService.findActiveMember(any()) }
    }

    @Test
    @DisplayName("DB 토큰과 불일치하는 리프레시 토큰으로 요청 시 INVALID_TOKEN 예외 발생")
    fun refreshAccessToken_withMismatchedToken_shouldThrowException() {
        // given
        val requestToken = "request.token"
        val dbToken = "db.token"
        val memberId = 1L
        val mockMember =
            mockk<Member> {
                every { id } returns memberId
                every { refreshToken } returns dbToken
            }

        every { refreshTokenProvider.validateToken(requestToken) } returns true
        every { refreshTokenProvider.getTokenType(requestToken) } returns "refresh"
        every { refreshTokenProvider.getSubject(requestToken) } returns memberId.toString()
        every { memberService.findActiveMember(memberId) } returns mockMember

        // when
        val exception =
            assertThrows<BusinessException> {
                tokenUseCase.refreshAccessToken(requestToken)
            }

        // then
        assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_TOKEN)
        verify(exactly = 1) { refreshTokenProvider.validateToken(requestToken) }
        verify(exactly = 1) { memberService.findActiveMember(memberId) }
        verify(exactly = 0) { accessTokenProvider.generateToken(any()) }
    }
}
