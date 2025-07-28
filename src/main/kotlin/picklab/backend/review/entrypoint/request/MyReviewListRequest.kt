package picklab.backend.review.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import picklab.backend.review.application.model.MyReviewListQueryRequest

data class MyReviewListRequest(
    @field:Min(1)
    @field:Schema(description = "요청 페이지 (1부터 시작)", example = "1")
    val page: Int = 1,
    @field:Min(1)
    @field:Max(100)
    @field:Schema(description = "페이지 크기", example = "10")
    val size: Int = 10,
) {
    fun toQueryRequest(memberId: Long): MyReviewListQueryRequest =
        MyReviewListQueryRequest(
            page = this.page,
            size = this.size,
            memberId = memberId,
        )
}
