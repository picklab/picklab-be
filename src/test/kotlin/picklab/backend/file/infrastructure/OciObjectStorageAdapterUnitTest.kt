package picklab.backend.file.infrastructure

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.CopyObjectResponse
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectResponse
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OciObjectStorageAdapterUnitTest {
    private val client = mockk<S3Client>()
    private val adapter = OciObjectStorageAdapter(mockk<S3Presigner>(), client, "https://storage.example", "bucket")
    private val tempKey = "temp/review/1/10/abcd1234_20261002_120000.jpg"
    private val permanentKey = tempKey.removePrefix("temp/")

    @Test
    fun `HEAD 성공이면 파일 존재 검증을 통과한다`() {
        every { client.headObject(any<HeadObjectRequest>()) } returns HeadObjectResponse.builder().build()

        adapter.verifyTempFileExists(tempKey)

        verify(exactly = 1) { client.headObject(match<HeadObjectRequest> { it.bucket() == "bucket" && it.key() == tempKey }) }
    }

    @Test
    fun `일반 S3 HEAD 404도 FILE_NOT_FOUND로 처리한다`() {
        every { client.headObject(any<HeadObjectRequest>()) } throws S3Exception.builder().statusCode(404).build()

        val error = assertFailsWith<BusinessException> { adapter.verifyTempFileExists(tempKey) }

        assertEquals(ErrorCode.FILE_NOT_FOUND, error.errorCode)
    }

    @ParameterizedTest
    @ValueSource(ints = [403, 500])
    fun `권한과 저장소 오류는 파일 없음으로 처리하지 않는다`(status: Int) {
        every { client.headObject(any<HeadObjectRequest>()) } throws S3Exception.builder().statusCode(status).build()

        val error = assertFailsWith<BusinessException> { adapter.verifyTempFileExists(tempKey) }

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, error.errorCode)
    }

    @Test
    fun `통신 실패는 저장소 오류로 처리한다`() {
        every { client.headObject(any<HeadObjectRequest>()) } throws SdkClientException.create("connection failed")

        val error = assertFailsWith<BusinessException> { adapter.verifyTempFileExists(tempKey) }

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, error.errorCode)
    }

    @Test
    fun `영구 키 이동은 OCI 복사 성공 후 원본을 삭제한다`() {
        stubMove()

        assertEquals(permanentKey, adapter.moveTempFileToPermanentKey(tempKey))
        verifyOrder {
            client.copyObject(
                match<CopyObjectRequest> {
                    it.sourceBucket() == "bucket" &&
                        it.sourceKey() == tempKey &&
                        it.destinationBucket() == "bucket" &&
                        it.destinationKey() == permanentKey
                },
            )
            client.deleteObject(match<DeleteObjectRequest> { it.bucket() == "bucket" && it.key() == tempKey })
        }
    }

    @Test
    fun `기존 URL 기반 이동 메서드는 프로필과 아카이브 응답 형식을 유지한다`() {
        stubMove()

        assertEquals("https://storage.example/bucket/$permanentKey", adapter.moveTempFileToPermanent(tempKey))
    }

    @Test
    fun `복사 실패 시 임시 원본을 삭제하지 않는다`() {
        every { client.copyObject(any<CopyObjectRequest>()) } throws S3Exception.builder().statusCode(500).build()

        val error = assertFailsWith<BusinessException> { adapter.moveTempFileToPermanentKey(tempKey) }

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, error.errorCode)
        verify(exactly = 0) { client.deleteObject(any<DeleteObjectRequest>()) }
    }

    private fun stubMove() {
        every { client.copyObject(any<CopyObjectRequest>()) } returns CopyObjectResponse.builder().build()
        every { client.deleteObject(any<DeleteObjectRequest>()) } returns DeleteObjectResponse.builder().build()
    }
}
