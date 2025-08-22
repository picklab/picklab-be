package picklab.backend.file

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object FileKeyGenerator {
    /**
     * 고유한 파일 키를 생성합니다.
     * key 구조: {category}/{memberId}/{uuid}_{timestamp}.{extension}
     */
    fun generateFileKey(
        fileName: String,
        category: String,
        memberId: Long,
    ): String {
        val extension = fileName.substringAfterLast('.', "")
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")).toString()
        val uuid = UUID.randomUUID().toString().substring(0, 8)

        return "${category.lowercase()}/$memberId/${uuid}_$timestamp.$extension"
    }
}
