package picklab.backend.participation.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

data class GetActivityApplicationUrlResponse(
    @Schema(description = "활동 지원 URL")
    val applicationUrl: String,
)
