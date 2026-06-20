package picklab.backend.search.domain.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import picklab.backend.search.domain.enums.PopularSearchKeywordTrend
import picklab.backend.search.domain.model.PopularSearchKeyword
import picklab.backend.search.domain.model.PopularSearchKeywords
import picklab.backend.search.domain.repository.BlockedSearchKeywordRepository
import picklab.backend.search.domain.repository.PopularSearchKeywordEventRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class PopularSearchKeywordService(
    private val popularSearchKeywordEventRepository: PopularSearchKeywordEventRepository,
    private val blockedSearchKeywordRepository: BlockedSearchKeywordRepository,
    private val searchKeywordNormalizer: SearchKeywordNormalizer,
) {
    companion object {
        private const val MIN_SEARCH_COUNT = 2L
        private const val RANK_LIMIT = 10
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun recordSearch(
        keyword: String,
        searcherKey: String,
        totalCount: Long,
    ) {
        if (totalCount <= 0) return

        val normalizedKeyword = searchKeywordNormalizer.normalize(keyword)
        if (normalizedKeyword.isBlank()) return
        if (blockedSearchKeywordRepository.existsByKeyword(normalizedKeyword)) return

        val now = LocalDateTime.now()
        popularSearchKeywordEventRepository.insertIgnore(
            keyword = normalizedKeyword,
            searcherKey = searcherKey,
            searchHour = now.truncatedTo(ChronoUnit.HOURS),
            searchedAt = now,
        )
    }

    @Transactional(readOnly = true)
    fun getPopularKeywords(): PopularSearchKeywords {
        val aggregatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        val currentSearchHour = aggregatedAt.minusHours(1)
        val previousSearchHour = currentSearchHour.minusHours(1)
        val pageable = PageRequest.of(0, RANK_LIMIT)

        val currentRanks =
            popularSearchKeywordEventRepository.findRanksBySearchHour(
                searchHour = currentSearchHour,
                minSearchCount = MIN_SEARCH_COUNT,
                pageable = pageable,
            )
        val previousRankByKeyword =
            popularSearchKeywordEventRepository
                .findRanksBySearchHour(
                    searchHour = previousSearchHour,
                    minSearchCount = MIN_SEARCH_COUNT,
                    pageable = pageable,
                ).mapIndexed { index, row -> row.keyword to index + 1 }
                .toMap()

        return PopularSearchKeywords(
            aggregatedAt = aggregatedAt,
            keywords =
                currentRanks.mapIndexed { index, row ->
                    val currentRank = index + 1
                    PopularSearchKeyword(
                        rank = currentRank,
                        keyword = row.keyword,
                        trend =
                            calculateTrend(
                                currentRank = currentRank,
                                previousRank = previousRankByKeyword[row.keyword],
                            ),
                    )
                },
        )
    }

    private fun calculateTrend(
        currentRank: Int,
        previousRank: Int?,
    ): PopularSearchKeywordTrend =
        when {
            previousRank == null -> PopularSearchKeywordTrend.NEW
            currentRank < previousRank -> PopularSearchKeywordTrend.UP
            currentRank > previousRank -> PopularSearchKeywordTrend.DOWN
            else -> PopularSearchKeywordTrend.SAME
        }
}
