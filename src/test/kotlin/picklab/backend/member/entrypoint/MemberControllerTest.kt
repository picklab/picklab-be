package picklab.backend.member.entrypoint

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.*
import org.springframework.web.bind.MethodArgumentNotValidException
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.member.application.MemberUseCase
import picklab.backend.member.entrypoint.request.*

@WebMvcTest(MemberController::class)
class MemberControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @MockitoBean
    lateinit var memberUseCase: MemberUseCase

    @Nested
    @WithMockUser
    @DisplayName("회원 추가 정보 기입")
    inner class InsertMemberAdditionalInfo {
        @Test
        @DisplayName("[성공] 회원 추가 정보를 기입한다.")
        fun success() {
            // given
            val given = mapper.createObjectNode()
            given.put("nickname", "테스트유저")
            given.put("education_level", "대학교(4년)")
            given.put("school", "테스트 대학교")
            given.put("graduation_status", "졸업")
            given
                .putArray("interested_job_categories")
                .addObject()
                .put("group", "PLANNING")
                .put("detail", "SERVICE_PLANNING")
                .withArray("interested_job_categories")
                .addObject()
                .put("group", "DEVELOPMENT")
                .put("detail", "BACKEND")

            // when
            mockMvc
                .post("/v1/members/signup/additional-info") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(given)
                    with(csrf())
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.SIGNUP_SUCCESS.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.SIGNUP_SUCCESS.message) } }
        }

        @Test
        @DisplayName("[실패] 닉네임이 20자를 초과할 경우 MethodArgumentNotValidException 예외가 발생한다.")
        fun failWithValidation() {
            // given
            val given = mapper.createObjectNode()
            given.put("nickname", "테스트유저12345678901234567890")
            given.put("education_level", "대학교(4년)")
            given.put("school", "테스트 대학교")
            given.put("graduation_status", "졸업")
            given
                .putArray("interested_job_categories")
                .addObject()
                .put("group", "PLANNING")
                .put("detail", "SERVICE_PLANNING")
                .withArray("interested_job_categories")
                .addObject()
                .put("group", "DEVELOPMENT")
                .put("detail", "BACKEND")

            // when
            val result =
                mockMvc
                    .post("/v1/members/signup/additional-info") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(given)
                        with(csrf())
                    }.andExpect { status { isBadRequest() } }
                    .andExpect { jsonPath("$.code") { value(400) } }
                    .andExpect { jsonPath("$.message") { value("잘못된 요청입니다.") } }
                    .andReturn()

            assertTrue(result.resolvedException is MethodArgumentNotValidException)
        }

        @Test
        @DisplayName("[실패] 필수 정보가 누락되었을 경우 HttpMessageNotReadableException 예외가 발생한다.")
        fun failWithNull() {
            // given
            val given = mapper.createObjectNode()
            given.put("education_level", "대학교(4년)")
            given.put("school", "테스트 대학교")
            given.put("graduation_status", "졸업")
            given
                .putArray("interested_job_categories")
                .addObject()
                .put("group", "PLANNING")
                .put("detail", "SERVICE_PLANNING")
                .withArray("interested_job_categories")
                .addObject()
                .put("group", "DEVELOPMENT")
                .put("detail", "BACKEND")

            // when
            val result =
                mockMvc
                    .post("/v1/members/signup/additional-info") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(given)
                        with(csrf())
                    }.andExpect { status { isBadRequest() } }
                    .andExpect { jsonPath("$.code") { value(400) } }
                    .andExpect { jsonPath("$.message") { value("잘못된 요청입니다.") } }
                    .andReturn()

            assertTrue(result.resolvedException is HttpMessageNotReadableException)
        }

        @Test
        @DisplayName("[실패] 필수 정보가 공백일 경우 MethodArgumentNotValidException 예외가 발생한다.")
        fun failWithBlank() {
            // given
            val given = mapper.createObjectNode()
            given.put("nickname", "")
            given.put("education_level", "대학교(4년)")
            given.put("school", "테스트 대학교")
            given.put("graduation_status", "졸업")
            given
                .putArray("interested_job_categories")
                .addObject()
                .put("group", "PLANNING")
                .put("detail", "SERVICE_PLANNING")
                .withArray("interested_job_categories")
                .addObject()
                .put("group", "DEVELOPMENT")
                .put("detail", "BACKEND")

            // when
            val result =
                mockMvc
                    .post("/v1/members/signup/additional-info") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(given)
                        with(csrf())
                    }.andExpect { status { isBadRequest() } }
                    .andExpect { jsonPath("$.code") { value(400) } }
                    .andExpect { jsonPath("$.message") { value("잘못된 요청입니다.") } }
                    .andReturn()

            assertTrue(result.resolvedException is MethodArgumentNotValidException)
        }
    }

    @Nested
    @WithMockUser
    @DisplayName("회원 정보 수정")
    inner class UpdateMemberInfo {
        @Test
        @DisplayName("[성공] 회원 정보를 수정한다.")
        fun success() {
            // given
            val given = mapper.createObjectNode()
            given.put("name", "테스트")
            given.put("nickname", "수정된테스트유저")
            given.put("education_level", "대학교(4년)")
            given.put("school", "수정대학교")
            given.put("graduation_status", "졸업")

            // when
            mockMvc
                .put("/v1/members/info") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(given)
                    with(csrf())
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.MEMBER_INFO_UPDATED.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.MEMBER_INFO_UPDATED.message) } }
        }
    }

    @Nested
    @WithMockUser
    @DisplayName("회원 관심 직무 수정")
    inner class UpdateJobCategories {
        @Test
        @DisplayName("[성공] 회원의 관심 직무를 수정한다.")
        fun success() {
            // given
            val given = mapper.createObjectNode()

            given
                .putArray("interested_job_categories")
                .addObject()
                .put("group", "PLANNING")
                .put("detail", "SERVICE_PLANNING")
                .withArray("interested_job_categories")
                .addObject()
                .put("group", "DEVELOPMENT")
                .put("detail", "BACKEND")

            // when
            mockMvc
                .put("/v1/members/job-categories") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(given)
                    with(csrf())
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.MEMBER_JOB_CATEGORY_UPDATED.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.MEMBER_JOB_CATEGORY_UPDATED.message) } }
        }
    }

    @Nested
    @WithMockUser
    @DisplayName("회원 프로필 이미지 수정")
    inner class UpdateProfileImage {
        @Test
        @DisplayName("[성공] 회원의 프로필 이미지를 수정한다.")
        fun success() {
            // given
            val given = mapper.createObjectNode()
            given.put("profile_image", "https://example.com/profile.jpg")

            // when
            mockMvc
                .put("/v1/members/profile-image") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(given)
                    with(csrf())
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.MEMBER_PROFILE_IMAGE_UPDATED.status.value()) } }
                .andExpect {
                    jsonPath("$.message") { value(SuccessCode.MEMBER_PROFILE_IMAGE_UPDATED.message) }
                }
        }
    }

    @Nested
    @WithMockUser
    @DisplayName("회원 이메일 변경")
    inner class UpdateEmail {
        @Test
        @DisplayName("[성공] 회원의 이메일을 변경한다.")
        fun success() {
            // given
            val given = mapper.createObjectNode()
            given.put("email", "test@example.com")

            // when
            mockMvc
                .post("/v1/members/email") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(given)
                    with(csrf())
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.MEMBER_EMAIL_UPDATED.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.MEMBER_EMAIL_UPDATED.message) } }
        }

        @Test
        @DisplayName("[실패] 이메일 형식이 잘못된 경우 MethodArgumentNotValidException 예외가 발생한다.")
        fun failWithInvalidEmailFormat() {
            // given
            val given = mapper.createObjectNode()
            given.put("email", "invalid")

            // when
            val result =
                mockMvc
                    .post("/v1/members/email") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(given)
                        with(csrf())
                    }.andExpect { status { isBadRequest() } }
                    .andExpect { jsonPath("$.code") { value(400) } }
                    .andExpect { jsonPath("$.message") { value("잘못된 요청입니다.") } }
                    .andReturn()

            assertTrue(result.resolvedException is MethodArgumentNotValidException)
        }

        @Test
        @DisplayName("[실패] 이메일이 비어있는 경우 MethodArgumentNotValidException 예외가 발생한다.")
        fun failWithEmptyEmail() {
            // given
            val given = mapper.createObjectNode()
            given.put("email", "")

            // when
            val result =
                mockMvc
                    .post("/v1/members/email") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(given)
                        with(csrf())
                    }.andExpect { status { isBadRequest() } }
                    .andExpect { jsonPath("$.code") { value(400) } }
                    .andExpect { jsonPath("$.message") { value("잘못된 요청입니다.") } }
                    .andReturn()

            assertTrue(result.resolvedException is MethodArgumentNotValidException)
        }

        @Test
        @DisplayName("[실패] 이메일이 필드가 없는 경우 HttpMessageNotReadableException 예외가 발생한다.")
        fun failWithMissingEmailField() {
            // given
            val given = mapper.createObjectNode()

            // when
            val result =
                mockMvc
                    .post("/v1/members/email") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(given)
                        with(csrf())
                    }.andExpect { status { isBadRequest() } }
                    .andExpect { jsonPath("$.code") { value(400) } }
                    .andExpect { jsonPath("$.message") { value("잘못된 요청입니다.") } }
                    .andReturn()

            assertTrue(result.resolvedException is HttpMessageNotReadableException)
        }
    }

    @Nested
    @WithMockUser
    @DisplayName("이메일 인증 코드 확인")
    inner class CheckEmailCode {
        @Test
        @DisplayName("[성공] 이메일 인증 코드를 확인한다.")
        fun success() {
            // given
            val given = mapper.createObjectNode()
            given.put("code", "123123")

            // when
            mockMvc
                .post("/v1/members/email/code/verify") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(given)
                    with(csrf())
                }.andExpect { status { isOk() } }
                .andExpect {
                    jsonPath("$.code") { value(SuccessCode.VERIFY_EMAIL_CODE.status.value()) }
                }.andExpect { jsonPath("$.message") { value(SuccessCode.VERIFY_EMAIL_CODE.message) } }
        }

        @Test
        @DisplayName("[실패] 인증 코드에 문자가 포함된 경우 MethodArgumentNotValidException 예외가 발생한다.")
        fun failWithInvalidCode() {
            // given
            val given = mapper.createObjectNode()
            given.put("code", "123abc")

            // when
            val result =
                mockMvc
                    .post("/v1/members/email/code/verify") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(given)
                        with(csrf())
                    }.andExpect { status { isBadRequest() } }
                    .andExpect { jsonPath("$.code") { value(400) } }
                    .andExpect { jsonPath("$.message") { value("잘못된 요청입니다.") } }
                    .andReturn()

            assertTrue(result.resolvedException is MethodArgumentNotValidException)
        }

        @Test
        @DisplayName("[실패] 인증 코드가 6자리를 넘어갈 경우 MethodArgumentNotValidException 예외가 발생한다.")
        fun failWithTooLongCode() {
            // given
            val given = mapper.createObjectNode()
            given.put("code", "1234567")

            // when
            val result =
                mockMvc
                    .post("/v1/members/email/code/verify") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(given)
                        with(csrf())
                    }.andExpect { status { isBadRequest() } }
                    .andExpect { jsonPath("$.code") { value(400) } }
                    .andExpect { jsonPath("$.message") { value("잘못된 요청입니다.") } }
                    .andReturn()

            assertTrue(result.resolvedException is MethodArgumentNotValidException)
        }
    }

    @Nested
    @WithMockUser
    @DisplayName("이메일 수신 동의 여부 수정")
    inner class UpdateEmailAgreement {
        @Test
        @DisplayName("[성공] 이메일 수신 동의 여부를 수정한다.")
        fun success() {
            // given
            val given = mapper.createObjectNode()
            given.put("email_agreement", true)
            // when
            mockMvc
                .patch("/v1/members/email-agreement") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(given)
                    with(csrf())
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.UPDATE_EMAIL_AGREEMENT.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.UPDATE_EMAIL_AGREEMENT.message) } }
        }
    }

    @Nested
    @WithMockUser
    @DisplayName("회원 소셜 로그인 정보 조회")
    inner class GetSocialLoginInfo {
        @Test
        @DisplayName("[성공] 회원의 소셜 로그인 정보를 조회한다.")
        fun success() {
            // when
            mockMvc
                .get("/v1/members/social-logins") {
                    with(csrf())
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.GET_MEMBER_SOCIAL_LOGINS.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.GET_MEMBER_SOCIAL_LOGINS.message) } }
        }
    }

    @Nested
    @WithMockUser
    @DisplayName("회원 탈퇴")
    inner class WithdrawMember {
        @Test
        @DisplayName("[성공] 회원을 탈퇴한다.")
        fun success() {
            // when
            mockMvc
                .delete("/v1/members") {
                    with(csrf())
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.MEMBER_WITHDRAW.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.MEMBER_WITHDRAW.message) } }
        }
    }

    @Nested
    @WithMockUser
    @DisplayName("탈퇴 설문 제출")
    inner class SubmitSurvey {
        @Test
        @DisplayName("[성공] 탈퇴 설문을 제출한다.")
        fun success() {
            // given
            val given = mapper.createObjectNode()
            given.put("reason", "LACK_OF_CONTENT")

            // when
            mockMvc
                .post("/v1/members/withdrawal-survey") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(given)
                    with(csrf())
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.SUBMIT_SURVEY.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.SUBMIT_SURVEY.message) } }
        }

        @Test
        @DisplayName("[실패] 상세 사유가 500자를 초과할 경우 MethodArgumentNotValidException 예외가 발생한다.")
        fun failWithDetailTooLong() {
            // given
            val given = mapper.createObjectNode()
            given.put("reason", "ETC")
            given.put("detail", "a".repeat(501))

            // when
            val result =
                mockMvc
                    .post("/v1/members/withdrawal-survey") {
                        contentType = MediaType.APPLICATION_JSON
                        content = mapper.writeValueAsString(given)
                        with(csrf())
                    }.andExpect { status { isBadRequest() } }
                    .andExpect { jsonPath("$.code") { value(400) } }
                    .andExpect { jsonPath("$.message") { value("잘못된 요청입니다.") } }
                    .andReturn()

            assertTrue(result.resolvedException is MethodArgumentNotValidException)
        }
    }

    @Nested
    @WithMockUser
    @DisplayName("알림 설정 토글")
    inner class ToggleNotification {
        @Test
        @DisplayName("[성공] 알림 설정을 토글한다.")
        fun success() {
            // given
            val given = mapper.createObjectNode()
            given.put("type", "POPULAR")

            // when
            mockMvc
                .patch("/v1/members/notifications") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(given)
                    with(csrf())
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.MEMBER_NOTIFICATION_UPDATED.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.MEMBER_NOTIFICATION_UPDATED.message) } }
        }
    }
}
