package picklab.backend.job.entrypoint

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import org.springframework.test.web.servlet.get
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.helper.extractBody
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.entrypoint.response.JobDetailResponse
import picklab.backend.job.entrypoint.response.JobResponse
import picklab.backend.template.IntegrationTest
import kotlin.test.Test

@WithMockUser
class JobControllerTest : IntegrationTest() {
    @Nested
    @DisplayName("직무 정보 조회")
    inner class FindAll {
        @Test
        @DisplayName("[성공] 모든 직무 정보를 조회한다.")
        fun successTest() {
            val want =
                JobDetail.entries
                    .groupBy { it.group }
                    .map { (group, details) ->
                        JobResponse(
                            group = group,
                            label = group.label,
                            details =
                                details.map {
                                    JobDetailResponse(code = it, label = it.label)
                                },
                        )
                    }

            val result =
                mockMvc
                    .get("/v1/jobs")
                    .andExpect { status { isOk() } }
                    .andExpect { jsonPath("$.code").value(SuccessCode.JOB_DETAILS_RETRIEVED.status) }
                    .andExpect { jsonPath("$.message").value(SuccessCode.JOB_DETAILS_RETRIEVED.message) }
                    .andReturn()
            val body: ResponseWrapper<List<JobResponse>> = result.extractBody(mapper)
            val got = body.data!!

            assertThat(got).containsExactlyInAnyOrderElementsOf(want)
        }
    }
}
