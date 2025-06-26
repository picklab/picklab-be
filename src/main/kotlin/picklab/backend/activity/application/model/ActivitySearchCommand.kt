package picklab.backend.activity.application.model

import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.ActivitySortType
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.enums.DomainType
import picklab.backend.activity.domain.enums.EducationCostType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.job.domain.enums.JobDetail

data class ActivitySearchCommand(
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
