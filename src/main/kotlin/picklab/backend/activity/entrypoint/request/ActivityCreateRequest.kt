package picklab.backend.activity.entrypoint.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import picklab.backend.activity.application.model.ActivityCreateCommand
import picklab.backend.activity.application.model.ActivityJobCategoryCommand
import picklab.backend.activity.application.model.ActivityUploadFileCommand
import picklab.backend.activity.application.model.CompetitionActivityCreateCommand
import picklab.backend.activity.application.model.EducationActivityCreateCommand
import picklab.backend.activity.application.model.ExternalActivityCreateCommand
import picklab.backend.activity.application.model.SeminarActivityCreateCommand
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.enums.DomainType
import picklab.backend.activity.domain.enums.EducationCostType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import java.time.LocalDate

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "activity_type",
    visible = true,
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ExternalActivityCreateRequest::class, name = "EXTRACURRICULAR"),
    JsonSubTypes.Type(value = SeminarActivityCreateRequest::class, name = "SEMINAR"),
    JsonSubTypes.Type(value = EducationActivityCreateRequest::class, name = "EDUCATION"),
    JsonSubTypes.Type(value = CompetitionActivityCreateRequest::class, name = "COMPETITION"),
)
@Schema(
    description = "활동 생성 요청. activity_type 값에 따라 타입별 필드를 입력합니다.",
    discriminatorProperty = "activity_type",
    oneOf = [
        ExternalActivityCreateRequest::class,
        SeminarActivityCreateRequest::class,
        EducationActivityCreateRequest::class,
        CompetitionActivityCreateRequest::class,
    ],
    discriminatorMapping = [
        DiscriminatorMapping(value = "EXTRACURRICULAR", schema = ExternalActivityCreateRequest::class),
        DiscriminatorMapping(value = "SEMINAR", schema = SeminarActivityCreateRequest::class),
        DiscriminatorMapping(value = "EDUCATION", schema = EducationActivityCreateRequest::class),
        DiscriminatorMapping(value = "COMPETITION", schema = CompetitionActivityCreateRequest::class),
    ],
)
sealed class ActivityCreateRequest(
    @field:NotNull
    @field:Schema(description = "활동 타입", example = "EXTRACURRICULAR")
    open val activityType: ActivityType,
    @field:Schema(description = "활동 그룹 ID")
    open val activityGroupId: Long,
    @field:NotBlank
    @field:Size(max = 50)
    @field:Schema(description = "활동명")
    open val title: String,
    @field:Schema(description = "주최 기관/단체")
    open val organizer: OrganizerType,
    @field:Schema(description = "참여 대상")
    open val targetAudience: ParticipantType,
    @field:Schema(description = "모집 시작일")
    open val recruitmentStartDate: LocalDate,
    @field:Schema(description = "모집 종료일")
    open val recruitmentEndDate: LocalDate,
    @field:Schema(description = "활동 시작일")
    open val startDate: LocalDate,
    @field:Schema(description = "활동 종료일")
    open val endDate: LocalDate,
    @field:Schema(description = "모집 상태")
    open val status: RecruitmentStatus,
    @field:Min(0)
    @field:Schema(description = "활동 기간(일)")
    open val duration: Int,
    @field:Size(max = 255)
    @field:Schema(description = "활동 홈페이지 URL")
    open val activityHomepageUrl: String? = null,
    @field:Size(max = 255)
    @field:Schema(description = "활동 신청 URL")
    open val activityApplicationUrl: String? = null,
    @field:Size(max = 255)
    @field:Schema(description = "활동 썸네일 URL")
    open val activityThumbnailUrl: String? = null,
    @field:Size(max = 2000)
    @field:Schema(description = "활동 설명")
    open val description: String? = null,
    @field:Size(max = 2000)
    @field:Schema(description = "활동 혜택")
    open val benefit: String = "",
    @field:Valid
    @field:Schema(description = "활동 연관 직무 목록")
    open val jobCategories: List<ActivityJobCategoryRequest> = emptyList(),
    @field:Valid
    @field:Schema(description = "활동 업로드 파일 목록")
    open val uploadFiles: List<ActivityUploadFileRequest> = emptyList(),
) {
    abstract fun toCommand(): ActivityCreateCommand

    protected fun commonJobCategories() = jobCategories.map { it.toCommand() }

    protected fun commonUploadFiles() = uploadFiles.map { it.toCommand() }
}

