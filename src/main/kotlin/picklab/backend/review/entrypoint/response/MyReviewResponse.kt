package picklab.backend.review.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.review.domain.entity.Review

@Schema(description = "내 리뷰 단건 조회 응답")
data class MyReviewResponse(
    @Schema(description = "직무")
    val jobGroup: JobGroup,
    @Schema(description = "상세 직무 (null = 전체)")
    val jobDetail: JobDetail?,
    @Schema(description = "총 평점")
    val overallScore: Int,
    @Schema(description = "정보 점수")
    val infoScore: Int,
    @Schema(description = "강도 점수")
    val difficultyScore: Int,
    @Schema(description = "혜택 점수")
    val benefitScore: Int,
    @Schema(description = "직무 연관성 점수")
    val jobRelevanceScore: Int,
    @Schema(description = "한줄 평")
    val summary: String,
    @Schema(description = "장점")
    val strength: String,
    @Schema(description = "단점")
    val weakness: String,
    @Schema(description = "꿀팁")
    val tips: String?,
    @Schema(description = "인증자료 URL")
    val url: String?,
) {
    companion object {
        fun from(
            review: Review,
            jobCategory: JobCategory,
        ): MyReviewResponse =
            MyReviewResponse(
                jobGroup = jobCategory.jobGroup,
                jobDetail = jobCategory.jobDetail,
                overallScore = review.overallScore,
                infoScore = review.infoScore,
                difficultyScore = review.difficultyScore,
                benefitScore = review.benefitScore,
                jobRelevanceScore = review.jobRelevanceScore,
                summary = review.summary,
                strength = review.strength,
                weakness = review.weakness,
                tips = review.tips,
                url = review.url,
            )
    }
}
