package picklab.backend.job.application

import org.springframework.stereotype.Component
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.job.domain.service.JobService

@Component
class JobUseCase(
    private val jobService: JobService,
) {
    fun findGrouped(): Map<JobGroup, List<JobDetail>> = jobService.groupByJobGroup()
}
