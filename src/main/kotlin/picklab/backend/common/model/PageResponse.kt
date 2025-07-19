package picklab.backend.common.model

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

data class PageResponse<T>(
    @Schema(description = "요소 리스트")
    val items: List<T>,
    @Schema(description = "페이지 번호 (1부터 시작)")
    val page: Int,
    @Schema(description = "페이지 크기")
    val size: Int,
    @Schema(description = "전체 페이지 수")
    val totalPages: Int,
    @Schema(description = "전체 요소 개수")
    val totalElements: Long,
) {
    companion object {
        fun <T> from(page: Page<T>): PageResponse<T> =
            PageResponse(
                items = page.content,
                page = page.number + 1,
                size = page.size,
                totalPages = page.totalPages,
                totalElements = page.totalElements,
            )
    }
}
