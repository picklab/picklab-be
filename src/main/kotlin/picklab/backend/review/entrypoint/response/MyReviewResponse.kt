package picklab.backend.review.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup

@Schema(description = "내 리뷰 단건 조회 응답")
data class MyReviewResponse(
    @field:Schema(description = "직무")
    val jobGroup: JobGroup,
    @field:Schema(description = "상세 직무 (null = 전체)")
    val jobDetail: JobDetail?,
    @field:Schema(description = "총 평점")
    val overallScore: Int,
    @field:Schema(description = "정보 점수")
    val infoScore: Int,
    @field:Schema(description = "강도 점수")
    val difficultyScore: Int,
    @field:Schema(description = "혜택 점수")
    val benefitScore: Int,
    @field:Schema(description = "직무 연관성 점수")
    val jobRelevanceScore: Int,
    @field:Schema(description = "한줄 평")
    val summary: String,
    @field:Schema(description = "장점")
    val strength: String,
    @field:Schema(description = "단점")
    val weakness: String,
    @field:Schema(description = "꿀팁")
    val tips: String?,
    @field:Schema(description = "인증자료 URL")
    val url: String?,
)
