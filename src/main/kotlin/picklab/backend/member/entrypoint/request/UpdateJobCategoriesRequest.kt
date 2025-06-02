package picklab.backend.member.entrypoint.request

import jakarta.validation.constraints.NotEmpty

data class UpdateJobCategoriesRequest(
    @field:NotEmpty
    val interestedJobCategories: List<JobCategoryDto>,
)
