package picklab.backend.activitygroup.entrypoint

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import picklab.backend.activitygroup.domain.entity.ActivityGroup
import picklab.backend.activitygroup.domain.repository.ActivityGroupRepository
import picklab.backend.activitygroup.entrypoint.request.ActivityGroupCreateRequest
import picklab.backend.activitygroup.entrypoint.response.ActivityGroupResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.helper.extractBody
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

    @Nested
    @DisplayName("활동 그룹 목록 조회")
    inner class GetActivityGroups {
        @Test
        @DisplayName("[성공] 활동 그룹 목록을 조회한다.")
        fun success() {
            val first =
                activityGroupRepository.save(
                    ActivityGroup(
                        name = "대외활동",
                        description = "대외활동 관련 그룹입니다.",
                    ),
                )
            val second =
                activityGroupRepository.save(
                    ActivityGroup(
                        name = "교육",
                        description = "교육 관련 그룹입니다.",
                    ),
                )

            val result =
                mockMvc
                    .get("/v1/activity-groups")
                    .andExpect { status { isOk() } }
                    .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITY_GROUPS.status.value()) } }
                    .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITY_GROUPS.message) } }
                    .andReturn()

            val body: ResponseWrapper<List<ActivityGroupResponse>> = result.extractBody(mapper)
            val got = body.data!!

            assertThat(got).containsExactly(
                ActivityGroupResponse(
                    id = first.id,
                    name = first.name,
                    description = first.description,
                ),
                ActivityGroupResponse(
                    id = second.id,
                    name = second.name,
                    description = second.description,
                ),
            )
        }
    }
}
