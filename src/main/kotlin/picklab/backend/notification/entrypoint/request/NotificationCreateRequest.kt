package picklab.backend.notification.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.notification.domain.entity.NotificationType

@Schema(description = "알림 생성 요청")
data class NotificationCreateRequest(
    @Schema(description = "수신자 회원 ID", example = "1")
    val receiverId: Long,
    
    @Schema(description = "알림 제목", example = "새로운 활동이 등록되었습니다")
    val title: String,
    
    @Schema(description = "알림 타입", example = "ACTIVITY_CREATED")
    val type: NotificationType,
    
    @Schema(description = "클릭 시 이동할 링크", example = "/activities/123")
    val link: String
) 