package picklab.backend.member.application.model

import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import java.time.LocalDate

data class MemberMeResult(
    val name: String,
    val nickname: String,
    val educationLevel: String,
    val birthDate: LocalDate?,
    val selectedInterestedJobs: List<JobDetail>,
    val jobFields: List<JobGroup>,
    val employmentStatus: String,
    val company: String,
)
