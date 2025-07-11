package picklab.backend.notification.entrypoint

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.notification.application.NotificationUseCase
import picklab.backend.notification.entrypoint.request.NotificationCreateRequest
import picklab.backend.notification.entrypoint.response.NotificationResponse

@RestController
class NotificationController(
    private val notificationUseCase: NotificationUseCase
) : NotificationApi {

    @GetMapping("/notifications/subscribe")
    override fun subscribeNotifications(
        @AuthenticationPrincipal memberPrincipal: MemberPrincipal
    ): SseEmitter {
        return notificationUseCase.subscribeNotifications(memberPrincipal.memberId)
    }

    @PostMapping("/notifications/send")
    override fun sendNotification(
        @RequestBody request: NotificationCreateRequest
    ): ResponseWrapper<NotificationResponse> {
        val response = notificationUseCase.sendNotification(request)
        return ResponseWrapper.success(SuccessCode.SEND_NOTIFICATION_SUCCESS, response)
    }

    @GetMapping("/notifications")
    override fun getMyNotifications(
        @AuthenticationPrincipal memberPrincipal: MemberPrincipal,
        pageable: Pageable
    ): ResponseWrapper<Page<NotificationResponse>> {
        val notifications = notificationUseCase.getMyNotifications(memberPrincipal.memberId, pageable)
        return ResponseWrapper.success(SuccessCode.GET_NOTIFICATIONS_SUCCESS, notifications)
    }

    @GetMapping("/notifications/recent")
    override fun getRecentNotifications(
        @AuthenticationPrincipal memberPrincipal: MemberPrincipal,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseWrapper<List<NotificationResponse>> {
        val notifications = notificationUseCase.getRecentNotifications(memberPrincipal.memberId, days)
        return ResponseWrapper.success(SuccessCode.GET_RECENT_NOTIFICATIONS_SUCCESS, notifications)
    }

    @PatchMapping("/notifications/{notificationId}/read")
    override fun markAsRead(
        @PathVariable notificationId: Long,
        @AuthenticationPrincipal memberPrincipal: MemberPrincipal
    ): ResponseWrapper<NotificationResponse> {
        val response = notificationUseCase.markAsRead(notificationId, memberPrincipal.memberId)
        return ResponseWrapper.success(SuccessCode.MARK_NOTIFICATION_READ_SUCCESS, response)
    }

    @PatchMapping("/notifications/read-all")
    override fun markAllAsRead(
        @AuthenticationPrincipal memberPrincipal: MemberPrincipal
    ): ResponseWrapper<Unit> {
        notificationUseCase.markAllAsRead(memberPrincipal.memberId)
        return ResponseWrapper.success(SuccessCode.MARK_ALL_NOTIFICATIONS_READ_SUCCESS)
    }

    @DeleteMapping("/notifications")
    override fun deleteAllByMember(@AuthenticationPrincipal memberPrincipal: MemberPrincipal): ResponseWrapper<Unit> {
        notificationUseCase.deleteAllByMember(memberPrincipal.memberId)
        return ResponseWrapper.success(SuccessCode.DELETE_ALL_MEMBER_NOTIFICATION)
    }
}