package picklab.backend.file

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.file.application.FileStoragePort
import picklab.backend.file.application.FileUploadUseCase
import picklab.backend.file.application.model.CreatePresignedUrlCommand
import picklab.backend.template.IntegrationTest
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*

class NcpObjectStorageTest(
    @Value("\${ncp.storage.bucket-name}")
    private val testBucketName: String,
) : IntegrationTest() {
    @Autowired
    private lateinit var s3Client: S3Client

    @Autowired
    private lateinit var fileStoragePort: FileStoragePort

    @Autowired
    private lateinit var fileUploadUseCase: FileUploadUseCase

    private val testKey = "temp/profile/1/test_20231201_120000.jpg"
    private val permanentKey = "profile/1/test_20231201_120000.jpg"

    @BeforeEach
    fun setupBucket() {
        try {
            s3Client.createBucket(
                CreateBucketRequest
                    .builder()
                    .bucket(testBucketName)
                    .build(),
            )
        } catch (e: Exception) {
        }

        cleanupTestObjects()
    }

    private fun cleanupTestObjects() {
        try {
            val listRequest =
                ListObjectsV2Request
                    .builder()
                    .bucket(testBucketName)
                    .build()

            val response = s3Client.listObjectsV2(listRequest)
            response.contents().forEach { obj ->
                s3Client.deleteObject(
                    DeleteObjectRequest
                        .builder()
                        .bucket(testBucketName)
                        .key(obj.key())
                        .build(),
                )
            }
        } catch (e: Exception) {
        }
    }

    private fun putTestObject(
        key: String,
        content: String = "test content",
    ) {
        s3Client.putObject(
            PutObjectRequest
                .builder()
                .bucket(testBucketName)
                .key(key)
                .build(),
            RequestBody.fromString(content),
        )
    }

    @Test
    @DisplayName("presigned URL 생성 테스트")
    fun `presigned URL 생성 UseCase 테스트`() {
        val command =
            CreatePresignedUrlCommand(
                fileName = "test.jpg",
                fileSize = 1024L,
                category = FileCategory.PROFILE,
                memberId = 1L,
                activityId = null,
            )

        val response = fileUploadUseCase.generateUploadPresignedUrl(command)

        assertThat(response.presignedUrl).isNotBlank()
    }

    @Test
    @DisplayName("임시 파일 존재 확인 테스트 - 성공")
    fun tempFileExistSuccess() {
        putTestObject(testKey)

        fileStoragePort.verifyTempFileExists(testKey)
    }

    @Test
    @DisplayName("임시 파일 존재 확인 테스트 - 파일 없음")
    fun tempFileExistFailure() {
        assertThatThrownBy {
            fileStoragePort.verifyTempFileExists("non-existent-key")
        }.isInstanceOf(BusinessException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_NOT_FOUND)
    }

    @Test
    @DisplayName("임시 파일을 영구 저장소로 이동 테스트")
    fun moveTempFileToPermanent() {
        putTestObject(testKey)

        val permanentUrl = fileStoragePort.moveTempFileToPermanent(testKey)

        assertThat(permanentUrl).contains(permanentKey)

        // 원본 임시 파일이 삭제되었는지 확인
        assertThatThrownBy {
            s3Client.headObject(
                HeadObjectRequest
                    .builder()
                    .bucket(testBucketName)
                    .key(testKey)
                    .build(),
            )
        }.isInstanceOf(NoSuchKeyException::class.java)

        // 영구 파일이 생성되었는지 확인
        val headResponse =
            s3Client.headObject(
                HeadObjectRequest
                    .builder()
                    .bucket(testBucketName)
                    .key(permanentKey)
                    .build(),
            )
        assertThat(headResponse).isNotNull
    }

    @Test
    @DisplayName("파일 삭제 테스트")
    fun deleteFileTest() {
        putTestObject(testKey)

        // 파일이 존재하는지 확인
        s3Client.headObject(
            HeadObjectRequest
                .builder()
                .bucket(testBucketName)
                .key(testKey)
                .build(),
        )

        fileStoragePort.deleteFile(testKey)

        // 파일이 삭제되었는지 확인
        assertThatThrownBy {
            s3Client.headObject(
                HeadObjectRequest
                    .builder()
                    .bucket(testBucketName)
                    .key(testKey)
                    .build(),
            )
        }.isInstanceOf(NoSuchKeyException::class.java)
    }

    @Test
    @DisplayName("특정 prefix로 시작하는 객체 키 목록 조회 테스트")
    fun getListSpecificPrefix() {
        val prefix = "temp/profile/1/"
        val keys =
            listOf(
                "temp/profile/1/file1.jpg",
                "temp/profile/1/file2.jpg",
                "temp/profile/2/file3.jpg",
            )

        keys.forEach { putTestObject(it) }

        val result = fileStoragePort.listObjectKeys(prefix)

        assertThat(result).hasSize(2)
        assertThat(result).containsExactlyInAnyOrder(
            "temp/profile/1/file1.jpg",
            "temp/profile/1/file2.jpg",
        )
    }
}
