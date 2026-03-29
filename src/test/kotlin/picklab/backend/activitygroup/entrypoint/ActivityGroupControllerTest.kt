package picklab.backend.activitygroup.entrypoint

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import picklab.backend.activitygroup.domain.repository.ActivityGroupRepository
import picklab.backend.activitygroup.entrypoint.request.ActivityGroupCreateRequest
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.template.IntegrationTest

@WithMockUser
class ActivityGroupControllerTest : IntegrationTest() {
    @Autowired
    lateinit var activityGroupRepository: ActivityGroupRepository

    @BeforeEach
    fun setUp() {
        cleanUp.all()
    }

    @Nested
    @DisplayName("활동 그룹 생성")
    inner class CreateActivityGroup {
        @Test
        @DisplayName("[성공] 활동 그룹을 생성한다.")
        fun success() {
            val request =
                ActivityGroupCreateRequest(
                    name = "대외활동",
                    description = "대외활동 관련 그룹입니다.",
                )

            mockMvc
                .post("/v1/activity-groups") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(request)
                }.andExpect { status { isOk() } }
                .andExpect { jsonPath("$.code") { value(SuccessCode.CREATE_ACTIVITY_GROUP.status.value()) } }
                .andExpect { jsonPath("$.message") { value(SuccessCode.CREATE_ACTIVITY_GROUP.message) } }

            val saved = activityGroupRepository.findAll()
            assertThat(saved).hasSize(1)
            assertThat(saved[0].name).isEqualTo(request.name)
            assertThat(saved[0].description).isEqualTo(request.description)
        }
    }
}
