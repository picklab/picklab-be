package picklab.backend.member.entrypoint.request

import picklab.backend.member.domain.enums.NotificationType

data class ToggleMemberNotificationRequest(
    val type: NotificationType,
)
