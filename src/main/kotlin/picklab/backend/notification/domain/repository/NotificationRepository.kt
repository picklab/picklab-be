package picklab.backend.notification.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import picklab.backend.member.domain.entity.Member
import picklab.backend.notification.domain.entity.Notification
import picklab.backend.notification.domain.entity.NotificationType
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

    /**
     * 지정된 날짜 이전에 생성된 알림을 배치 단위로 조회합니다
     */
    fun findByCreatedAtBeforeOrderByCreatedAtAsc(
        cutoffDate: LocalDateTime,
        pageable: Pageable
    ): Page<Notification>

    @Query(nativeQuery = true, value = "select * from notification where id = :id")
    fun findByIdIgnoreDelete(id: Long): Notification?

    fun findAllByMember(member: Member) : List<Notification>

    fun existsByTypeAndReferenceId(type: NotificationType, referenceId: String): Boolean
    
    /**
     * 특정 사용자가 특정 활동에 대한 특정 타입의 알림을 이미 받았는지 확인합니다
     */
    fun existsByTypeAndReferenceIdAndMemberId(type: NotificationType, referenceId: String, memberId: Long): Boolean
}