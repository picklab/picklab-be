package picklab.backend.activity.entrypoint.mapper

import picklab.backend.activity.application.model.ActivitySearchCondition
import picklab.backend.activity.domain.enums.*
import picklab.backend.activity.entrypoint.request.ActivitySearchRequest
import picklab.backend.job.domain.enums.JobDetail

fun ActivitySearchRequest.toCommand(): ActivitySearchCondition =
    ActivitySearchCondition(
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
