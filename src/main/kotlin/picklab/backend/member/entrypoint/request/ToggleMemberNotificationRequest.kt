package picklab.backend.member.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.member.domain.enums.NotificationType

data class ToggleMemberNotificationRequest(
    @field:Schema(description = "알림 타입", example = "POPULAR")
    val type: NotificationType,
)
