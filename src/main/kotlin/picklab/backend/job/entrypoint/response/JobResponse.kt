package picklab.backend.job.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.job.domain.enums.JobGroup

@Schema(description = "직무 분류")
data class JobResponse(
    @Schema(description = "직무 그룹 코드", example = "DEVELOPMENT")
    val group: JobGroup,
    @Schema(description = "직무 그룹 이름", example = "개발")
    val label: String,
    @Schema(description = "상세 직무 목록")
    val details: List<JobDetailResponse>,
)
