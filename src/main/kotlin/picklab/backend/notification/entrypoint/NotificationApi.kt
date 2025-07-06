package picklab.backend.notification.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.notification.entrypoint.request.NotificationCreateRequest
import picklab.backend.notification.entrypoint.response.NotificationResponse

@Tag(name = "알림", description = "알림 관련 API 문서 입니다.")
interface NotificationApi {

    @Operation(
        summary = "SSE 알림 구독",
        description = "실시간 알림을 받기 위한 SSE 연결을 생성합니다."
    )
    @ApiResponse(responseCode = "200", description = "SSE 연결 성공")
    @GetMapping("/notifications/subscribe")
    fun subscribeNotifications(
        @AuthenticationPrincipal memberPrincipal: MemberPrincipal
    ): SseEmitter

    @Operation(
        summary = "알림 전송",
        description = "특정 사용자에게 알림을 전송합니다."
    )
    @ApiResponse(responseCode = "200", description = "알림 전송 성공")
    @PostMapping("/notifications/send")
    fun sendNotification(
        @RequestBody request: NotificationCreateRequest
    ): ResponseWrapper<NotificationResponse>

    @Operation(
        summary = "내 알림 목록 조회",
        description = "현재 로그인한 사용자의 알림 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공")
    @GetMapping("/notifications")
    fun getMyNotifications(
        @AuthenticationPrincipal memberPrincipal: MemberPrincipal,
        pageable: Pageable
    ): ResponseWrapper<Page<NotificationResponse>>

    @Operation(
        summary = "최근 n일 내 알림 조회",
        description = "현재 로그인한 사용자의 최근 n일 내 알림을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "최근 알림 조회 성공")
    @GetMapping("/notifications/recent")
    fun getRecentNotifications(
        @AuthenticationPrincipal memberPrincipal: MemberPrincipal,
        @Parameter(description = "조회할 일 수", example = "7")
        @RequestParam(defaultValue = "7") days: Int
    ): ResponseWrapper<List<NotificationResponse>>

    @Operation(
        summary = "알림 읽음 처리",
        description = "특정 알림을 읽음 상태로 변경합니다."
    )
    @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공")
    @PatchMapping("/notifications/{notificationId}/read")
    fun markAsRead(
        @Parameter(description = "알림 ID") @PathVariable notificationId: Long,
        @AuthenticationPrincipal memberPrincipal: MemberPrincipal
    ): ResponseWrapper<NotificationResponse>

    @Operation(
        summary = "모든 알림 읽음 처리",
        description = "현재 로그인한 사용자의 모든 알림을 읽음 상태로 변경합니다."
    )
    @ApiResponse(responseCode = "200", description = "모든 알림 읽음 처리 성공")
    @PatchMapping("/notifications/read-all")
    fun markAllAsRead(
        @AuthenticationPrincipal memberPrincipal: MemberPrincipal
    ): ResponseWrapper<Unit>

    @Operation(
        summary = "읽지 않은 알림 개수 조회",
        description = "현재 로그인한 사용자의 읽지 않은 알림 개수를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "읽지 않은 알림 개수 조회 성공")
    @GetMapping("/notifications/unread-count")
    fun getUnreadCount(
        @AuthenticationPrincipal memberPrincipal: MemberPrincipal
    ): ResponseWrapper<Long>
}