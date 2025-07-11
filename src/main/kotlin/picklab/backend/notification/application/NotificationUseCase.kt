package picklab.backend.notification.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import picklab.backend.member.domain.MemberService
import picklab.backend.notification.domain.service.NotificationService
import picklab.backend.notification.domain.service.SseEmitterService
import picklab.backend.notification.entrypoint.request.NotificationCreateRequest
import picklab.backend.notification.entrypoint.response.NotificationResponse

@Component
class NotificationUseCase(
    private val notificationService: NotificationService,
    private val sseEmitterService: SseEmitterService,
    private val memberService: MemberService
) {

    /**
     * SSE 알림 구독
     */
    fun subscribeNotifications(memberId: Long): SseEmitter {
        return sseEmitterService.createEmitter(memberId)
    }

    /**
     * 알림 전송
     */
    fun sendNotification(request: NotificationCreateRequest): NotificationResponse {
        return notificationService.createAndSendNotification(request)
    }

    /**
     * 내 알림 목록 조회
     */
    fun getMyNotifications(memberId: Long, pageable: Pageable): Page<NotificationResponse> {
        return notificationService.getNotificationsByMemberId(memberId, pageable)
    }

    /**
     * 최근 n일 내 알림 조회
     */
    fun getRecentNotifications(memberId: Long, days: Int): List<NotificationResponse> {
        return notificationService.getRecentNotifications(memberId, days)
    }

    /**
     * 알림 읽음 처리
     */
    fun markAsRead(notificationId: Long, memberId: Long): NotificationResponse {
        return notificationService.markAsRead(notificationId, memberId)
    }

    /**
     * 모든 알림 읽음 처리
     */
    fun markAllAsRead(memberId: Long): Int {
        return notificationService.markAllAsRead(memberId)
    }

    @Transactional
    fun deleteAllByMember(memberId: Long) {
        val member = memberService.findActiveMember(memberId)
        val notifications = notificationService.findAllByMember(member)

        notifications.forEach {
            it.read()
            it.delete()
        }

        notificationService.saveAll(notifications)
    }
}