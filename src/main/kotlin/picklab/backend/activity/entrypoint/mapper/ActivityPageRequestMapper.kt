package picklab.backend.activity.entrypoint.mapper

import picklab.backend.activity.application.model.PopularActivitiesCondition
import picklab.backend.activity.application.model.RecommendActivitiesCondition
import picklab.backend.activity.entrypoint.request.GetActivityPageRequest

fun GetActivityPageRequest.toPopularActivitiesCommand(memberId: Long?): PopularActivitiesCondition =
    PopularActivitiesCondition(
        memberId = memberId,
        page = page,
        size = size,
    )

fun GetActivityPageRequest.toRecommendActivitiesCommand(memberId: Long): RecommendActivitiesCondition =
    RecommendActivitiesCondition(
        memberId = memberId,
        page = page,
        size = size,
    )
