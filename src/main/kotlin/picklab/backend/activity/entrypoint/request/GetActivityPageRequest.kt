package picklab.backend.activity.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class GetActivityPageRequest(
    @field:Min(1)
    @field:Schema(description = "요청 페이지 (기본값 1)")
    val page: Int = 1,
    @field:Min(1)
    @field:Max(100)
    @field:Schema(description = "페이지 크기 (기본값 4)")
    val size: Int = 4,
)
