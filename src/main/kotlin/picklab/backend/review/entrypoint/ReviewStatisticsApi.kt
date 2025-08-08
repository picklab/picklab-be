package picklab.backend.review.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.review.entrypoint.response.JobRelevanceStatisticsResponse
import picklab.backend.review.entrypoint.response.SatisfactionStatisticsResponse

@Tag(name = "리뷰 통계 API", description = "리뷰 통계 관련 API 입니다.")
interface ReviewStatisticsApi {
    @Operation(
        summary = "활동별 직무 연관성 점수 평균 조회",
        description = "활동에 대한 전체 리뷰를 집계해 직무별 평균 연관성 점수 반환합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "직무 연관성 평균 점수 조회에 성공했습니다."),
        ],
    )
    fun getJobRelevanceStatistics(
        @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<JobRelevanceStatisticsResponse>>

    @Operation(
        summary = "활동별 만족도 평가 평균 점수 조회",
        description = """
활동에 대한 전체 리뷰와 로그인 사용자의 관심 직무별 만족도 평가 평균 점수를 반환합니다.
- 비로그인(인증되지 않은) 상태일 경우: 전체 리뷰에 대한 통계만 노출됩니다.
- 로그인 상태일 경우: 전체 리뷰 통계와 함께 사용자가 등록한 최대 5개 관심 직무별 통계도 같이 반환됩니다.
""",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "활동별 만족도 통계 조회에 성공했습니다."),
        ],
    )
    fun getSatisfactionStatistics(
        @AuthenticationPrincipal member: MemberPrincipal?,
        @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<SatisfactionStatisticsResponse>>
}
