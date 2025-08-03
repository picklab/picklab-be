package picklab.backend.search.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "검색 기록 목록 응답")
data class SearchHistoryListResponse(
    @Schema(description = "검색 기록 목록")
    val items: List<SearchHistoryResponse>,
    
    @Schema(description = "현재 페이지 번호", example = "1")
    val currentPage: Int,
    
    @Schema(description = "페이지 크기", example = "20")
    val pageSize: Int,
    
    @Schema(description = "전체 요소 수", example = "150")
    val totalElements: Long,
    
    @Schema(description = "전체 페이지 수", example = "8")
    val totalPages: Int,
    
    @Schema(description = "첫 번째 페이지 여부", example = "true")
    val isFirst: Boolean,
    
    @Schema(description = "마지막 페이지 여부", example = "false")
    val isLast: Boolean
) 