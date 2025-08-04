package picklab.backend.review.application.model

import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.participation.domain.enums.ProgressStatus

data class ActivityReviewListQueryRequest(
    val rating: Int?,
    val jobGroup: List<JobGroup>?,
    val jobDetail: List<JobDetail>?,
    val status: ProgressStatus?,
)
