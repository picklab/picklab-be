package picklab.backend.job.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.job.entrypoint.response.JobResponse

interface JobApi {
    @Operation(
        summary = "직무 목록 조회",
        description = "직무 그룹 및 상세 직무 목록을 조회합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "직무 상세 목록 조회에 성공했습니다."),
        ],
    )
    fun findAll(): ResponseEntity<ResponseWrapper<List<JobResponse>>>
}
