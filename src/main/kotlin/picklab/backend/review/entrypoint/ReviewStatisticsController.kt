package picklab.backend.review.entrypoint

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.review.application.ReviewStatisticsUseCase
import picklab.backend.review.entrypoint.response.JobRelevanceStatisticsResponse
import picklab.backend.review.entrypoint.response.SatisfactionStatisticsResponse
import picklab.backend.review.entrypoint.response.toResponse

@RestController
class ReviewStatisticsController(
    private val reviewStatisticsUseCase: ReviewStatisticsUseCase,
) : ReviewStatisticsApi {
    @GetMapping("/v1/activities/{activityId}/reviews/statistics/job-relevance")
    override fun getJobRelevanceStatistics(
        @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<JobRelevanceStatisticsResponse>> =
        reviewStatisticsUseCase
            .getJobRelevanceStatistics(activityId)
            .toResponse()
            .let { ResponseWrapper.success(SuccessCode.GET_JOB_RELEVANCE_AVG_SCORES_SUCCESS, it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/v1/activities/{activityId}/reviews/statistics/satisfaction")
    override fun getSatisfactionStatistics(
        @AuthenticationPrincipal member: MemberPrincipal?,
        @PathVariable activityId: Long,
    ): ResponseEntity<ResponseWrapper<SatisfactionStatisticsResponse>> =
        reviewStatisticsUseCase
            .getSatisfactionStatistics(member?.memberId, activityId)
            .map { it.toResponse() }
            .let { SatisfactionStatisticsResponse(items = it) }
            .let { ResponseWrapper.success(SuccessCode.GET_SATISFACTION_AVG_SCORES_SUCCESS, it) }
            .let { ResponseEntity.ok(it) }
}
