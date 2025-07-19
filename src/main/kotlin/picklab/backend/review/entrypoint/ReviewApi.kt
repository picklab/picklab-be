package picklab.backend.review.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import picklab.backend.archive.entrypoint.request.ReviewCreateRequest
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.review.application.model.MyReviewListRequest
import picklab.backend.review.entrypoint.response.MyReviewsResponse

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

    @Operation(
        summary = "내가 작성한 리뷰 리스트 조회",
        description =
            "로그인한 사용자가 작성한 리뷰 리스트를 조회합니다.\n\n" +
                "**요청 파라미터:**\n" +
                "- size: 한번에 가져올 데이터 개수 (기본값 100)\n" +
                "- page: 페이지 번호 (기본값 1)",
        responses = [
            ApiResponse(responseCode = "200", description = "리뷰 조회에 성공했습니다."),
        ],
    )
    fun getMyReviews(
        member: MemberPrincipal,
        request: MyReviewListRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<MyReviewsResponse>>>
}
