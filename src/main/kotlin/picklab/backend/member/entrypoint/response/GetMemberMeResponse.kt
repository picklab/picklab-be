package picklab.backend.member.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import java.time.LocalDate

data class GetMemberMeResponse(
    @field:Schema(description = "이름")
    val name: String,
    @field:Schema(description = "닉네임")
    val nickname: String,
    @field:Schema(description = "최종 학력")
    val educationLevel: String,
    @field:Schema(description = "생년월일")
    val birthDate: LocalDate?,
    @field:Schema(description = "선택한 관심 직무")
    val selectedInterestedJobs: List<JobDetail>,
    @field:Schema(description = "직무 분야")
    val jobFields: List<JobGroup>,
    @field:Schema(description = "재직 상태")
    val employment: EmploymentInfoResponse,
)

data class EmploymentInfoResponse(
    @field:Schema(description = "재직 상태")
    val employmentStatus: String,
    @field:Schema(description = "회사")
    val company: String,
)
