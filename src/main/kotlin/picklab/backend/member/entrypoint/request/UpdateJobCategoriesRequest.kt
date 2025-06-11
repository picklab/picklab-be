package picklab.backend.member.entrypoint.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty

data class UpdateJobCategoriesRequest(
    @field:NotEmpty
    @field:JsonProperty("interested_job_categories")
    val interestedJobCategories: List<JobCategoryDto>,
)
