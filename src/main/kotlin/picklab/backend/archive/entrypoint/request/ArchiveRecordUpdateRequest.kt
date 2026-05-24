package picklab.backend.archive.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.archive.domain.enums.DetailRoleType
import picklab.backend.archive.domain.enums.RoleType
import java.time.LocalDate

class ArchiveRecordUpdateRequest(
    @field:Schema(description = "활동 기록")
    val activityRecord: String,
    @field:Schema(description = "활동 역할")
    val role: RoleType,
    @field:Schema(description = "상세 역할")
    val detailRole: DetailRoleType,
    @field:Schema(description = "상세 역할에서 기타를 선택하여 직접 입력한 역할")
    val customRole: String?,
    @field:Schema(description = "활동 파일 URLs (기존 영구 URL + 신규 임시 URL 혼용 가능)")
    val fileUrls: List<String>,
    @field:Schema(description = "활동 연관 URLs")
    val referenceUrls: List<String>,
    @field:Schema(description = "활동 시작일")
    val startDate: LocalDate,
    @field:Schema(description = "활동 종료일")
    val endDate: LocalDate,
)
