package picklab.backend.job.entrypoint

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.job.application.JobUseCase
import picklab.backend.job.entrypoint.mapper.toJobResponses
import picklab.backend.job.entrypoint.response.JobResponse

@RestController
@RequestMapping("/v1/jobs")
class JobController(
    private val jobUseCase: JobUseCase,
) : JobApi {
    @GetMapping
    override fun findAll(): ResponseEntity<ResponseWrapper<List<JobResponse>>> =
        jobUseCase
            .findGrouped()
            .toJobResponses()
            .let { ResponseWrapper.success(SuccessCode.JOB_DETAILS_RETRIEVED, it) }
            .let { ResponseEntity.status(HttpStatus.OK).body(it) }
}
