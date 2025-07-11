package picklab.backend.activity.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page
import picklab.backend.activity.application.model.ActivityItemWithBookmark

data class GetActivityListResponse(
    @field:Schema(description = "요소 정보")
    val items: List<ActivityItemWithBookmark>,
    @field:Schema(description = "페이지 번호")
    val page: Int,
    @field:Schema(description = "페이지 크기")
    val size: Int,
    @field:Schema(description = "페이지 총 번호")
    val totalPages: Int,
    @field:Schema(description = "전체 요소 개수")
    val totalElements: Long,
) {
    companion object {
        fun from(
            activityPage: Page<*>,
            items: List<ActivityItemWithBookmark>,
        ): GetActivityListResponse =
            GetActivityListResponse(
                items = items,
                page = activityPage.number + 1,
                size = activityPage.size,
                totalPages = activityPage.totalPages,
                totalElements = activityPage.totalElements,
            )
    }
}
