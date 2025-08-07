package picklab.backend.review.application.query.model

import com.querydsl.core.annotations.QueryProjection
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.participation.domain.enums.ProgressStatus
import java.time.LocalDateTime

data class ActivityReviewListItem
    @QueryProjection
    constructor(
        val id: Long,
        val overallScore: Int,
        val infoScore: Int,
        val difficultyScore: Int,
        val benefitScore: Int,
        val jobGroup: JobGroup,
        val jobDetail: JobDetail?,
        val participationDate: LocalDateTime,
        val progressStatus: ProgressStatus,
        val summary: String,
        val strength: String,
        val weakness: String,
        val tips: String,
    )
