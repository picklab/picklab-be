package picklab.backend.common.file.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import picklab.backend.common.file.FileCategory
import picklab.backend.common.file.application.model.CreatePresignedUrlCommand

data class CreatePresignedUrlRequest(
    @NotBlank(message = "파일 이름은 필수입니다.")
    @Schema(description = "업로드 할 파일 전체 이름(확장자 포함)", example = "example.jpg")
    val fileName: String,
    @NotBlank(message = "카테고리는 필수입니다.")
    @Schema(description = "이미지가 업로드 되는 카테고리(PROFILE, ARCHIVE, REVIEW)", example = "PROFILE")
    val category: FileCategory,
) {
    fun toCommand(memberId: Long): CreatePresignedUrlCommand =
        CreatePresignedUrlCommand(
            fileName = this.fileName,
            category = this.category,
            memberId = memberId,
        )
}
