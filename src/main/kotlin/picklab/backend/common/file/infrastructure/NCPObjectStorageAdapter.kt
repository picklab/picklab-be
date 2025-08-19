package picklab.backend.common.file.infrastructure

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import picklab.backend.common.file.application.FileStoragePort
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

@Service
class NCPObjectStorageAdapter(
    @Qualifier("ncpObjectStoragePresigner")
    private val presigner: S3Presigner,
    @Value("\${ncp.cloud.end-point:https://kr.object.ncloudstorage.com}")
    private val endPoint: String,
    @Value("\${ncp.storage.bucket-name}")
    private val bucketName: String,
) : FileStoragePort {
    override fun generateUploadPresignedUrl(
        contentType: String,
        key: String,
    ): String {
        // content-length 제한을 적용할 지 프론트측과 논의 필요
        val putObjectRequest =
            PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
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

    override fun getPublicReadUrl(key: String): String = "$endPoint/$bucketName/$key"
}
