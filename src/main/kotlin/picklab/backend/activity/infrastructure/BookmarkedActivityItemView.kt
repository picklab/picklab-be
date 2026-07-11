package picklab.backend.activity.infrastructure

import com.querydsl.core.annotations.QueryProjection
import picklab.backend.activity.application.model.BookmarkedActivityView
import picklab.backend.activity.domain.enums.RecruitmentEndType
import java.time.LocalDate
import java.time.LocalDateTime

@QueryProjection
data class BookmarkedActivityItemView(
    override val id: Long,
    override val title: String,
    override val organization: String?,
    override val organizerType: String,
    override val startDate: LocalDate,
    override val recruitmentStartDate: LocalDate,
    override val category: String,
    override val jobTags: List<String>,
    override val thumbnailUrl: String?,
    override val viewCount: Long,
    override val recruitmentEndDate: LocalDate?,
    override val recruitmentEndType: RecruitmentEndType,
    override val bookmarkedAt: LocalDateTime,
) : BookmarkedActivityView
