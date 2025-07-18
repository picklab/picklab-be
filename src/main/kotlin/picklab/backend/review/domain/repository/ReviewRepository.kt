package picklab.backend.review.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.review.domain.entity.Review

interface ReviewRepository : JpaRepository<Review, Long> {
    fun existsByActivityIdAndMemberId(
        activityId: Long,
        memberId: Long,
    ): Boolean
}
