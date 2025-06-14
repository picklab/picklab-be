package picklab.backend.job.domain

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup

interface JobCategoryRepository : JpaRepository<JobCategory, Long> {
    fun findByJobGroupInAndJobDetailIn(
        jobGroups: List<JobGroup>,
        jobDetails: List<JobDetail>,
    ): List<JobCategory>
}
