package picklab.backend.review.infrastructure.query.projection

import com.querydsl.core.annotations.QueryProjection
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.review.application.query.model.SatisfactionStatisticsView

@QueryProjection
data class SatisfactionStatisticsItem(
    override val jobGroup: JobGroup?,
    override val jobDetail: JobDetail?,
    override val avgTotalScore: Double,
    override val avgJobExperienceScore: Double,
    override val avgActivityIntensityScore: Double,
    override val avgBenefitScore: Double,
) : SatisfactionStatisticsView
