package picklab.backend.file.application

import org.springframework.stereotype.Component
import picklab.backend.file.FileKeyGenerator
import picklab.backend.file.application.model.CreatePresignedUrlCommand
import picklab.backend.file.entrypoint.response.CreatePresignedurlResponse

@Component
class FileUploadUseCase(
    private val fileStoragePort: FileStoragePort,
) {
    /**
     * 파일 업로드를 위한 presigned URL을 반환합니다.
     */
    fun generateUploadPresignedUrl(command: CreatePresignedUrlCommand): CreatePresignedurlResponse {
        val contentType = ContentTypeResolver.resolveContentType(command.fileName)

        val fileKey = FileKeyGenerator.generateTempFileKey(command.fileName, command.category.name, command.memberId)

        val presignedUrl = fileStoragePort.generateUploadPresignedUrl(contentType, fileKey, command.fileSize)

        return CreatePresignedurlResponse(
            presignedUrl = presignedUrl,
        )
    }
}
