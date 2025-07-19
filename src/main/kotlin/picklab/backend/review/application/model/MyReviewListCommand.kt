package picklab.backend.review.application.model

data class MyReviewListCommand(
    val page: Int,
    val size: Int,
    val memberId: Long,
)
