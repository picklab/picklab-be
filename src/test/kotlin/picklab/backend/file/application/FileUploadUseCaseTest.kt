package picklab.backend.file.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.validation.Validation
import org.junit.jupiter.api.Test
import picklab.backend.file.FileCategory
import picklab.backend.file.FileKeyGenerator
import picklab.backend.file.application.model.CreatePresignedUrlCommand
import picklab.backend.file.entrypoint.request.CreatePresignedUrlRequest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FileUploadUseCaseTest {
    @Test
    fun `발급 응답의 객체 키는 실제 PUT 서명에 사용한 키와 같다`() {
        val storage = mockk<FileStoragePort>()
        val generator = FileKeyGenerator("bucket")
        val useCase = FileUploadUseCase(storage, generator, FileValidator())
        var signedKey = ""
        every { storage.generateUploadPresignedUrl("image/jpeg", any(), 1024) } answers {
            signedKey = secondArg()
            "https://storage.example/upload?signature=test"
        }

        val response =
            useCase.generateUploadPresignedUrl(
                CreatePresignedUrlCommand("proof.jpg", FileCategory.REVIEW, 1024, 1, 10),
            )

        assertEquals(signedKey, response.objectKey)
        assertTrue(response.objectKey.startsWith("temp/review/1/10/"))
        assertEquals("https://storage.example/upload?signature=test", response.presignedUrl)
        verify(exactly = 1) { storage.generateUploadPresignedUrl("image/jpeg", response.objectKey, 1024) }
    }

    @Test
    fun `발급 요청은 enum과 양수 크기를 검증 오류 없이 받아들인다`() {
        Validation.buildDefaultValidatorFactory().use { factory ->
            val request = CreatePresignedUrlRequest("proof.jpg", FileCategory.REVIEW, 1024, 10)

            assertTrue(factory.validator.validate(request).isEmpty())
            assertEquals(
                "fileSize",
                factory.validator
                    .validate(request.copy(fileSize = 0))
                    .single()
                    .propertyPath
                    .toString(),
            )
        }
    }
}
