package picklab.backend.archive.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.archive.domain.entity.Archive
import picklab.backend.archive.domain.enums.WriteStatus
import picklab.backend.participation.domain.entity.ActivityParticipation
import java.time.LocalDate

data class ArchiveActivityResponse(
    @field:Schema(description = "아카이브 ID")
    val archiveId: Long?,
    @field:Schema(description = "활동 참여 ID")
    val activityParticipationId: Long,
    @field:Schema(description = "공고보기용 원본 활동 ID")
    val activityId: Long,
    @field:Schema(description = "활동 썸네일 이미지 URL")
    val activityThumbnailUrl: String?,
    @field:Schema(description = "활동 유형")
    val activityType: String,
    @field:Schema(description = "활동명")
    val title: String,
    @field:Schema(description = "주최기관/단체명")
    val organizer: String?,
    @field:Schema(description = "활동 시작일")
    val startDate: LocalDate,
    @field:Schema(description = "활동 종료일")
    val endDate: LocalDate?,
    @field:Schema(description = "작성 여부")
    val writeStatus: WriteStatus,
) {
    companion object {
        fun from(
            participation: ActivityParticipation,
            archive: Archive?,
        ): ArchiveActivityResponse {
            val activity = participation.activity
            return ArchiveActivityResponse(
                archiveId = archive?.id,
                activityParticipationId = participation.id,
                activityId = activity.id,
                activityThumbnailUrl = activity.activityThumbnailUrl,
                activityType = activity.activityType ?: "",
                title = activity.title,
                organizer = activity.organizer,
                startDate = archive?.userStartDate ?: activity.startDate,
                endDate = archive?.userEndDate ?: activity.endDate,
                writeStatus = archive?.writeStatus ?: WriteStatus.NOT_WRITTEN,
            )
        }

        fun from(archive: Archive): ArchiveActivityResponse =
            ArchiveActivityResponse(
                archiveId = archive.id,
                activityParticipationId = archive.participation.id,
                activityId = archive.participation.activity.id,
                activityThumbnailUrl = archive.participation.activity.activityThumbnailUrl,
                activityType = archive.participation.activity.activityType ?: "",
                title = archive.participation.activity.title,
                organizer = archive.participation.activity.organizer,
                startDate = archive.userStartDate,
                endDate = archive.userEndDate,
                writeStatus = archive.writeStatus,
            )
    }
}
