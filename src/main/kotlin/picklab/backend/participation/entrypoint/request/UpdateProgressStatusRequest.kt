package picklab.backend.participation.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.participation.domain.enums.ProgressStatus

data class UpdateProgressStatusRequest(
    @field:Schema(description = "진행 상태", example = "COMPLETED")
    val progressStatus: ProgressStatus,
)
