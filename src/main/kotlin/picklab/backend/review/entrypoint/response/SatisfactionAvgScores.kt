package picklab.backend.review.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup

@Schema(description = "직무별 평균 점수 정보")
data class SatisfactionAvgScores(
    @Schema(description = "직무 그룹 코드 (null이면 전체를 의미)", example = "PLANNING", nullable = true)
    val jobGroup: JobGroup?,
    @Schema(description = "상세 직무 코드 (null이면 전체 혹은 그룹 전체를 의미)", example = "PM_PO", nullable = true)
    val jobDetail: JobDetail?,
    @Schema(description = "총 평점 평균")
    val avgTotalScore: Double,
    @Schema(description = "직무 경험 점수 평균")
    val avgJobExperienceScore: Double,
    @Schema(description = "활동 강도 점수 평균")
    val avgActivityIntensityScore: Double,
    @Schema(description = "혜택 및 복지 점수 평균")
    val avgBenefitScore: Double,
)
