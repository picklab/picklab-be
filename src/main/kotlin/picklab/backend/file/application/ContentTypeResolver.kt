package picklab.backend.file.application

import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode

object ContentTypeResolver {
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

    /**
     * 파일명에서 확장자를 추출하여 해당하는 Content-Type을 반환
     */
    fun resolveContentType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return extensionToContentType[extension] ?: throw BusinessException(ErrorCode.UNSUPPORTED_FILE_EXTENSION)
    }
}
