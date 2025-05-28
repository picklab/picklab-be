package picklab.backend.job.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.job.domain.enums.JobDetail

@Schema(description = "직무 상세 정보")
data class JobDetailResponse(
    @Schema(description = "직무 코드", example = "BACKEND")
    val code: JobDetail,
    @Schema(description = "직무 이름", example = "백엔드")
    val label: String,
)
