package picklab.backend.file.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.file.entrypoint.request.CreatePresignedUrlRequest
import picklab.backend.file.entrypoint.response.CreatePresignedurlResponse

@Tag(name = "파일 관련 API", description = "OCI Object Storage를 이용한 파일 업로드 API")
interface FileUploadApi {
    @Operation(
        summary = "PUT Presigned URL 발급",
        description = """
            파일 업로드용 Presigned URL과 임시 객체 키를 발급합니다.
            URL은 10분간 유효하며, 파일 확장자에 맞는 Content-Type으로 파일 본문을 PUT 전송합니다.
            """,
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Presigned URL 발급에 성공했습니다."),
            ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            ApiResponse(responseCode = "500", description = "서버 오류입니다."),
        ],
    )
    fun getPresignedUrl(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Valid @RequestBody request: CreatePresignedUrlRequest,
    ): ResponseEntity<ResponseWrapper<CreatePresignedurlResponse>>
}
