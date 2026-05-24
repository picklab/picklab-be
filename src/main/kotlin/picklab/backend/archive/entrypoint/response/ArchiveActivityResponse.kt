package picklab.backend.archive.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.archive.domain.entity.Archive
import picklab.backend.archive.domain.enums.WriteStatus
import java.time.LocalDate

data class ArchiveActivityResponse(
    @field:Schema(description = "아카이브 ID")
    val id: Long,
    @field:Schema(description = "원본 활동 ID")
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
    val userStartDate: LocalDate,
    @field:Schema(description = "활동 종료일")
    val userEndDate: LocalDate,
    @field:Schema(description = "작성 여부")
    val writeStatus: WriteStatus,
) {
    companion object {
        fun from(archive: Archive): ArchiveActivityResponse =
            ArchiveActivityResponse(
                id = archive.id,
                activityId = archive.activity.id,
                activityThumbnailUrl = archive.activity.activityThumbnailUrl,
                activityType = archive.activityType.name,
                title = archive.activity.title,
                organizer = archive.activity.organizer,
                userStartDate = archive.userStartDate,
                userEndDate = archive.userEndDate,
                writeStatus = archive.writeStatus,
            )
    }
}
