package picklab.backend.review.application.query.model

import com.querydsl.core.annotations.QueryProjection
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup

data class SatisfactionStatisticsItem
    @QueryProjection
    constructor(
        val jobGroup: JobGroup?,
        val jobDetail: JobDetail?,
        val avgTotalScore: Double,
        val avgJobExperienceScore: Double,
        val avgActivityIntensityScore: Double,
        val avgBenefitScore: Double,
    )
