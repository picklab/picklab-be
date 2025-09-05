package picklab.backend.file.application

import org.springframework.stereotype.Component
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode

@Component
class FileValidator {
    companion object {
        private const val MAX_FILE_SIZE = 50 * 1024 * 1024 // 50MB

        private val extensionToContentType =
            mapOf(
                "jpg" to "image/jpeg",
                "jpeg" to "image/jpeg",
                "png" to "image/png",
                "gif" to "image/gif",
                "psd" to "image/vnd.adobe.photoshop",
                "ai" to "application/postscript",
                "tif" to "image/tiff",
                "tiff" to "image/tiff",
                "hwp" to "application/octet-stream",
                "doc" to "application/msword",
                "docx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "ppt" to "application/vnd.ms-powerpoint",
                "pptx" to "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "pdf" to "application/pdf",
                "xls" to "application/vnd.ms-excel",
                "xlsx" to "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            )
    }

    fun validateFileSize(fileSize: Long) {
        if (fileSize > MAX_FILE_SIZE) {
            throw BusinessException(ErrorCode.FILE_SIZE_EXCEEDED)
        }
    }

    /**
     * 파일명 확장자를 검증하고 해당하는 Content-Type을 반환합니다.
     */
    fun validateExtensionAndResolveContentType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return extensionToContentType[extension]
            ?: throw BusinessException(ErrorCode.UNSUPPORTED_FILE_EXTENSION)
    }
}
