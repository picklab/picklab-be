package picklab.backend.review.application.model

data class MyReviewListQueryRequest(
    val page: Int,
    val size: Int,
    val memberId: Long,
)
