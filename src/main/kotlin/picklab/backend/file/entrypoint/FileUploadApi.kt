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
            임시 객체 키와 10분간 유효한 PUT 업로드 URL을 발급합니다.
            파일 확장자에 맞는 Content-Type으로 파일 자체를 PUT 본문에 전송합니다. multipart/form-data는 사용하지 않습니다.
            REVIEW는 activityId가 필수이며, PUT 성공 후 응답의 objectKey를 리뷰 등록·수정 API에 전달합니다.
            URL에서 키를 추출하거나 쿼리를 제거할 필요가 없습니다.
            PROFILE·ARCHIVE의 기존 URL 기반 등록·수정 방식은 유지합니다.
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
