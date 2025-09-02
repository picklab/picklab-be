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

    /**
     * 파일을 임시저장하는 key를 생성합니다. 임시저장된 파일의 경우 이후 클라이언트의 요청을 받아 업로드가 정상적으로 완료된 파일인지 확인합니다.
     */
    fun generateTempFileKey(
        fileName: String,
        category: String,
        memberId: Long,
    ): String {
        val extension = fileName.substringAfterLast('.', "")
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")).toString()
        val uuid = UUID.randomUUID().toString().substring(0, 8)

        return "temp/${category.lowercase()}/$memberId/${uuid}_$timestamp.$extension"
    }
}
