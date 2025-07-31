package picklab.backend.activity.application.model

data class RecommendActivitiesCommand(
    val memberId: Long,
    val page: Int,
    val size: Int,
)
