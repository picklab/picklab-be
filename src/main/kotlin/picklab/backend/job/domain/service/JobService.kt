package picklab.backend.job.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.job.domain.JobCategoryRepository
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup

@Service
class JobService(
    private val jobCategoryRepository: JobCategoryRepository,
) {
    fun groupByJobGroup(): Map<JobGroup, List<JobDetail>> = JobDetail.entries.groupBy { it.group }

    @Transactional(readOnly = true)
    fun findJobCategoriesByGroupAndDetail(jobCategoryList: List<Pair<JobGroup, JobDetail>>): List<JobCategory> =
        jobCategoryRepository
            .findByJobGroupInAndJobDetailIn(
                jobCategoryList.map { it.first }.distinct(),
                jobCategoryList.map { it.second }.distinct(),
            )
}
