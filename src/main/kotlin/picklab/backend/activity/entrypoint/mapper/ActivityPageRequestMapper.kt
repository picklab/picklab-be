package picklab.backend.activity.entrypoint.mapper

import picklab.backend.activity.application.model.PopularActivitiesCommand
import picklab.backend.activity.application.model.RecommendActivitiesCommand
import picklab.backend.activity.entrypoint.request.GetActivityPageRequest

fun GetActivityPageRequest.toPopularActivitiesCommand(memberId: Long?): PopularActivitiesCommand =
    PopularActivitiesCommand(
        memberId = memberId,
        page = page,
        size = size,
    )

fun GetActivityPageRequest.toRecommendActivitiesCommand(memberId: Long): RecommendActivitiesCommand =
    RecommendActivitiesCommand(
        memberId = memberId,
        page = page,
        size = size,
    )
