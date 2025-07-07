package picklab.backend.notification.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.notification.domain.entity.Notification
import picklab.backend.notification.domain.entity.NotificationType
import java.time.LocalDateTime

@Schema(description = "알림 응답")
data class NotificationResponse(
    @Schema(description = "알림 ID", example = "1")
    val id: Long,

    @Schema(description = "알림 제목", example = "새로운 활동이 등록되었습니다")
    val title: String,

    @Schema(description = "알림 타입", example = "ACTIVITY_CREATED")
    val type: NotificationType,

    @Schema(description = "클릭 시 이동할 링크", example = "/activities/123")
    val link: String,

    @Schema(description = "읽음 여부", example = "false")
    val isRead: Boolean,

    @Schema(description = "생성일시", example = "2024-01-01T12:00:00")
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(notification: Notification): NotificationResponse {
            return NotificationResponse(
                id = notification.id,
                title = notification.title,
                type = notification.type,
                link = notification.link,
                isRead = notification.isRead,
                createdAt = notification.createdAt
            )
        }
    }
} 