package picklab.backend.review.application.query

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import picklab.backend.review.application.model.ActivityReviewListQueryRequest
import picklab.backend.review.application.query.model.ActivityReviewListView
import picklab.backend.review.application.query.model.MyReviewListView

interface ReviewOverviewQueryRepository {
    fun findMyReviews(
        memberId: Long,
        pageable: Pageable,
    ): Page<MyReviewListView>

    fun findActivityReviewsWithFilter(
        request: ActivityReviewListQueryRequest,
        activityId: Long,
        pageable: Pageable,
    ): Page<ActivityReviewListView>
}
