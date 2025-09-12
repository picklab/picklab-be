package picklab.backend.file.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

data class CreatePresignedurlResponse(
    @Schema(description = "PUT요청용 Presgined Url")
    val presignedUrl: String,
)
