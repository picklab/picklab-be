package picklab.backend.file

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class FileKeyGenerator(
    @Value("\${oci.storage.bucket-name}")
    private val bucketName: String,
) {
    /**
     * 파일을 임시저장하는 key를 생성합니다. 임시저장된 파일의 경우 이후 클라이언트의 요청을 받아 업로드가 정상적으로 완료된 파일인지 확인합니다.
     * - PROFILE 카테고리 : temp/profile/{memberId}/{uuid}_{timestamp}.{extension}
     * - REVIEW, ARCHIVE 카테고리 : temp/{category}/{memberId}/{activityId}/{uuid}_{timestamp}.{extension}
     */
    fun generateTempFileKey(
        fileName: String,
        category: FileCategory,
        memberId: Long,
        activityId: Long?,
    ): String {
        val extension = fileName.substringAfterLast('.', "")
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")).toString()
        val uuid = UUID.randomUUID().toString().substring(0, 8)

        return if (category == FileCategory.PROFILE) {
            "temp/${category.name.lowercase()}/$memberId/${uuid}_$timestamp.$extension"
        } else {
            if (activityId == null) {
                throw BusinessException(ErrorCode.NEED_ACTIVITY_ID)
            }
            "temp/${category.name.lowercase()}/$memberId/$activityId/${uuid}_$timestamp.$extension"
        }
    }

    fun extractFileKeyFromUrl(url: String): String = url.substringAfter("$bucketName/")
}
