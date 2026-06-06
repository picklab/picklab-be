package picklab.backend.review.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import picklab.backend.review.domain.entity.ReviewHelpful

interface ReviewHelpfulRepository : JpaRepository<ReviewHelpful, Long> {
    @Modifying
    @Query(
        nativeQuery = true,
        value = """
            INSERT INTO review_helpful (member_id, review_id, created_at, updated_at)
            VALUES (:memberId, :reviewId, NOW(), NOW())
            ON DUPLICATE KEY UPDATE member_id = member_id
        """,
    )
    fun upsert(
        @Param("memberId") memberId: Long,
        @Param("reviewId") reviewId: Long,
    ): Int

    @Modifying
    @Query(
        """
        DELETE FROM ReviewHelpful helpful
        WHERE helpful.member.id = :memberId
          AND helpful.review.id = :reviewId
        """,
    )
    fun deleteByMemberIdAndReviewId(
        @Param("memberId") memberId: Long,
        @Param("reviewId") reviewId: Long,
    ): Int

    @Query(
        """
        SELECT helpful.review.id AS reviewId, COUNT(helpful.id) AS helpfulCount
        FROM ReviewHelpful helpful
        WHERE helpful.review.id IN :reviewIds
        GROUP BY helpful.review.id
        """,
    )
    fun countByReviewIds(
        @Param("reviewIds") reviewIds: Collection<Long>,
    ): List<ReviewHelpfulCountProjection>

    @Query(
        """
        SELECT helpful.review.id
        FROM ReviewHelpful helpful
        WHERE helpful.member.id = :memberId
          AND helpful.review.id IN :reviewIds
        """,
    )
    fun findReviewIdsByMemberId(
        @Param("memberId") memberId: Long,
        @Param("reviewIds") reviewIds: Collection<Long>,
    ): List<Long>
}
