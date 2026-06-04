package picklab.backend.archive.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.archive.domain.entity.Archive
import picklab.backend.archive.domain.enums.DetailRoleType
import picklab.backend.archive.domain.enums.RoleType
import picklab.backend.archive.domain.enums.WriteStatus
import picklab.backend.participation.domain.entity.ActivityParticipation
import java.time.LocalDate

class ArchiveCreateRequest(
    @field:Schema(description = "활동 참여 ID")
    val participationId: Long,
    @field:Schema(description = "상세 역할")
    val detailRole: DetailRoleType,
    @field:Schema(description = "활동 기록")
    val activityRecord: String,
    @field:Schema(description = "활동 파일 URLs")
    val fileUrls: List<String>,
    @field:Schema(description = "활동 연관 URLs")
    val referenceUrls: List<String>,
    @field:Schema(description = "활동 시작일")
    val startDate: LocalDate,
    @field:Schema(description = "활동 종료일")
    val endDate: LocalDate,
    @field:Schema(description = "활동 역할")
    val role: RoleType,
    @field:Schema(description = "상세 역할에서 기타를 선택하여 직접 입력한 역할")
    val customRole: String?,
) {
    fun toCreateEntity(participation: ActivityParticipation): Archive =
        Archive(
            participation = participation,
            detailRole = detailRole,
            activityRecord = activityRecord,
            userStartDate = startDate,
            userEndDate = endDate,
            role = role,
            writeStatus = WriteStatus.COMPLETED,
            customRole = customRole,
        )
}
