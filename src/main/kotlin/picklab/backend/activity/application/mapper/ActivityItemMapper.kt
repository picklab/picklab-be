package picklab.backend.activity.application.mapper

import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.application.model.ActivityView

fun ActivityView.withBookmark(isBookmarked: Boolean) =
    ActivityItemWithBookmark(
        id,
        title,
        organization,
        startDate,
        category,
        jobTags,
        thumbnailUrl,
        isBookmarked,
    )