@Schema(name = "ExternalActivityCreateRequest", description = "대외활동 생성 요청")
data class ExternalActivityCreateRequest(
    @field:Schema(description = "활동 타입", example = "EXTRACURRICULAR")
    override val activityType: ActivityType = ActivityType.EXTRACURRICULAR,
    @field:Min(1)
    @field:Schema(description = "활동 그룹 ID")
    override val activityGroupId: Long,
    @field:NotBlank
    @field:Size(max = 50)
    @field:Schema(description = "활동명")
    override val title: String,
    @field:Schema(description = "주최 기관/단체")
    override val organizer: OrganizerType,
    @field:Schema(description = "참여 대상")
    override val targetAudience: ParticipantType,
    @field:Schema(description = "모집 시작일")
    override val recruitmentStartDate: LocalDate,
    @field:Schema(description = "모집 종료일")
    override val recruitmentEndDate: LocalDate,
    @field:Schema(description = "활동 시작일")
    override val startDate: LocalDate,
    @field:Schema(description = "활동 종료일")
    override val endDate: LocalDate,
    @field:Schema(description = "모집 상태")
    override val status: RecruitmentStatus,
    @field:Min(0)
    @field:Schema(description = "활동 기간(일)")
    override val duration: Int,
    @field:Schema(description = "활동 홈페이지 URL")
    override val activityHomepageUrl: String? = null,
    @field:Schema(description = "활동 신청 URL")
    override val activityApplicationUrl: String? = null,
    @field:Schema(description = "활동 썸네일 URL")
    override val activityThumbnailUrl: String? = null,
    @field:Schema(description = "활동 설명")
    override val description: String? = null,
    @field:Schema(description = "활동 혜택")
    override val benefit: String = "",
    @field:Valid
    @field:Schema(description = "활동 연관 직무 목록")
    override val jobCategories: List<ActivityJobCategoryRequest> = emptyList(),
    @field:Valid
    @field:Schema(description = "활동 업로드 파일 목록")
    override val uploadFiles: List<ActivityUploadFileRequest> = emptyList(),
    @field:Schema(description = "모임 지역")
    val location: LocationType,
    @field:Schema(description = "활동 분야")
    val activityField: ActivityFieldType,
) : ActivityCreateRequest(
        activityType = activityType,
        activityGroupId = activityGroupId,
        title = title,
        organizer = organizer,
        targetAudience = targetAudience,
        recruitmentStartDate = recruitmentStartDate,
        recruitmentEndDate = recruitmentEndDate,
        startDate = startDate,
        endDate = endDate,
        status = status,
        duration = duration,
        activityHomepageUrl = activityHomepageUrl,
        activityApplicationUrl = activityApplicationUrl,
        activityThumbnailUrl = activityThumbnailUrl,
        description = description,
        benefit = benefit,
        jobCategories = jobCategories,
        uploadFiles = uploadFiles,
    ) {
    override fun toCommand(): ActivityCreateCommand =
        ExternalActivityCreateCommand(
            activityGroupId = activityGroupId,
            title = title,
            organizer = organizer,
            targetAudience = targetAudience,
            recruitmentStartDate = recruitmentStartDate,
            recruitmentEndDate = recruitmentEndDate,
            startDate = startDate,
            endDate = endDate,
            status = status,
            duration = duration,
            activityHomepageUrl = activityHomepageUrl,
            activityApplicationUrl = activityApplicationUrl,
            activityThumbnailUrl = activityThumbnailUrl,
            description = description,
            benefit = benefit,
            jobCategories = commonJobCategories(),
            uploadFiles = commonUploadFiles(),
            location = location,
            activityField = activityField,
        )
}

