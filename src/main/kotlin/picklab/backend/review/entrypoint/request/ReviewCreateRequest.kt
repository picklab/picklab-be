package picklab.backend.archive.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import picklab.backend.review.application.model.ReviewCreateCommand
import picklab.backend.review.entrypoint.request.JobCategoryDto

class ReviewCreateRequest(
    @Schema(description = "활동 ID")
    val activityId: Long,
    @Schema(description = "총 평점 (1 ~ 5점)")
    @Min(1)
    @Max(5)
    val overallScore: Int,
    @Schema(description = "정보 점수 (1 ~ 5점)")
    @Min(1)
    @Max(5)
    val infoScore: Int,
    @Schema(description = "강도 점수 (1 ~ 5점)")
    @Min(1)
    @Max(5)
    val difficultyScore: Int,
    @Schema(description = "혜택 점수 (1 ~ 5점)")
    @Min(1)
    @Max(5)
    val benefitScore: Int,
    @Schema(description = "한줄 평")
    @Size(min = 1, max = 255)
    val summary: String,
    @Schema(description = "장점 (30~1000자)")
    @Size(min = 30, max = 1000)
    val strength: String,
    @Schema(description = "단점 (30~1000자)")
    @Size(min = 30, max = 1000)
    val weakness: String,
    @Schema(description = "꿀팁 (선택, 30~1000자)")
    @Size(min = 30, max = 1000)
    val tips: String? = null,
    @Schema(description = "직무 연관성 점수 (1 ~ 5점)")
    @Min(1)
    @Max(5)
    val jobRelevanceScore: Int,
    @Schema(description = "리뷰 인증 자료 URL")
    val url: String? = null,
    @Schema(description = "직무 정보")
    val jobCategory: JobCategoryDto,
) {
    fun toCommand(memberId: Long): ReviewCreateCommand =
        ReviewCreateCommand(
            activityId = this.activityId,
            memberId = memberId,
            overallScore = this.overallScore,
            infoScore = this.infoScore,
            difficultyScore = this.difficultyScore,
            benefitScore = this.benefitScore,
            summary = this.summary,
            strength = this.strength,
            weakness = this.weakness,
            tips = this.tips,
            jobRelevanceScore = this.jobRelevanceScore,
            url = this.url,
            jobGroup = this.jobCategory.jobGroup,
            jobDetail = this.jobCategory.jobDetail,
        )
}
