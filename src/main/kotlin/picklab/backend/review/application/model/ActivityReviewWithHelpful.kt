package picklab.backend.review.application.model

import picklab.backend.review.application.query.model.ActivityReviewListView

data class ActivityReviewWithHelpful(
    val review: ActivityReviewListView,
    val helpfulCount: Long,
    val isHelpful: Boolean,
)