@Schema(name = "SeminarActivityCreateRequest", description = "강연/세미나 생성 요청")
data class SeminarActivityCreateRequest(
    @field:Schema(description = "활동 타입", example = "SEMINAR")
    override val activityType: ActivityType = ActivityType.SEMINAR,
    @field:Min(1)
    @field:Schema(description = "활동 그룹 ID")
    override val activityGroupId: Long,
    @field:NotBlank
    @field:Size(max = 50)
    @field:Schema(description = "활동명")
    override val title: String,
    @field:Schema(description = "주최 기관/단체")
    override val organizer: OrganizerType,
    @field:Schema(description = "참여 대상")
    override val targetAudience: ParticipantType,
    @field:Schema(description = "모집 시작일")
    override val recruitmentStartDate: LocalDate,
    @field:Schema(description = "모집 종료일")
    override val recruitmentEndDate: LocalDate,
    @field:Schema(description = "활동 시작일")
    override val startDate: LocalDate,
    @field:Schema(description = "활동 종료일")
    override val endDate: LocalDate,
    @field:Schema(description = "모집 상태")
    override val status: RecruitmentStatus,
    @field:Min(0)
    @field:Schema(description = "활동 기간(일)")
    override val duration: Int,
    @field:Schema(description = "활동 홈페이지 URL")
    override val activityHomepageUrl: String? = null,
    @field:Schema(description = "활동 신청 URL")
    override val activityApplicationUrl: String? = null,
    @field:Schema(description = "활동 썸네일 URL")
    override val activityThumbnailUrl: String? = null,
    @field:Schema(description = "활동 설명")
    override val description: String? = null,
    @field:Schema(description = "활동 혜택")
    override val benefit: String = "",
    @field:Valid
    @field:Schema(description = "활동 연관 직무 목록")
    override val jobCategories: List<ActivityJobCategoryRequest> = emptyList(),
    @field:Valid
    @field:Schema(description = "활동 업로드 파일 목록")
    override val uploadFiles: List<ActivityUploadFileRequest> = emptyList(),
    @field:Schema(description = "모임 지역")
    val location: LocationType,
) : ActivityCreateRequest(
        activityType = activityType,
        activityGroupId = activityGroupId,
        title = title,
        organizer = organizer,
        targetAudience = targetAudience,
        recruitmentStartDate = recruitmentStartDate,
        recruitmentEndDate = recruitmentEndDate,
        startDate = startDate,
        endDate = endDate,
        status = status,
        duration = duration,
        activityHomepageUrl = activityHomepageUrl,
        activityApplicationUrl = activityApplicationUrl,
        activityThumbnailUrl = activityThumbnailUrl,
        description = description,
        benefit = benefit,
        jobCategories = jobCategories,
        uploadFiles = uploadFiles,
    ) {
    override fun toCommand(): ActivityCreateCommand =
        SeminarActivityCreateCommand(
            activityGroupId = activityGroupId,
            title = title,
            organizer = organizer,
            targetAudience = targetAudience,
            recruitmentStartDate = recruitmentStartDate,
            recruitmentEndDate = recruitmentEndDate,
            startDate = startDate,
            endDate = endDate,
            status = status,
            duration = duration,
            activityHomepageUrl = activityHomepageUrl,
            activityApplicationUrl = activityApplicationUrl,
            activityThumbnailUrl = activityThumbnailUrl,
            description = description,
            benefit = benefit,
            jobCategories = commonJobCategories(),
            uploadFiles = commonUploadFiles(),
            location = location,
        )
}

