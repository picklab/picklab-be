package picklab.backend.review.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import picklab.backend.review.application.query.ReviewQueryRepository
import picklab.backend.review.application.query.model.MyReviewListItem

@Service
class ReviewQueryService(
    private val reviewQueryRepository: ReviewQueryRepository,
) {
    fun findMyReviews(
        memberId: Long,
        pageable: PageRequest,
    ): Page<MyReviewListItem> = reviewQueryRepository.findMyReviews(memberId, pageable)
}
