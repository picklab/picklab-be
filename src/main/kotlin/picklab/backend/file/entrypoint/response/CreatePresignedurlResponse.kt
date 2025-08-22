package picklab.backend.file.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

data class CreatePresignedurlResponse(
    @Schema(description = "PUT요청용 Presgined Url")
    val presignedUrl: String,
    @Schema(description = "이미지 업로드 완료 시 접근 가능한 Public URL")
    val publicReadUrl: String,
)
