package picklab.backend.review.domain.service

import org.springframework.stereotype.Service
import picklab.backend.review.domain.repository.ReviewHelpfulRepository

@Service
class ReviewHelpfulService(
    private val reviewHelpfulRepository: ReviewHelpfulRepository,
) {
    fun markHelpful(
        memberId: Long,
        reviewId: Long,
    ) {
        reviewHelpfulRepository.upsert(memberId, reviewId)
    }

    fun unmarkHelpful(
        memberId: Long,
        reviewId: Long,
    ) {
        reviewHelpfulRepository.deleteByMemberIdAndReviewId(memberId, reviewId)
    }

    fun countByReviewIds(reviewIds: Collection<Long>): Map<Long, Long> {
        if (reviewIds.isEmpty()) {
            return emptyMap()
        }

        return reviewHelpfulRepository
            .countByReviewIds(reviewIds)
            .associate { it.reviewId to it.helpfulCount }
    }

    fun findHelpfulReviewIds(
        memberId: Long?,
        reviewIds: Collection<Long>,
    ): Set<Long> {
        if (memberId == null || reviewIds.isEmpty()) {
            return emptySet()
        }

        return reviewHelpfulRepository
            .findReviewIdsByMemberId(memberId, reviewIds)
            .toSet()
    }
}
