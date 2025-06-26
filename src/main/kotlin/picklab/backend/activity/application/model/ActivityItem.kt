package picklab.backend.activity.application.model

import java.time.LocalDate

data class ActivityItem(
    val id: Long,
    val title: String,
    val organization: String,
    val startDate: LocalDate,
    val category: String,
    val jobTags: List<String>,
    val thumbnailUrl: String?,
)
