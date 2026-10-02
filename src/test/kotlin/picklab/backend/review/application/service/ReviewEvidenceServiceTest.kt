package picklab.backend.review.application.service

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.file.application.FileStoragePort
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ReviewEvidenceServiceTest {
    private val storage = mockk<FileStoragePort>()
    private val service = ReviewEvidenceService(storage)
    private val tempKey = "temp/review/1/10/abcd1234_20261002_120000.jpg"
    private val permanentKey = tempKey.removePrefix("temp/")

    @Test
    fun `업로드된 내 활동의 인증 자료만 확인 후 영구 키로 이동한다`() {
        every { storage.verifyTempFileExists(tempKey) } just runs
        every { storage.moveTempFileToPermanentKey(tempKey) } returns permanentKey

        assertEquals(permanentKey, service.confirmUpload(tempKey, 1, 10))
        verifyOrder {
            storage.verifyTempFileExists(tempKey)
            storage.moveTempFileToPermanentKey(tempKey)
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "temp/review/2/10/abcd1234_20261002_120000.jpg",
            "temp/review/1/11/abcd1234_20261002_120000.jpg",
            "temp/archive/1/10/abcd1234_20261002_120000.jpg",
            "review/1/10/abcd1234_20261002_120000.jpg",
            "https://storage.example/bucket/temp/review/1/10/abcd1234_20261002_120000.jpg",
            "temp/review/1/10/../11/abcd1234_20261002_120000.jpg",
            "temp/review/1/10/abcd1234_20261002_120000.jpg?signature=test",
        ],
    )
    fun `사용자 활동 카테고리와 키 형식이 다르면 저장소 호출 전에 거절한다`(key: String) {
        val error = assertFailsWith<BusinessException> { service.confirmUpload(key, 1, 10) }

        assertEquals(ErrorCode.INVALID_REVIEW_EVIDENCE_KEY, error.errorCode)
        verify(exactly = 0) { storage.verifyTempFileExists(any()) }
        verify(exactly = 0) { storage.moveTempFileToPermanentKey(any()) }
    }

    @Test
    fun `HEAD에서 파일이 없으면 이동하지 않는다`() {
        every { storage.verifyTempFileExists(tempKey) } throws BusinessException(ErrorCode.FILE_NOT_FOUND)

        val error = assertFailsWith<BusinessException> { service.confirmUpload(tempKey, 1, 10) }

        assertEquals(ErrorCode.FILE_NOT_FOUND, error.errorCode)
        verify(exactly = 0) { storage.moveTempFileToPermanentKey(any()) }
    }

    @Test
    fun `이동 실패는 성공한 인증 자료로 반환하지 않는다`() {
        every { storage.verifyTempFileExists(tempKey) } just runs
        every { storage.moveTempFileToPermanentKey(tempKey) } throws BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)

        val error = assertFailsWith<BusinessException> { service.confirmUpload(tempKey, 1, 10) }

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, error.errorCode)
    }

    @Test
    fun `현재 리뷰의 영구 키를 유지하면 다시 이동하지 않는다`() {
        assertEquals(permanentKey, service.confirmUpdate(permanentKey, permanentKey, 1, 10, 10))
        verify(exactly = 0) { storage.verifyTempFileExists(any()) }
        verify(exactly = 0) { storage.moveTempFileToPermanentKey(any()) }
    }

    @Test
    fun `같은 사용자의 다른 리뷰 영구 키도 새 인증 자료로 연결할 수 없다`() {
        val anotherKey = "review/1/10/ffff1234_20261002_120000.jpg"

        val error =
            assertFailsWith<BusinessException> {
                service.confirmUpdate(anotherKey, permanentKey, 1, 10, 10)
            }

        assertEquals(ErrorCode.INVALID_REVIEW_EVIDENCE_KEY, error.errorCode)
    }

    @Test
    fun `활동이 바뀌면 이전 활동의 영구 키를 유지할 수 없다`() {
        val error =
            assertFailsWith<BusinessException> {
                service.confirmUpdate(permanentKey, permanentKey, 1, 10, 11)
            }

        assertEquals(ErrorCode.INVALID_REVIEW_EVIDENCE_KEY, error.errorCode)
    }

    @Test
    fun `수정 시 새 임시 파일은 확인 후 영구 이동한다`() {
        every { storage.verifyTempFileExists(tempKey) } just runs
        every { storage.moveTempFileToPermanentKey(tempKey) } returns permanentKey

        assertEquals(permanentKey, service.confirmUpdate(tempKey, null, 1, 10, 10))
        verify(exactly = 1) { storage.verifyTempFileExists(tempKey) }
        verify(exactly = 1) { storage.moveTempFileToPermanentKey(tempKey) }
    }

    @Test
    fun `인증 자료가 없거나 제거되면 저장소를 호출하지 않는다`() {
        assertNull(service.confirmUpload(null, 1, 10))
        assertNull(service.confirmUpload(" ", 1, 10))
        assertNull(service.confirmUpdate(null, permanentKey, 1, 10, 10))
        verify(exactly = 0) { storage.verifyTempFileExists(any()) }
        verify(exactly = 0) { storage.moveTempFileToPermanentKey(any()) }
    }
}
