package picklab.backend.activity.entrypoint.mapper

import picklab.backend.activity.application.model.ActivitySearchCondition
import picklab.backend.activity.entrypoint.request.ActivitySearchRequest

fun ActivitySearchRequest.toCondition(): ActivitySearchCondition =
    ActivitySearchCondition(
        category = category,
        jobTag = jobTag,
        organizerType = organizerType,
        target = target,
        field = field,
        location = location,
        format = format,
        costType = costType?.let { listOf(it) },
        award = award,
        duration = duration,
        domain = domain,
        sort = sort,
    )
