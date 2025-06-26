package picklab.backend.activity.application.model

import java.time.LocalDate

class ActivityItemWithBookmark(
    val id: Long,
    val title: String,
    val organization: String,
    val startDate: LocalDate,
    val category: String,
    val jobTags: List<String>,
    val thumbnailUrl: String?,
    val isBookmarked: Boolean,
) {
    companion object {
        fun from(
            item: ActivityItem,
            isBookmarked: Boolean,
        ): ActivityItemWithBookmark =
            ActivityItemWithBookmark(
                id = item.id,
                title = item.title,
                organization = item.organization,
                startDate = item.startDate,
                category = item.category,
                jobTags = item.jobTags,
                thumbnailUrl = item.thumbnailUrl,
                isBookmarked = isBookmarked,
            )
    }
}
