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
    @field:Schema(description = "이메일 마케팅 수신 동의 여부")
    val emailAgreement: Boolean,
    @field:Schema(description = "알림 수신 설정")
    val notificationPreferences: NotificationPreferencesResponse,
)

data class EmploymentInfoResponse(
    @field:Schema(description = "재직 상태")
    val employmentStatus: String,
    @field:Schema(description = "회사")
    val company: String,
)

data class NotificationPreferencesResponse(
    @field:Schema(description = "인기 공고 알림 수신 여부")
    val popular: Boolean,
    @field:Schema(description = "저장한 공고 알림 수신 여부")
    val bookmarked: Boolean,
)
