package picklab.backend.activity.infrastructure

import com.querydsl.core.annotations.QueryProjection
import picklab.backend.activity.application.model.ActivityView
import java.time.LocalDate

@QueryProjection
data class ActivityItem(
    override val id: Long,
    override val title: String,
    override val organization: String,
    override val startDate: LocalDate,
    override val category: String,
    override val jobTags: List<String>,
    override val thumbnailUrl: String?,
) : ActivityView
