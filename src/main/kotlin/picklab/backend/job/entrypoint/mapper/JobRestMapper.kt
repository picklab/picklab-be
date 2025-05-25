package picklab.backend.job.entrypoint.mapper

import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.job.entrypoint.response.JobDetailResponse
import picklab.backend.job.entrypoint.response.JobResponse

fun Map<JobGroup, List<JobDetail>>.toJobResponses(): List<JobResponse> =
    this.map { (group, details) ->
        JobResponse(
            group = group,
            label = group.label,
            details =
                details.map { detail ->
                    JobDetailResponse(
                        code = detail,
                        label = detail.label,
                    )
                },
        )
    }
