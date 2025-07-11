package picklab.backend.review.domain.enums

enum class ReviewStatus(
    val label: String,
) {
    PENDING("승인 중"),
    APPROVED("승인"),
    REJECTED("미승인"),
}
