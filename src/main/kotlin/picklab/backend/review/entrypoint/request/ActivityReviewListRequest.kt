package picklab.backend.review.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.participation.domain.enums.ProgressStatus
import picklab.backend.review.application.model.ActivityReviewListQueryRequest

data class ActivityReviewListRequest(
    @field:Min(1)
    @field:Schema(description = "요청 페이지 (1부터 시작)", example = "1")
    val page: Int = 1,
    @field:Min(1)
    @field:Max(100)
    @field:Schema(description = "페이지 크기", example = "10")
    val size: Int = 10,
    @field:Schema(description = "활동 총 평점 필터 (1~5)")
    val rating: Int?,
    @field:Schema(description = "관심 직무 필터 (대분류 전체 리스트)")
    val jobGroup: List<JobGroup>?,
    @field:Schema(description = "관심 직무 필터 (세부 직무 리스트)")
    val jobDetail: List<JobDetail>?,
    @field:Schema(description = "수료 상태 필터", example = "COMPLETED")
    val status: ProgressStatus?,
) {
    fun toQueryRequest(): ActivityReviewListQueryRequest =
        ActivityReviewListQueryRequest(
            rating = this.rating,
            jobGroup = this.jobGroup,
            jobDetail = this.jobDetail,
            status = this.status,
        )
}
