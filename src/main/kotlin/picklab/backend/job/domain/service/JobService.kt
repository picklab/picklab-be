package picklab.backend.job.domain.service

import org.springframework.stereotype.Service
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup

@Service
class JobService {
    fun groupByJobGroup(): Map<JobGroup, List<JobDetail>> = JobDetail.entries.groupBy { it.group }
}
