package picklab.backend.file.application

import org.springframework.stereotype.Service
import picklab.backend.file.FileKeyGenerator

@Service
class FileManagementService(
    private val fileStoragePort: FileStoragePort,
    private val fileKeyGenerator: FileKeyGenerator,
) {
    /**
     * 입력으로 들어온 public read url에 해당하는 임시파일이 존재하는 지 파악한 후,
     * 존재할 경우 정상이라고 판단하여 임시저장된 파일을 영구저장소로 이동시킵니다.
     * @return 영구 저장소로 이동된 파일의 public read URL
     */
    fun verifyTempFileAndMoveToPermanent(fileUrl: String): String {
        val key = fileKeyGenerator.extractFileKeyFromUrl(fileUrl)
        fileStoragePort.verifyTempFileExists(key)
        val publicReadUrl = fileStoragePort.moveTempFileToPermanent(key)
        return publicReadUrl
    }

    /**
     * 파일 URL로부터 키를 추출하여 해당 파일을 삭제합니다.
     */
    fun deleteFile(fileUrl: String) {
        val key = fileKeyGenerator.extractFileKeyFromUrl(fileUrl)
        fileStoragePort.deleteFile(key)
    }

    /**
     * 특정 멤버의 활동에 대한 임시 파일들을 일괄로 검증하고 영구저장소로 이동합니다.
     * @return 영구 저장소로 이동된 파일들의 public read URL 목록
     */
    fun verifyAndMoveTempFilesToPermanent(
        fileUrls: List<String>,
        memberId: Long,
        activityId: Long,
        category: String,
    ): List<String> {
        if (fileUrls.isEmpty()) return emptyList()

        val prefix = "temp/${category.lowercase()}/$memberId/$activityId/"
        val availableKeys = fileStoragePort.listObjectKeys(prefix)

        val requestedKeys = fileUrls.map { url -> fileKeyGenerator.extractFileKeyFromUrl(url) }

        return fileStoragePort.moveValidTempFilesToPermanent(requestedKeys, availableKeys)
    }
}
