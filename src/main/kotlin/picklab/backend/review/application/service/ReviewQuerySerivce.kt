package picklab.backend.review.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import picklab.backend.review.application.model.ActivityReviewListQueryRequest
import picklab.backend.review.application.query.ReviewOverviewQueryRepository
import picklab.backend.review.application.query.model.ActivityReviewListItem
import picklab.backend.review.application.query.model.MyReviewListItem

@Service
class ReviewOverviewQueryService(
    private val reviewOverviewQueryRepository: ReviewOverviewQueryRepository,
) {
    fun findMyReviews(
        memberId: Long,
        pageable: PageRequest,
    ): Page<MyReviewListItem> = reviewOverviewQueryRepository.findMyReviews(memberId, pageable)

    fun findActivityReviews(
        request: ActivityReviewListQueryRequest,
        activityId: Long,
        pageable: PageRequest,
    ): Page<ActivityReviewListItem> = reviewOverviewQueryRepository.findActivityReviewsWithFilter(request, activityId, pageable)
}
