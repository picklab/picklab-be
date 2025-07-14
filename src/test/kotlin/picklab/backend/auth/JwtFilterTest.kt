package picklab.backend.auth

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockCookie
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import org.springframework.test.web.servlet.get
import picklab.backend.auth.infrastructure.JwtTokenProvider
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.helper.extractBody
import picklab.backend.template.IntegrationTest

class JwtFilterTest : IntegrationTest() {
    @Autowired
    lateinit var accessTokenProvider: JwtTokenProvider

    companion object {
        private const val EXPIRED_ACCESS_TOKEN =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwaWNrbGFiIiwic3ViIjoiMSIsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTc0ODUyNTczMiwiZXhwIjoxNzQ4NTI1NzM0fQ.K4OSbGXHfgVYErbg0toly5owhHkyvlbsKRIGZn1kZvQ"
    }

    @Test
    @DisplayName("[성공] Access Token 이 존재하고, memberId을 반환한다.")
    fun successTest() {
        val memberId = 1L
        val accessToken = accessTokenProvider.generateToken(memberId)

        val result =
            mockMvc
                .get("/v1/test/auth/filter") {
                    cookie(MockCookie("accessToken", accessToken))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("성공")
                }.andReturn()

        val body: ResponseWrapper<Long> = result.extractBody(mapper)
        val got = body.data!!

        assertThat(got).isEqualTo(1L)
    }

    @Test
    @DisplayName("[실패] Access Token 쿠키가 존재하지 않으면, UNAUTHORIZED 에러가 발생한다.")
    fun failTest() {
        val result =
            mockMvc
                .get("/v1/test/auth/filter")
                .andExpect {
                    status { isUnauthorized() }
                    jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.status.value())
                    jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.message)
                }.andReturn()

        val body: ResponseWrapper<Unit> = result.extractBody(mapper)
        assertThat(body.code).isEqualTo(ErrorCode.UNAUTHORIZED.status.value())
        assertThat(body.message).isEqualTo(ErrorCode.UNAUTHORIZED.message)
    }

    @Test
    @DisplayName("[실패] Access Token이 만료되면 TOKEN_EXPIRED 에러가 발생한다.")
    fun failWithInvalidTokenTest() {
        val accessToken = EXPIRED_ACCESS_TOKEN

        val result =
            mockMvc
                .get("/v1/test/auth/filter") {
                    cookie(MockCookie("accessToken", accessToken))
                }.andExpect {
                    status { isUnauthorized() }
                    jsonPath("$.code").value(ErrorCode.TOKEN_EXPIRED.status.value())
                    jsonPath("$.message").value(ErrorCode.TOKEN_EXPIRED.message)
                }.andReturn()

        val body: ResponseWrapper<Unit> = result.extractBody(mapper)
        assertThat(body.code).isEqualTo(ErrorCode.TOKEN_EXPIRED.status.value())
        assertThat(body.message).isEqualTo(ErrorCode.TOKEN_EXPIRED.message)
    }

    @Test
    @DisplayName("[실패] Access Token이 잘못된 형식이면 INVALID_TOKEN 에러가 발생한다.")
    fun failWithMalformedTokenTest() {
        val accessToken = "jwt.token.string"

        val result =
            mockMvc
                .get("/v1/test/auth/filter") {
                    cookie(MockCookie("accessToken", accessToken))
                }.andExpect {
                    status { isUnauthorized() }
                    jsonPath("$.code").value(ErrorCode.INVALID_TOKEN.status.value())
                    jsonPath("$.message").value(ErrorCode.INVALID_TOKEN.message)
                }.andReturn()

        val body: ResponseWrapper<Unit> = result.extractBody(mapper)
        assertThat(body.code).isEqualTo(ErrorCode.INVALID_TOKEN.status.value())
        assertThat(body.message).isEqualTo(ErrorCode.INVALID_TOKEN.message)
    }
}
