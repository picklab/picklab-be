package picklab.backend.review.application.service

import org.springframework.stereotype.Service
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.file.application.FileStoragePort

@Service
class ReviewEvidenceService(
    private val fileStoragePort: FileStoragePort,
) {
    fun confirmUpload(
        objectKey: String?,
        memberId: Long,
        activityId: Long,
    ): String? {
        if (objectKey.isNullOrBlank()) return null

        validateKey(objectKey, "temp/review/$memberId/$activityId/")
        fileStoragePort.verifyTempFileExists(objectKey)
        return fileStoragePort.moveTempFileToPermanentKey(objectKey)
    }

    fun confirmUpdate(
        objectKey: String?,
        originalObjectKey: String?,
        memberId: Long,
        originalActivityId: Long,
        activityId: Long,
    ): String? {
        if (objectKey.isNullOrBlank()) return null

        if (objectKey == originalObjectKey && !objectKey.startsWith("temp/") && activityId == originalActivityId) {
            validateKey(objectKey, "review/$memberId/$activityId/")
            return objectKey
        }

        return confirmUpload(objectKey, memberId, activityId)
    }

    private fun validateKey(
        objectKey: String,
        prefix: String,
    ) {
        if (!objectKey.startsWith(prefix) || !FILE_NAME.matches(objectKey.removePrefix(prefix))) {
            throw BusinessException(ErrorCode.INVALID_REVIEW_EVIDENCE_KEY)
        }
    }

    companion object {
        private val FILE_NAME = Regex("[a-f0-9]{8}_\\d{8}_\\d{6}\\.[a-zA-Z0-9]+")
    }
}
