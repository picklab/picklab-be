package picklab.backend.activity.application.mapper

import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.application.model.ActivityView
import picklab.backend.activity.application.model.BookmarkedActivityItem
import picklab.backend.activity.application.model.BookmarkedActivityView
import picklab.backend.activity.domain.enums.RecruitmentEndType
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun ActivityView.withBookmark(isBookmarked: Boolean): ActivityItemWithBookmark {
    val dDay =
        recruitmentEndDate
            ?.takeIf { recruitmentEndType == RecruitmentEndType.FIXED }
            ?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }

    return ActivityItemWithBookmark(
        id,
        title,
        organization,
        organizerType,
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

fun BookmarkedActivityView.toBookmarkedActivityItem(): BookmarkedActivityItem {
    val dDay =
        recruitmentEndDate
            ?.takeIf { recruitmentEndType == RecruitmentEndType.FIXED }
            ?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }

    return BookmarkedActivityItem(
        id = id,
        title = title,
        organization = organization,
        organizerType = organizerType,
        startDate = startDate,
        recruitmentStartDate = recruitmentStartDate,
        recruitmentEndDate = recruitmentEndDate,
        category = category,
        jobTags = jobTags,
        thumbnailUrl = thumbnailUrl,
        viewCount = viewCount,
        recruitmentEndType = recruitmentEndType,
        dDay = dDay,
        isBookmarked = true,
        bookmarkedAt = bookmarkedAt.toLocalDate(),
    )
}
