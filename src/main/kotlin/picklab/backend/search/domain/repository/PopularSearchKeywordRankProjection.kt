package picklab.backend.search.domain.repository

import java.time.LocalDateTime

interface PopularSearchKeywordRankProjection {
    val keyword: String
    val searchCount: Long
    val lastSearchedAt: LocalDateTime
}
