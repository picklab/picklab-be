package picklab.backend.review.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup

data class JobCategoryDto(
    @field:NotNull(message = "직무 대분류는 필수 입력값입니다.")
    @field:Schema(description = "관심 직무 대분류", example = "DEVELOPMENT")
    val jobGroup: JobGroup,
    @field:Schema(description = "관심 직무 세부 분류 (직무 전체일 경우 null)", example = "BACKEND")
    val jobDetail: JobDetail? = null,
)
