package picklab.backend.file.application

import org.springframework.stereotype.Component
import picklab.backend.file.FileKeyGenerator
import picklab.backend.file.application.model.CreatePresignedUrlCommand
import picklab.backend.file.entrypoint.response.CreatePresignedurlResponse

@Component
class FileUploadUseCase(
    private val fileStoragePort: FileStoragePort,
    private val fileKeyGenerator: FileKeyGenerator,
    private val fileValidator: FileValidator,
) {
    /**
     * 파일 업로드를 위한 presigned URL을 반환합니다.
     */
    fun generateUploadPresignedUrl(command: CreatePresignedUrlCommand): CreatePresignedurlResponse {
        fileValidator.validateFileSize(command.fileSize)

        val contentType = fileValidator.validateExtensionAndResolveContentType(command.fileName)

        val fileKey =
            fileKeyGenerator.generateTempFileKey(
                command.fileName,
                command.category,
                command.memberId,
                command.activityId,
            )

        val presignedUrl = fileStoragePort.generateUploadPresignedUrl(contentType, fileKey, command.fileSize)

        return CreatePresignedurlResponse(
            presignedUrl = presignedUrl,
        )
    }
}
