package picklab.backend.activity.entrypoint.response

import org.springframework.data.domain.Page
import picklab.backend.activity.application.model.ActivityItemWithBookmark

data class GetActivityListResponse(
    val items: List<ActivityItemWithBookmark>,
    val page: Int,
    val size: Int,
    val totalPages: Int,
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
