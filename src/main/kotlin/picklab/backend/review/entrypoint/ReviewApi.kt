package picklab.backend.review.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import picklab.backend.archive.entrypoint.request.ReviewCreateRequest
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper

@Tag(name = "리뷰 API", description = "리뷰 관련 API 입니다.")
interface ReviewApi {
    @Operation(
        summary = "리뷰 등록",
        description = "해당 공고에 대한 리뷰를 등록 합니다",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "리뷰 등록에 성공했습니다."),
        ],
    )
    fun create(
        member: MemberPrincipal,
        request: ReviewCreateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>
}
