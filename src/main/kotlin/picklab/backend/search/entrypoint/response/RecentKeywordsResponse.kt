package picklab.backend.search.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "최근 검색어 응답")
data class RecentKeywordsResponse(
    @Schema(
        description = "최근 검색어 목록 (키워드별 unique, 최신순 정렬, 삭제 가능하도록 ID 포함)",
    )
    val keywords: List<RecentKeywordItem>,
)
