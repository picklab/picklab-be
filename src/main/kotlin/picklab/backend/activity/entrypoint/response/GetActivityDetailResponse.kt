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
    @Schema(description = "활동 ID")
    val id: Long,
    @Schema(description = "활동 제목")
    val title: String,
    @Schema(description = "조직")
    val organization: String,
    @Schema(description = "대상")
    val target: String,
    @Schema(description = "모집 기간")
    val recruitPeriod: RecruitPeriod,
    @Schema(description = "활동 기간")
    val activityPeriod: ActivityPeriod,
    @Schema(description = "카테고리")
    val category: String,
    @Schema(description = "도메인들")
    val domains: List<String>,
    @Schema(description = "지역들")
    val regions: List<String>,
    @Schema(description = "직무 태그들")
    val jobTags: List<String>?,
    @Schema(description = "조회수")
    val views: Long,
    @Schema(description = "북마크")
    val bookmarks: Long,
    @Schema(description = "미리보기")
    val thumbnail: String?,
    @Schema(description = "홈페이지 URL")
    val homepageUrl: String?,
    @Schema(description = "지원 상태")
    val applyStatus: String,
    @Schema(description = "북마크 여부")
    val isBookmarked: Boolean,
    @Schema(description = "살세 설명")
    val description: String,
    @Schema(description = "해택")
    val benefits: String,
    @Schema(description = "필요 서류들")
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
            thumbnail = activity.activityThumbnailUrl,
            homepageUrl = null,
            applyStatus = activity.status.name,
            isBookmarked = isBookmarked,
            description = activity.description,
            benefits = activity.benefit,
            requiredFiles = null,
        )
    }
}

data class ActivityPeriod(
    @Schema(description = "시작일")
    val startDate: LocalDate,
    @Schema(description = "종료일")
    val endDate: LocalDate,
)

data class RecruitPeriod(
    @Schema(description = "시작일")
    val startDate: LocalDate,
    @Schema(description = "종료일")
    val endDate: LocalDate,
)

data class RequiredFile(
    @Schema(description = "이름")
    val name: String,
    @Schema(description = "URL")
    val url: String,
)
