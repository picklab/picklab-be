package picklab.backend.participation.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.participation.domain.entity.ActivityParticipation
import picklab.backend.participation.domain.enums.ApplicationStatus
import picklab.backend.participation.domain.enums.ProgressStatus
import java.time.LocalDate
import java.time.LocalDateTime

data class ActivityParticipationResultResponse(
    @field:Schema(description = "활동 참여 ID")
    val participationId: Long,
    @field:Schema(description = "활동 ID")
    val activityId: Long,
    @field:Schema(description = "활동명")
    val title: String,
    @field:Schema(description = "주최 기관/단체명")
    val organizer: String?,
    @field:Schema(description = "활동 유형")
    val activityType: String?,
    @field:Schema(description = "활동 썸네일 이미지 URL")
    val thumbnailUrl: String?,
    @field:Schema(description = "지원 시작일")
    val recruitmentStartDate: LocalDate,
    @field:Schema(description = "지원 종료일")
    val recruitmentEndDate: LocalDate?,
    @field:Schema(description = "활동 시작일")
    val activityStartDate: LocalDate,
    @field:Schema(description = "활동 종료일")
    val activityEndDate: LocalDate?,
    @field:Schema(description = "지원 상태")
    val applicationStatus: ApplicationStatus,
    @field:Schema(description = "진행 상태")
    val progressStatus: ProgressStatus,
    @field:Schema(description = "리뷰 작성 가능 여부")
    val canWriteReview: Boolean,
    @field:Schema(description = "지원 완료 표시 일시")
    val appliedAt: LocalDateTime,
) {
    companion object {
        fun from(participation: ActivityParticipation): ActivityParticipationResultResponse {
            val activity = participation.activity
            return ActivityParticipationResultResponse(
                participationId = participation.id,
                activityId = activity.id,
                title = activity.title,
                organizer = activity.organizer,
                activityType = activity.activityType,
                thumbnailUrl = activity.activityThumbnailUrl,
                recruitmentStartDate = activity.recruitmentStartDate,
                recruitmentEndDate = activity.recruitmentEndDate,
                activityStartDate = activity.startDate,
                activityEndDate = activity.endDate,
                applicationStatus = participation.applicationStatus,
                progressStatus = participation.progressStatus,
                canWriteReview = participation.canWriteReview(),
                appliedAt = participation.createdAt,
            )
        }
    }
}
