package picklab.backend.review.application.query

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import picklab.backend.review.application.model.ActivityReviewListQueryRequest
import picklab.backend.review.application.query.model.ActivityReviewListItem
import picklab.backend.review.application.query.model.MyReviewListItem

interface ReviewOverviewQueryRepository {
    fun findMyReviews(
        memberId: Long,
        pageable: Pageable,
    ): Page<MyReviewListItem>

    fun findActivityReviewsWithFilter(
        request: ActivityReviewListQueryRequest,
        activityId: Long,
        pageable: Pageable,
    ): Page<ActivityReviewListItem>
}
