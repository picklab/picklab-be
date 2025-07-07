package picklab.backend.activity.entrypoint.response

import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.entity.CompetitionActivity
import picklab.backend.activity.domain.entity.EducationActivity
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.entity.SeminarActivity
import picklab.backend.activity.domain.enums.ActivityType
import java.time.LocalDate

// TODO 파일 업로드, Review 관련 내용 추가 필요함
data class GetActivityDetailResponse(
    val id: Long,
    val title: String,
    val organization: String,
    val target: String,
    val recruitPeriod: RecruitPeriod,
    val activityPeriod: ActivityPeriod,
    val category: String,
    val domains: List<String>,
    val regions: List<String>,
    val jobTags: List<String>?,
    val views: Long,
    val bookmarks: Long,
    val thumbnail: String?,
    val homepageUrl: String?,
    val applyStatus: String,
    val isBookmarked: Boolean,
    val description: String,
    val benefits: String,
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
    val startDate: LocalDate,
    val endDate: LocalDate,
)

data class RecruitPeriod(
    val startDate: LocalDate,
    val endDate: LocalDate,
)

data class RequiredFile(
    val name: String,
    val url: String,
)