@Schema(name = "EducationActivityCreateRequest", description = "교육 생성 요청")
data class EducationActivityCreateRequest(
    @field:Schema(description = "활동 타입", example = "EDUCATION")
    override val activityType: ActivityType = ActivityType.EDUCATION,
    @field:Min(1)
    @field:Schema(description = "활동 그룹 ID")
    override val activityGroupId: Long,
    @field:NotBlank
    @field:Size(max = 50)
    @field:Schema(description = "활동명")
    override val title: String,
    @field:Schema(description = "주최 기관/단체")
    override val organizer: OrganizerType,
    @field:Schema(description = "참여 대상")
    override val targetAudience: ParticipantType,
    @field:Schema(description = "모집 시작일")
    override val recruitmentStartDate: LocalDate,
    @field:Schema(description = "모집 종료일")
    override val recruitmentEndDate: LocalDate,
    @field:Schema(description = "활동 시작일")
    override val startDate: LocalDate,
    @field:Schema(description = "활동 종료일")
    override val endDate: LocalDate,
    @field:Schema(description = "모집 상태")
    override val status: RecruitmentStatus,
    @field:Min(0)
    @field:Schema(description = "활동 기간(일)")
    override val duration: Int,
    @field:Schema(description = "활동 홈페이지 URL")
    override val activityHomepageUrl: String? = null,
    @field:Schema(description = "활동 신청 URL")
    override val activityApplicationUrl: String? = null,
    @field:Schema(description = "활동 썸네일 URL")
    override val activityThumbnailUrl: String? = null,
    @field:Schema(description = "활동 설명")
    override val description: String? = null,
    @field:Schema(description = "활동 혜택")
    override val benefit: String = "",
    @field:Valid
    @field:Schema(description = "활동 연관 직무 목록")
    override val jobCategories: List<ActivityJobCategoryRequest> = emptyList(),
    @field:Valid
    @field:Schema(description = "활동 업로드 파일 목록")
    override val uploadFiles: List<ActivityUploadFileRequest> = emptyList(),
    @field:Schema(description = "모임 지역")
    val location: LocationType,
    @field:Min(0)
    @field:Schema(description = "교육 비용")
    val cost: Long,
    @field:Schema(description = "교육 비용 유형")
    val costType: EducationCostType,
    @field:Schema(description = "교육 형식")
    val educationFormat: EducationFormatType,
) : ActivityCreateRequest(
        activityType = activityType,
        activityGroupId = activityGroupId,
        title = title,
        organizer = organizer,
        targetAudience = targetAudience,
        recruitmentStartDate = recruitmentStartDate,
        recruitmentEndDate = recruitmentEndDate,
        startDate = startDate,
        endDate = endDate,
        status = status,
        duration = duration,
        activityHomepageUrl = activityHomepageUrl,
        activityApplicationUrl = activityApplicationUrl,
        activityThumbnailUrl = activityThumbnailUrl,
        description = description,
        benefit = benefit,
        jobCategories = jobCategories,
        uploadFiles = uploadFiles,
    ) {
    override fun toCommand(): ActivityCreateCommand =
        EducationActivityCreateCommand(
            activityGroupId = activityGroupId,
            title = title,
            organizer = organizer,
            targetAudience = targetAudience,
            recruitmentStartDate = recruitmentStartDate,
            recruitmentEndDate = recruitmentEndDate,
            startDate = startDate,
            endDate = endDate,
            status = status,
            duration = duration,
            activityHomepageUrl = activityHomepageUrl,
            activityApplicationUrl = activityApplicationUrl,
            activityThumbnailUrl = activityThumbnailUrl,
            description = description,
            benefit = benefit,
            jobCategories = commonJobCategories(),
            uploadFiles = commonUploadFiles(),
            location = location,
            cost = cost,
            costType = costType,
            educationFormat = educationFormat,
        )
}

