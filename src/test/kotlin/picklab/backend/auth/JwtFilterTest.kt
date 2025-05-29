package picklab.backend.auth

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockCookie
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import org.springframework.test.web.servlet.get
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.helper.extractBody
import picklab.backend.job.template.IntegrationTest

class JwtFilterTest : IntegrationTest() {
    companion object {
        private const val EXPIRED_ACCESS_TOKEN =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwaWNrbGFiIiwic3ViIjoiMSIsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTc0ODUyNTczMiwiZXhwIjoxNzQ4NTI1NzM0fQ.K4OSbGXHfgVYErbg0toly5owhHkyvlbsKRIGZn1kZvQ"
    }

    @Test
    @DisplayName("[실패] Access Token이 만료되면 TOKEN_EXPIRED 에러가 발생한다.")
    fun failWithInvalidTokenTest() {
        val accessToken = EXPIRED_ACCESS_TOKEN

        val result =
            mockMvc
                .get("/v1/auth/callback/google?code=12345678") {
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
                .get("/v1/auth/callback/google?code=12345678") {
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
