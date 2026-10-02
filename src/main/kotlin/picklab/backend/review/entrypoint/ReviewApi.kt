package picklab.backend.review.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.review.entrypoint.request.ActivityReviewListRequest
import picklab.backend.review.entrypoint.request.MyReviewListRequest
import picklab.backend.review.entrypoint.request.ReviewCreateRequest
import picklab.backend.review.entrypoint.request.ReviewUpdateRequest
import picklab.backend.review.entrypoint.response.ActivityReviewResponse
import picklab.backend.review.entrypoint.response.MyReviewResponse
import picklab.backend.review.entrypoint.response.MyReviewsResponse

@Tag(name = "리뷰 API", description = "리뷰 관련 API 입니다.")
interface ReviewApi {
    @Operation(
        summary = "리뷰 등록",
        description = """
            해당 활동에 대한 리뷰를 등록합니다.
            인증 자료는 REVIEW 카테고리로 업로드한 파일의 objectKey를 전달합니다.
            인증 자료가 있으면 PENDING, 없으면 REJECTED로 저장됩니다.
            """,
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "리뷰 등록에 성공했습니다."),
            ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            ApiResponse(responseCode = "404", description = "요청한 정보 또는 인증 자료 파일을 찾을 수 없습니다."),
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
        @ParameterObject request: MyReviewListRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<MyReviewsResponse>>>

    @Operation(
        summary = "특정 활동에 대한 리뷰 리스트 조회",
        description = """
            특정 활동의 승인된 리뷰(APPROVED) 리스트를 조회합니다.
            
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
        @ParameterObject request: ActivityReviewListRequest,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityReviewResponse>>>

    @Operation(
        summary = "리뷰 도움돼요 표시",
        description = "로그인한 사용자가 리뷰에 도움돼요를 표시합니다. 이미 표시한 경우에도 성공합니다.",
    )
    fun markReviewHelpful(
        @Parameter(description = "리뷰 ID값") @PathVariable id: Long,
        member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "리뷰 도움돼요 취소",
        description = "로그인한 사용자가 리뷰의 도움돼요 표시를 취소합니다. 표시하지 않은 경우에도 성공합니다.",
    )
    fun unmarkReviewHelpful(
        @Parameter(description = "리뷰 ID값") @PathVariable id: Long,
        member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(
        summary = "리뷰 수정",
        description = """
            본인이 작성한 리뷰를 수정합니다.
            활동 변경 후 인증 자료를 연결하려면 해당 활동으로 새로 업로드합니다.
            인증 자료 교체 시 PENDING, 제거 시 REJECTED로 변경됩니다. 기존 키와 활동을 유지하면 승인 상태도 유지됩니다.
            """,
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "리뷰 수정에 성공했습니다."),
            ApiResponse(responseCode = "403", description = "해당 리뷰를 수정할 권한이 없습니다."),
            ApiResponse(responseCode = "404", description = "리뷰 또는 업로드된 인증 자료 파일을 찾을 수 없습니다."),
            ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
        ],
    )
    fun updateReview(
        @Parameter(description = "리뷰 ID값") @PathVariable id: Long,
        member: MemberPrincipal,
        request: ReviewUpdateRequest,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(summary = "리뷰 삭제", description = "본인이 작성한 리뷰를 삭제합니다.")
    fun deleteReview(
        @Parameter(description = "리뷰 ID값") @PathVariable id: Long,
        @AuthenticationPrincipal member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<Unit>>
}
