package picklab.backend.activity.application.model

import picklab.backend.activity.domain.enums.ActivityBookmarkSortType
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.job.domain.enums.JobGroup

class GetMyBookmarkListCommand(
    val memberId: Long,
    val activityTypes: List<ActivityType>?,
    val jobGroups: List<JobGroup>?,
    val recruitmentStatus: RecruitmentStatus?,
    val sortType: ActivityBookmarkSortType,
    val page: Int,
    val size: Int,
)
