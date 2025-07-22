package picklab.backend.review.application.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class MyReviewListRequest(
    @Min(1)
    @Schema(description = "요청 페이지 (1부터 시작)")
    val page: Int = 1,
    @Min(1)
    @Max(100)
    @Schema(description = "페이지 크기")
    val size: Int = 10,
) {
    fun toQueryRequest(memberId: Long): MyReviewListQueryRequest =
        MyReviewListQueryRequest(
            page = this.page,
            size = this.size,
            memberId = memberId,
        )
}
