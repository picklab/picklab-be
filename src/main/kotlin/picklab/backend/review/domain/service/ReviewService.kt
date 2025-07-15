package picklab.backend.review.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.review.domain.entity.Review
import picklab.backend.review.domain.repository.ReviewRepository

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
) {
    @Transactional
    fun save(entity: Review): Review = reviewRepository.save(entity)

    fun existsByActivityIdAndMemberId(
        activityId: Long,
        memberId: Long,
    ): Boolean = reviewRepository.existsByActivityIdAndMemberId(activityId, memberId)
}
