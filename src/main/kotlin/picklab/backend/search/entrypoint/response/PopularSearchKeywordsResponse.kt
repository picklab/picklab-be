package picklab.backend.search.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.search.domain.enums.PopularSearchKeywordTrend
import picklab.backend.search.domain.model.PopularSearchKeyword
import picklab.backend.search.domain.model.PopularSearchKeywords
import java.time.LocalDateTime

@Schema(description = "인기 검색어 응답")
data class PopularSearchKeywordsResponse(
    @field:Schema(description = "집계 기준 시각")
    val aggregatedAt: LocalDateTime,
    @field:Schema(description = "인기 검색어 목록")
    val keywords: List<PopularSearchKeywordItem>,
) {
    companion object {
        fun from(result: PopularSearchKeywords): PopularSearchKeywordsResponse =
            PopularSearchKeywordsResponse(
                aggregatedAt = result.aggregatedAt,
                keywords = result.keywords.map(PopularSearchKeywordItem::from),
            )
    }
}

data class PopularSearchKeywordItem(
    @field:Schema(description = "순위")
    val rank: Int,
    @field:Schema(description = "검색어")
    val keyword: String,
    @field:Schema(description = "순위 변동")
    val trend: PopularSearchKeywordTrend,
) {
    companion object {
        fun from(keyword: PopularSearchKeyword): PopularSearchKeywordItem =
            PopularSearchKeywordItem(
                rank = keyword.rank,
                keyword = keyword.keyword,
                trend = keyword.trend,
            )
    }
}
