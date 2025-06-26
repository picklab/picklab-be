package picklab.backend.activity.entrypoint.mapper

import picklab.backend.activity.application.model.ActivitySearchCommand
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.ActivitySortType
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.enums.DomainType
import picklab.backend.activity.domain.enums.EducationCostType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.entrypoint.request.ActivitySearchCondition
import picklab.backend.job.domain.enums.JobDetail

fun ActivitySearchCondition.toCommand(): ActivitySearchCommand =
    ActivitySearchCommand(
        category = ActivityType.findByType(category),
        jobTag = jobTag?.map(JobDetail::findByType),
        organizer = organizer?.map(OrganizerType::findByType),
        target = target?.map(ParticipantType::findByType),
        field = field?.map(ActivityFieldType::findByType),
        location = location?.map(LocationType::findByType),
        format = format?.map(EducationFormatType::findByType),
        costType = costType?.let { listOf(EducationCostType.findByType(it)) },
        award = award,
        duration = duration,
        domain = domain?.map(DomainType::findByType),
        sort = ActivitySortType.findByType(sort),
    )
