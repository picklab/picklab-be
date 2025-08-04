package picklab.backend.review.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import picklab.backend.archive.entrypoint.request.ReviewCreateRequest
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.review.entrypoint.request.ActivityReviewListRequest
import picklab.backend.review.entrypoint.request.MyReviewListRequest
import picklab.backend.review.entrypoint.request.ReviewUpdateRequest
import picklab.backend.review.entrypoint.response.ActivityReviewResponse
import picklab.backend.review.entrypoint.response.MyReviewResponse
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
        summary = "내가 작성한 리뷰 단건 조회",
        description = "로그인한 사용자가 본인이 작성한 특정 리뷰를 단건 조회합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "리뷰 조회에 성공했습니다."),
            ApiResponse(responseCode = "403", description = "해당 리뷰를 조회할 권한이 없습니다."),
            ApiResponse(responseCode = "404", description = "리뷰 정보를 찾을 수 없습니다."),
        ],
    )
    fun getMyReview(
        @Parameter(description = "리뷰 ID값") @PathVariable id: Long,
        member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<MyReviewResponse>>

    @Operation(
        summary = "내가 작성한 리뷰 리스트 조회",
        description = """
        로그인한 사용자가 작성한 리뷰 리스트를 조회합니다.

        요청 파라미터:
        - size: 한번에 가져올 데이터 개수 (기본값 10)
        - page: 페이지 번호 (기본값 1)
        """,
        responses = [
            ApiResponse(responseCode = "200", description = "리뷰 조회에 성공했습니다."),
        ],
    )
    fun getMyReviews(
        member: MemberPrincipal,
        @ModelAttribute @ParameterObject request: MyReviewListRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<MyReviewsResponse>>>

    @Operation(
        summary = "특정 활동에 대한 리뷰 리스트 조회",
        description = """
        특정 활동에 대한 리뷰 리스트를 조회합니다.

        요청 파라미터:
        - page: 페이지 번호 (1부터 시작, 기본값 1)
        - size: 한번에 가져올 데이터 개수 (1~100, 기본값 10)
        - rating: 활동 총 평점 필터 (1~5)
        - jobGroup: 관심 직무 필터 (대분류 전체 리스트)
        - jobDetail: 관심 직무 필터 (세부 직무 리스트)
        - status: 수료 상태 필터 (IN_PROGRESSING, COMPLETED, DROPPED)

        로그인 여부에 따라 응답 데이터가 달라집니다.
    """,
        responses = [
            ApiResponse(responseCode = "200", description = "리뷰 조회에 성공했습니다."),
        ],
    )
    fun getReviewsByActivity(
        @Parameter(description = "활동 ID값") @PathVariable activityId: Long,
        member: MemberPrincipal?,
        @ModelAttribute @ParameterObject request: ActivityReviewListRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityReviewResponse>>>

    @Operation(
        summary = "리뷰 수정",
        description = "본인이 작성한 리뷰를 수정합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "리뷰 수정에 성공했습니다."),
            ApiResponse(responseCode = "403", description = "해당 리뷰를 수정할 권한이 없습니다."),
            ApiResponse(responseCode = "404", description = "리뷰 정보를 찾을 수 없습니다."),
            ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
        ],
    )
    fun updateReview(
        @Parameter(description = "리뷰 ID값") @PathVariable id: Long,
        member: MemberPrincipal,
        request: ReviewUpdateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>
}
