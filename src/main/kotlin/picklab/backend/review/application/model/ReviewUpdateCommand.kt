package picklab.backend.review.application.model

data class ReviewUpdateCommand(
    val id: Long,
    val activityId: Long,
    val memberId: Long,
    val overallScore: Int,
    val infoScore: Int,
    val difficultyScore: Int,
    val benefitScore: Int,
    val summary: String,
    val strength: String,
    val weakness: String,
    val tips: String?,
    val jobRelevanceScore: Int,
    val url: String?,
)
