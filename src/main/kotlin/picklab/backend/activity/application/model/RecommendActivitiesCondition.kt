package picklab.backend.activity.application.model

data class RecommendActivitiesCondition(
    val memberId: Long,
    val page: Int,
    val size: Int,
)
