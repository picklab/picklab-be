package picklab.backend.common.file.entrypoint

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import picklab.backend.common.file.application.FileUploadUseCase
import picklab.backend.common.file.entrypoint.request.CreatePresignedUrlRequest
import picklab.backend.common.file.entrypoint.response.CreatePresignedurlResponse
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode

@RestController
@RequestMapping("/v1/files")
class FileUploadController(
    private val fileUploadUseCase: FileUploadUseCase,
) : FileUploadApi {
    @PostMapping("/presigned-url")
    override fun getPresignedUrl(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: CreatePresignedUrlRequest,
    ): ResponseEntity<ResponseWrapper<CreatePresignedurlResponse>> {
        val data = fileUploadUseCase.generateUploadPresignedUrl(request.toCommand(member.memberId))

        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.GET_PRESIGNED_URL, data))
    }
}
