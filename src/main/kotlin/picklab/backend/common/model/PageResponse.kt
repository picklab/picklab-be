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
)

fun <T, R> Page<T>.toPageResponse(mapper: (T) -> R): PageResponse<R> =
    PageResponse(
        items = this.map(mapper).content,
        page = this.number + 1,
        size = this.size,
        totalPages = this.totalPages,
        totalElements = this.totalElements,
    )

fun <R> Page<R>.toPageResponse(): PageResponse<R> = toPageResponse { it }
