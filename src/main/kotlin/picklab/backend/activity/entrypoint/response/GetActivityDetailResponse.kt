package picklab.backend.activity.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.entity.CompetitionActivity
import picklab.backend.activity.domain.entity.EducationActivity
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.entity.SeminarActivity
import picklab.backend.activity.domain.enums.ActivityType
import java.time.LocalDate

// TODO 파일 업로드, Review 관련 내용 추가 필요함
data class GetActivityDetailResponse(
    @field:Schema(description = "활동 ID")
    val id: Long,
    @field:Schema(description = "활동 제목")
    val title: String,
    @field:Schema(description = "조직")
    val organization: String,
    @field:Schema(description = "대상")
    val target: String,
    @field:Schema(description = "모집 기간")
    val recruitPeriod: RecruitPeriod,
    @field:Schema(description = "활동 기간")
    val activityPeriod: ActivityPeriod,
    @field:Schema(description = "카테고리")
    val category: String,
    @field:Schema(description = "도메인들")
    val domains: List<String>,
    @field:Schema(description = "지역들")
    val regions: List<String>,
    @field:Schema(description = "직무 태그들")
    val jobTags: List<String>?,
    @field:Schema(description = "조회수")
    val views: Long,
    @field:Schema(description = "북마크")
    val bookmarks: Long,
    @field:Schema(description = "활동 홈페이지 URL")
    val homepageUrl: String?,
    @field:Schema(description = "활동 신청 URL")
    val applicationUrl: String? = null,
    @field:Schema(description = "활동 썸네일 URL")
    val thumbnail: String?,
    @field:Schema(description = "지원 상태")
    val applyStatus: String,
    @field:Schema(description = "북마크 여부")
    val isBookmarked: Boolean,
    @field:Schema(description = "상세 설명")
    val description: String?,
    @field:Schema(description = "해택")
    val benefits: String,
    @field:Schema(description = "필요 서류들")
    val requiredFiles: Map<String, RequiredFile>?,
) {
    companion object {
        fun from(
            activity: Activity,
            bookmarkCount: Long,
            isBookmarked: Boolean,
        ) = GetActivityDetailResponse(
            id = activity.id,
            title = activity.title,
            organization = activity.organizer.name,
            target = activity.targetAudience.name,
            recruitPeriod =
                RecruitPeriod(
                    startDate = activity.recruitmentStartDate,
                    endDate = activity.recruitmentEndDate,
                ),
            activityPeriod =
                ActivityPeriod(
                    startDate = activity.startDate,
                    endDate = activity.endDate,
                ),
            category =
                when (activity) {
                    is ExternalActivity -> {
                        ActivityType.EXTRACURRICULAR.name
                    }

                    is EducationActivity -> {
                        ActivityType.EDUCATION.name
                    }

                    is SeminarActivity -> {
                        ActivityType.SEMINAR.name
                    }

                    else -> {
                        ActivityType.COMPETITION.name
                    }
                },
            domains =
                when (activity) {
                    is CompetitionActivity -> listOf(activity.domain.name)
                    else -> emptyList()
                },
            regions =
                when (activity) {
                    is ExternalActivity -> activity.location.label.split("/")
                    is EducationActivity -> activity.location.label.split("/")
                    is SeminarActivity -> activity.location.label.split("/")
                    else -> emptyList()
                },
            jobTags = null,
            views = activity.viewCount,
            bookmarks = bookmarkCount,
            homepageUrl = activity.activityHomepageUrl,
            applicationUrl = activity.activityApplicationUrl,
            thumbnail = activity.activityThumbnailUrl,
            applyStatus = activity.status.name,
            isBookmarked = isBookmarked,
            description = activity.description,
            benefits = activity.benefit,
            requiredFiles = null,
        )
    }
}

data class ActivityPeriod(
    @field:Schema(description = "시작일")
    val startDate: LocalDate,
    @field:Schema(description = "종료일")
    val endDate: LocalDate?,
)

data class RecruitPeriod(
    @field:Schema(description = "시작일")
    val startDate: LocalDate,
    @field:Schema(description = "종료일")
    val endDate: LocalDate?,
)

data class RequiredFile(
    @field:Schema(description = "이름")
    val name: String,
    @field:Schema(description = "URL")
    val url: String,
)