@Schema(name = "CompetitionActivityCreateRequest", description = "공모전/해커톤 생성 요청")
data class CompetitionActivityCreateRequest(
    @field:Schema(description = "활동 타입", example = "COMPETITION")
    override val activityType: ActivityType = ActivityType.COMPETITION,
    @field:Min(1)
    @field:Schema(description = "활동 그룹 ID")
    override val activityGroupId: Long,
    @field:NotBlank
    @field:Size(max = 50)
    @field:Schema(description = "활동명")
    override val title: String,
    @field:Schema(description = "주최 기관/단체")
    override val organizer: OrganizerType,
    @field:Schema(description = "참여 대상")
    override val targetAudience: ParticipantType,
    @field:Schema(description = "모집 시작일")
    override val recruitmentStartDate: LocalDate,
    @field:Schema(description = "모집 종료일")
    override val recruitmentEndDate: LocalDate,
    @field:Schema(description = "활동 시작일")
    override val startDate: LocalDate,
    @field:Schema(description = "활동 종료일")
    override val endDate: LocalDate,
    @field:Schema(description = "모집 상태")
    override val status: RecruitmentStatus,
    @field:Min(0)
    @field:Schema(description = "활동 기간(일)")
    override val duration: Int,
    @field:Schema(description = "활동 홈페이지 URL")
    override val activityHomepageUrl: String? = null,
    @field:Schema(description = "활동 신청 URL")
    override val activityApplicationUrl: String? = null,
    @field:Schema(description = "활동 썸네일 URL")
    override val activityThumbnailUrl: String? = null,
    @field:Schema(description = "활동 설명")
    override val description: String? = null,
    @field:Schema(description = "활동 혜택")
    override val benefit: String = "",
    @field:Valid
    @field:Schema(description = "활동 연관 직무 목록")
    override val jobCategories: List<ActivityJobCategoryRequest> = emptyList(),
    @field:Valid
    @field:Schema(description = "활동 업로드 파일 목록")
    override val uploadFiles: List<ActivityUploadFileRequest> = emptyList(),
    @field:Schema(description = "도메인")
    val domain: DomainType,
    @field:Min(0)
    @field:Schema(description = "시상 규모")
    val cost: Long,
) : ActivityCreateRequest(
        activityType = activityType,
        activityGroupId = activityGroupId,
        title = title,
        organizer = organizer,
        targetAudience = targetAudience,
        recruitmentStartDate = recruitmentStartDate,
        recruitmentEndDate = recruitmentEndDate,
        startDate = startDate,
        endDate = endDate,
        status = status,
        duration = duration,
        activityHomepageUrl = activityHomepageUrl,
        activityApplicationUrl = activityApplicationUrl,
        activityThumbnailUrl = activityThumbnailUrl,
        description = description,
        benefit = benefit,
        jobCategories = jobCategories,
        uploadFiles = uploadFiles,
    ) {
    override fun toCommand(): ActivityCreateCommand =
        CompetitionActivityCreateCommand(
            activityGroupId = activityGroupId,
            title = title,
            organizer = organizer,
            targetAudience = targetAudience,
            recruitmentStartDate = recruitmentStartDate,
            recruitmentEndDate = recruitmentEndDate,
            startDate = startDate,
            endDate = endDate,
            status = status,
            duration = duration,
            activityHomepageUrl = activityHomepageUrl,
            activityApplicationUrl = activityApplicationUrl,
            activityThumbnailUrl = activityThumbnailUrl,
            description = description,
            benefit = benefit,
            jobCategories = commonJobCategories(),
            uploadFiles = commonUploadFiles(),
            domain = domain,
            cost = cost,
        )
}

data class ActivityJobCategoryRequest(
    @field:Schema(description = "직무 대분류", example = "DEVELOPMENT")
    val jobGroup: JobGroup,
    @field:Schema(description = "직무 상세 분류", example = "BACKEND")
    val jobDetail: JobDetail? = null,
) {
    fun toCommand() =
        ActivityJobCategoryCommand(
            jobGroup = jobGroup,
            jobDetail = jobDetail,
        )
}

data class ActivityUploadFileRequest(
    @field:NotBlank
    @field:Size(max = 255)
    @field:Schema(description = "업로드 파일명", example = "지원서.pdf")
    val name: String,
    @field:NotBlank
    @field:Size(max = 2084)
    @field:Schema(description = "업로드 파일 URL")
    val url: String,
) {
    fun toCommand() =
        ActivityUploadFileCommand(
            name = name,
            url = url,
        )
}
