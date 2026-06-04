package picklab.backend.participation.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.participation.domain.enums.ApplicationStatus

data class UpdateApplicationStatusRequest(
    @field:Schema(description = "지원 상태", example = "ACCEPTED")
    val applicationStatus: ApplicationStatus,
)
