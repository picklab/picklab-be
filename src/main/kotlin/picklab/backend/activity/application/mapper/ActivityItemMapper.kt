package picklab.backend.activity.application.mapper

import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.application.model.ActivityView
import picklab.backend.activity.domain.enums.RecruitmentEndType
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun ActivityView.withBookmark(isBookmarked: Boolean): ActivityItemWithBookmark {
    val today = LocalDate.now()
    val dDay =
        if (recruitmentEndType == RecruitmentEndType.FIXED) {
            recruitmentEndDate?.let { ChronoUnit.DAYS.between(today, it) }
        } else {
            null
        }
    return ActivityItemWithBookmark(
        id,
        title,
        organization,
        startDate,
        category,
        jobTags,
        thumbnailUrl,
        viewCount,
        recruitmentEndType,
        dDay,
        isBookmarked,
    )
}
