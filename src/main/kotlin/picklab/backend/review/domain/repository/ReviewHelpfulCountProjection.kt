package picklab.backend.review.domain.repository

interface ReviewHelpfulCountProjection {
    val reviewId: Long
    val helpfulCount: Long
}
