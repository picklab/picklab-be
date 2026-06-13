package picklab.backend.member.entrypoint

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.member.application.MemberUseCase
import picklab.backend.member.application.model.MemberMeResult
import java.time.LocalDate

@WebMvcTest(MemberController::class)
class MemberMeControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var memberUseCase: MemberUseCase

    @Test
    @WithMockUser
    @DisplayName("GET /v1/members/me는 필요한 필드를 한 번에 반환한다")
    fun getMemberMe() {
        given(memberUseCase.getMemberMe(1L)).willReturn(
            MemberMeResult(
                name = "hong",
                nickname = "picklab_member",
                educationLevel = "대학교(4년)",
                birthDate = LocalDate.of(1998, 3, 1),
                selectedInterestedJobs = listOf(JobDetail.BACKEND, JobDetail.DEVOPS),
                jobFields = listOf(JobGroup.DEVELOPMENT),
                employmentStatus = "재직 중",
                company = "Picklab",
                emailAgreement = true,
                notifyPopularActivity = true,
                notifyBookmarkedActivity = false,
            ),
        )

        mockMvc
            .get("/v1/members/me") {
                with(csrf())
            }.andExpect { status { isOk() } }
            .andExpect { jsonPath("$.code") { value(SuccessCode.GET_MEMBER_ME.status.value()) } }
            .andExpect { jsonPath("$.message") { value(SuccessCode.GET_MEMBER_ME.message) } }
            .andExpect { jsonPath("$.data.name") { value("hong") } }
            .andExpect { jsonPath("$.data.nickname") { value("picklab_member") } }
            .andExpect { jsonPath("$.data.education_level") { value("대학교(4년)") } }
            .andExpect { jsonPath("$.data.birth_date") { value("1998-03-01") } }
            .andExpect { jsonPath("$.data.selected_interested_jobs[0]") { value("BACKEND") } }
            .andExpect { jsonPath("$.data.selected_interested_jobs[1]") { value("DEVOPS") } }
            .andExpect { jsonPath("$.data.job_fields[0]") { value("DEVELOPMENT") } }
            .andExpect { jsonPath("$.data.employment.employment_status") { value("재직 중") } }
            .andExpect { jsonPath("$.data.employment.company") { value("Picklab") } }
            .andExpect { jsonPath("$.data.email_agreement") { value(true) } }
            .andExpect { jsonPath("$.data.notification_preferences.popular") { value(true) } }
            .andExpect { jsonPath("$.data.notification_preferences.bookmarked") { value(false) } }
    }
}
