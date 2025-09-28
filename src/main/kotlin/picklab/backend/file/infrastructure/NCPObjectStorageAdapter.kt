package picklab.backend.file.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.common.util.logger
import picklab.backend.file.application.FileStoragePort
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

@Service
class NCPObjectStorageAdapter(
    private val presigner: S3Presigner,
    private val s3client: S3Client,
    @Value("\${ncp.cloud.end-point:https://kr.object.ncloudstorage.com}")
    private val endPoint: String,
    @Value("\${ncp.storage.bucket-name}")
    private val bucketName: String,
) : FileStoragePort {
    val logger = logger()

    override fun generateUploadPresignedUrl(
        contentType: String,
        key: String,
        fileSize: Long,
    ): String {
        val putObjectRequest =
            PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .contentLength(fileSize)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build()

        val presignRequest =
            PutObjectPresignRequest
                .builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build()

        val presignedRequest = presigner.presignPutObject(presignRequest)

        return presignedRequest.url().toString()
    }

    override fun verifyTempFileExists(key: String) {
        try {
            val headObjectRequest =
                HeadObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()

            s3client.headObject(headObjectRequest)
        } catch (e: NoSuchKeyException) {
            throw BusinessException(ErrorCode.FILE_NOT_FOUND)
        }
    }

    override fun moveTempFileToPermanent(key: String): String {
        val permanentKey = key.removePrefix("temp/")

        try {
            val copyRequest =
                CopyObjectRequest
                    .builder()
                    .sourceBucket(bucketName)
                    .sourceKey(key)
                    .destinationBucket(bucketName)
                    .destinationKey(permanentKey)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build()

            s3client.copyObject(copyRequest)

            deleteFile(key)

            return "$endPoint/$bucketName/$permanentKey"
        } catch (e: Exception) {
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    override fun deleteFile(key: String) {
        try {
            val deleteRequest =
                DeleteObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()

            s3client.deleteObject(deleteRequest)
        } catch (e: Exception) {
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    override fun listObjectKeys(prefix: String): List<String> {
        try {
            val listRequest =
                ListObjectsV2Request
                    .builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .build()

            val response = s3client.listObjectsV2(listRequest)
            return response.contents().map { it.key() }
        } catch (e: Exception) {
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    override fun moveValidTempFilesToPermanent(
        requestedKeys: List<String>,
        availableKeys: List<String>,
    ): List<String> {
        val validKeys = requestedKeys.filter { it in availableKeys }
        val movedUrls = mutableListOf<String>()

        validKeys.forEach { key ->
            try {
                val url = moveTempFileToPermanent(key)
                movedUrls.add(url)
            } catch (e: Exception) {
                logger.warn("파일 이동 실패 $key: ${e.message}")
            }
        }

        return movedUrls
    }
}
