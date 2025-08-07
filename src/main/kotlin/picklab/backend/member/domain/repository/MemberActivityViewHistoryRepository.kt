package picklab.backend.member.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import picklab.backend.member.domain.entity.MemberActivityViewHistory
import java.time.LocalDateTime

interface MemberActivityViewHistoryRepository : JpaRepository<MemberActivityViewHistory, Long> {
    @Modifying
    @Query(
        """
        INSERT INTO member_activity_view_history (member_id, activity_id, created_at, updated_at) 
        VALUES (:memberId, :activityId, :now, :now)
        ON DUPLICATE KEY UPDATE updated_at = :now
    """,
        nativeQuery = true,
    )
    fun upsertViewHistory(
        @Param("memberId") memberId: Long,
        @Param("activityId") activityId: Long,
        @Param("now") now: LocalDateTime,
    )
}
