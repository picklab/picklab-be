package picklab.backend.activity.application.model

import picklab.backend.activity.domain.enums.*
import picklab.backend.job.domain.enums.JobDetail

data class ActivitySearchCondition(
    val category: ActivityType,
    val jobTag: List<JobDetail>?,
    val organizer: List<OrganizerType>?,
    val target: List<ParticipantType>?,
    val field: List<ActivityFieldType>?,
    val location: List<LocationType>?,
    val format: List<EducationFormatType>?,
    val costType: List<EducationCostType>?,
    val award: List<Long>?,
    val duration: List<Long>?,
    val domain: List<DomainType>?,
    val sort: ActivitySortType,
)
