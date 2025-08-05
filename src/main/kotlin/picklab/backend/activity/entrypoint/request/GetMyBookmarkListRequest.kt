package picklab.backend.activity.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.activity.application.model.GetMyBookmarkListCondition
import picklab.backend.activity.domain.enums.ActivityBookmarkSortType
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.job.domain.enums.JobGroup

data class GetMyBookmarkListRequest(
    @field:Schema(description = "활동 분류 (extracurricular, seminar, education, competition)")
    val activityTypes: List<ActivityType>? = null,
    @field:Schema(description = "관련 직무(PLANNING, DESIGN, DEVELOPMENT, MARKETING, AI)")
    val jobGroups: List<JobGroup>? = null,
    @field:Schema(description = "모집 상태 (OPEN, CLOSED)")
    val recruitmentStatus: RecruitmentStatus? = null,
    @field:Schema(description = "정렬 기준 (RECENTLY_BOOKMARKED, LATEST, DEADLINE_ASC, DEADLINE_DESC)")
    val sortType: ActivityBookmarkSortType = ActivityBookmarkSortType.RECENTLY_BOOKMARKED,
    @field:Schema(description = "요청 페이지 (기본값 0)")
    val page: Int = 0,
    @field:Schema(description = "페이지 크기 (기본값 16)")
    val size: Int = 16,
) {
    fun toCommand(memberId: Long) =
        GetMyBookmarkListCondition(
            memberId = memberId,
            activityTypes = activityTypes,
            jobGroups = jobGroups,
            recruitmentStatus = recruitmentStatus,
            sortType = sortType,
            page = page,
            size = size,
        )
}
