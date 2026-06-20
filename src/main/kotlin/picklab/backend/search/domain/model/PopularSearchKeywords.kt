package picklab.backend.search.domain.model

import picklab.backend.search.domain.enums.PopularSearchKeywordTrend
import java.time.LocalDateTime

data class PopularSearchKeywords(
    val aggregatedAt: LocalDateTime,
    val keywords: List<PopularSearchKeyword>,
)

data class PopularSearchKeyword(
    val rank: Int,
    val keyword: String,
    val trend: PopularSearchKeywordTrend,
)
