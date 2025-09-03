package picklab.backend.review.infrastructure.query.projection

import com.querydsl.core.annotations.QueryProjection
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.participation.domain.enums.ProgressStatus
import picklab.backend.review.application.query.model.ActivityReviewListView
import java.time.LocalDateTime

@QueryProjection
data class ActivityReviewListItem(
    override val id: Long,
    override val overallScore: Int,
    override val infoScore: Int,
    override val difficultyScore: Int,
    override val benefitScore: Int,
    override val jobGroup: JobGroup,
    override val jobDetail: JobDetail?,
    override val participationDate: LocalDateTime,
    override val progressStatus: ProgressStatus,
    override val summary: String,
    override val strength: String,
    override val weakness: String,
    override val tips: String,
) : ActivityReviewListView
