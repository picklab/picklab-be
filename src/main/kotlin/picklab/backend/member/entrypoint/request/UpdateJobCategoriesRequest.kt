package picklab.backend.member.entrypoint.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty

data class UpdateJobCategoriesRequest(
    @field:NotEmpty(message = "관심 직무 카테고리는 필수 입력값입니다.")
    @field:JsonProperty("interested_job_categories")
    val interestedJobCategories: List<JobCategoryDto>,
)
