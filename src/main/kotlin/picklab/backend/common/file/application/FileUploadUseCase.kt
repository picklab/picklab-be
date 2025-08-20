package picklab.backend.common.file.application

import org.springframework.stereotype.Component
import picklab.backend.common.file.FileKeyGenerator
import picklab.backend.common.file.application.model.CreatePresignedUrlCommand
import picklab.backend.common.file.entrypoint.response.CreatePresignedurlResponse

@Component
class FileUploadUseCase(
    private val fileStoragePort: FileStoragePort,
) {
    /**
     * 파일 업로드를 위한 presigned URL 및 public Read URL을 반환합니다.
     */
    fun generateUploadPresignedUrl(command: CreatePresignedUrlCommand): CreatePresignedurlResponse {
        val contentType = ContentTypeResolver.resolveContentType(command.fileName)

        val fileKey = FileKeyGenerator.generateFileKey(command.fileName, command.category.name, command.memberId)

        val presignedUrl = fileStoragePort.generateUploadPresignedUrl(contentType, fileKey)
        val publicReadUrl = fileStoragePort.getPublicReadUrl(fileKey)

        return CreatePresignedurlResponse(
            presignedUrl = presignedUrl,
            publicReadUrl = publicReadUrl,
        )
    }
}
