package picklab.backend.notification.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import picklab.backend.notification.domain.entity.Notification
import java.time.LocalDateTime

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    
    /**
     * 특정 사용자의 알림 목록을 조회합니다
     */
    fun findByMemberIdOrderByCreatedAtDesc(
        memberId: Long,
        pageable: Pageable
    ): Page<Notification>
    
    /**
     * 특정 사용자의 읽지 않은 알림 개수를 조회합니다
     */
    fun countByMemberIdAndReadIsFalse(memberId: Long): Long
    
    /**
     * 특정 사용자의 특정 알림을 조회합니다
     */
    fun findByIdAndMemberId(id: Long, memberId: Long): Notification?
    
    /**
     * 특정 사용자의 모든 읽지 않은 알림을 읽음 상태로 변경합니다
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.member.id = :memberId AND n.isRead = false")
    fun markAllAsReadByMemberId(@Param("memberId") memberId: Long): Int

    /**
     * 특정 사용자의 최근 n일 내 알림을 조회합니다
     */
    fun findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(
        memberId: Long,
        createdAt: LocalDateTime
    ): List<Notification>
}