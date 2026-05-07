package picklab.backend.activity.application.model

import picklab.backend.activity.domain.enums.RecruitmentEndType
import java.time.LocalDate

data class ActivityItemWithBookmark(
    val id: Long,
    val title: String,
    val organization: String,
    val startDate: LocalDate,
    val category: String,
    val jobTags: List<String>,
    val thumbnailUrl: String?,
    val viewCount: Long,
    val recruitmentEndType: RecruitmentEndType,
    val dDay: Long?,
    val isBookmarked: Boolean,
)
