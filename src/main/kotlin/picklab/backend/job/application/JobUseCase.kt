package picklab.backend.job.application

import org.springframework.stereotype.Component
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.job.domain.service.JobService
import picklab.backend.member.entrypoint.request.JobCategoryDto

@Component
class JobUseCase(
    private val jobService: JobService,
) {
    fun findGrouped(): Map<JobGroup, List<JobDetail>> = jobService.groupByJobGroup()

    fun findJobCategories(interestedJobCategories: List<JobCategoryDto>): List<JobCategory> {
        val jobCategoryList =
            interestedJobCategories.map {
                JobGroup.valueOf(it.group) to JobDetail.valueOf(it.detail)
            }

        return jobService.findJobCategoriesByGroupAndDetail(jobCategoryList)
    }
}
