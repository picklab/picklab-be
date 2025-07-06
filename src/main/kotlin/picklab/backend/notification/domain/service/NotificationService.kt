package picklab.backend.notification.domain.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.MemberService
import picklab.backend.notification.domain.entity.Notification
import picklab.backend.notification.domain.repository.NotificationRepository
import picklab.backend.notification.entrypoint.request.NotificationCreateRequest
import picklab.backend.notification.entrypoint.response.NotificationResponse
import java.time.LocalDateTime

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val memberService: MemberService,
    private val sseEmitterService: SseEmitterService
) {

    private val logger = LoggerFactory.getLogger(NotificationService::class.java)

    /**
     * 알림을 생성하고 실시간으로 전송합니다
     */
    fun createAndSendNotification(request: NotificationCreateRequest): NotificationResponse {
        // 수신자 조회
        val receiver = memberService.findActiveMember(request.receiverId)

        // 알림 생성
        val notification = Notification(
            title = request.title,
            type = request.type,
            link = request.link,
            member = receiver
        )

        // 알림 저장
        val savedNotification = notificationRepository.save(notification)

        // 실시간 알림 전송
        sendRealtimeNotification(savedNotification)

        return NotificationResponse.from(savedNotification)
    }

    /**
     * 실시간 알림을 전송합니다
     */
    private fun sendRealtimeNotification(notification: Notification) {
        try {
            val isConnected = sseEmitterService.isUserConnected(notification.member.id!!)
            if (isConnected) {
                val response = NotificationResponse.from(notification)
                val success = sseEmitterService.sendEventToUser(
                    notification.member.id!!,
                    "notification",
                    response
                )

                if (success) {
                    logger.info("실시간 알림 전송 성공. memberId: ${notification.member.id}, notificationId: ${notification.id}")
                } else {
                    logger.warn("실시간 알림 전송 실패. memberId: ${notification.member.id}, notificationId: ${notification.id}")
                }
            } else {
                logger.info("사용자가 연결되어 있지 않음. memberId: ${notification.member.id}, notificationId: ${notification.id}")
            }
        } catch (e: Exception) {
            logger.error("실시간 알림 전송 중 오류 발생", e)
        }
    }

    /**
     * 특정 사용자의 알림 목록을 조회합니다
     */
    @Transactional(readOnly = true)
    fun getNotificationsByMemberId(memberId: Long, pageable: Pageable): Page<NotificationResponse> {
        val notifications = notificationRepository.findByMemberIdOrderByCreatedAtDesc(
            memberId, pageable
        )
        return notifications.map { NotificationResponse.from(it) }
    }

    /**
     * 특정 사용자의 최근 n일 내 알림을 조회합니다
     */
    @Transactional(readOnly = true)
    fun getRecentNotifications(memberId: Long, days: Int): List<NotificationResponse> {
        val cutoffDate = LocalDateTime.now().minusDays(days.toLong())
        val notifications = notificationRepository.findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(
            memberId, cutoffDate
        )
        return notifications.map { NotificationResponse.from(it) }
    }

    /**
     * 알림을 읽음 상태로 변경합니다
     */
    fun markAsRead(notificationId: Long, memberId: Long): NotificationResponse {
        val notification = notificationRepository.findByIdAndMemberId(notificationId, memberId)
            ?: throw BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND)

        notification.isRead = true
        val savedNotification = notificationRepository.save(notification)

        // 읽음 상태 변경을 실시간으로 전송
        sendReadStatusUpdate(savedNotification)

        return NotificationResponse.from(savedNotification)
    }

    /**
     * 읽음 상태 변경을 실시간으로 전송합니다
     */
    private fun sendReadStatusUpdate(notification: Notification) {
        try {
            val isConnected = sseEmitterService.isUserConnected(notification.member.id!!)
            if (isConnected) {
                val response = NotificationResponse.from(notification)
                sseEmitterService.sendEventToUser(
                    notification.member.id!!,
                    "notification_read",
                    response
                )
            }
        } catch (e: Exception) {
            logger.error("읽음 상태 업데이트 실시간 전송 중 오류 발생", e)
        }
    }

    /**
     * 특정 사용자의 모든 알림을 읽음 상태로 변경합니다
     */
    fun markAllAsRead(memberId: Long): Int {
        val updatedCount = notificationRepository.markAllAsReadByMemberId(memberId)

        // 모든 알림 읽음 처리를 실시간으로 전송
        sendAllReadStatusUpdate(memberId)

        return updatedCount
    }

    /**
     * 모든 알림 읽음 처리를 실시간으로 전송합니다
     */
    private fun sendAllReadStatusUpdate(memberId: Long) {
        try {
            val isConnected = sseEmitterService.isUserConnected(memberId)
            if (isConnected) {
                sseEmitterService.sendEventToUser(
                    memberId,
                    "all_notifications_read",
                    mapOf("message" to "모든 알림이 읽음 처리되었습니다.")
                )
            }
        } catch (e: Exception) {
            logger.error("모든 알림 읽음 처리 실시간 전송 중 오류 발생", e)
        }
    }
}