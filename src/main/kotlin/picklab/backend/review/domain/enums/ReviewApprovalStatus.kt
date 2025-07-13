package picklab.backend.review.domain.enums

enum class ReviewApprovalStatus(
    val label: String,
) {
    PENDING("승인 중"),
    APPROVED("승인"),
    REJECTED("미승인"),
}
