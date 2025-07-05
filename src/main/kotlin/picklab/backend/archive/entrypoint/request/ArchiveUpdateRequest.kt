package picklab.backend.archive.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.archive.domain.enums.PassOrFailStatus
import picklab.backend.archive.domain.enums.ProgressStatus

class ArchiveUpdateRequest(
    @field:Schema(description = "활동 진행 상태")
    val activityProgressStatus: ProgressStatus,
    @field:Schema(description = "합격/불합격 상태")
    val passOrFailStatus: PassOrFailStatus,
)