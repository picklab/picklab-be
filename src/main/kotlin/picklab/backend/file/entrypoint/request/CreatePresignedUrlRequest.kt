package picklab.backend.file.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import picklab.backend.file.FileCategory
import picklab.backend.file.application.model.CreatePresignedUrlCommand

data class CreatePresignedUrlRequest(
    @field:NotBlank(message = "파일 이름은 필수입니다.")
    @field:Schema(description = "업로드 할 파일 전체 이름(확장자 포함)", example = "example.jpg")
    val fileName: String,
    @field:Schema(description = "업로드 파일 카테고리", example = "PROFILE")
    val category: FileCategory,
    @field:Positive(message = "파일 크기는 0보다 커야 합니다.")
    @field:Schema(description = "업로드 할 파일 크기(Byte)", example = "2048")
    val fileSize: Long,
    @field:Schema(description = "관련 활동 ID (REVIEW 및 ARCHIVE 필수, PROFILE 미사용)", example = "1")
    val activityId: Long? = null,
) {
    fun toCommand(memberId: Long): CreatePresignedUrlCommand =
        CreatePresignedUrlCommand(
            fileName = this.fileName,
            category = this.category,
            fileSize = this.fileSize,
            activityId = this.activityId,
            memberId = memberId,
        )
}
