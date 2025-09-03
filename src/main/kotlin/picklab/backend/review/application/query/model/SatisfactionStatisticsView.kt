package picklab.backend.review.application.query.model

import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup

interface SatisfactionStatisticsView {
    val jobGroup: JobGroup?
    val jobDetail: JobDetail?
    val avgTotalScore: Double
    val avgJobExperienceScore: Double
    val avgActivityIntensityScore: Double
    val avgBenefitScore: Double
}
