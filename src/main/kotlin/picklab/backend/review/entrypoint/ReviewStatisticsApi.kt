package picklab.backend.review.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.review.entrypoint.response.JobRelevanceStatisticsResponse

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
}
