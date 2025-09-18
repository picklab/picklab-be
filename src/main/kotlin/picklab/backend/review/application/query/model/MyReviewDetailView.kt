package picklab.backend.review.application.query.model

import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup

data class MyReviewDetailView(
    val jobGroup: JobGroup,
    val jobDetail: JobDetail?,
    val overallScore: Int,
    val infoScore: Int,
    val difficultyScore: Int,
    val benefitScore: Int,
    val jobRelevanceScore: Int,
    val summary: String,
    val strength: String,
    val weakness: String,
    val tips: String?,
    val url: String?,
)
