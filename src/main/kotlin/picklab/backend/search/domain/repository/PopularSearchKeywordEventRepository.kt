package picklab.backend.search.domain.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import picklab.backend.search.domain.entity.PopularSearchKeywordEvent
import java.time.LocalDateTime

interface PopularSearchKeywordEventRepository : JpaRepository<PopularSearchKeywordEvent, Long> {
    @Modifying
    @Query(
        value =
            """
            INSERT IGNORE INTO popular_search_keyword_event
                (keyword, searcher_key, search_hour, searched_at, created_at, updated_at)
            VALUES
                (:keyword, :searcherKey, :searchHour, :searchedAt, NOW(), NOW())
            """,
        nativeQuery = true,
    )
    fun insertIgnore(
        @Param("keyword") keyword: String,
        @Param("searcherKey") searcherKey: String,
        @Param("searchHour") searchHour: LocalDateTime,
        @Param("searchedAt") searchedAt: LocalDateTime,
    ): Int

    @Query(
        """
        SELECT
            event.keyword AS keyword,
            COUNT(event.id) AS searchCount,
            MAX(event.searchedAt) AS lastSearchedAt
        FROM PopularSearchKeywordEvent event
        WHERE event.searchHour = :searchHour
          AND NOT EXISTS (
              SELECT blocked.id
              FROM BlockedSearchKeyword blocked
              WHERE blocked.keyword = event.keyword
          )
        GROUP BY event.keyword
        HAVING COUNT(event.id) >= :minSearchCount
        ORDER BY COUNT(event.id) DESC, MAX(event.searchedAt) DESC
        """,
    )
    fun findRanksBySearchHour(
        @Param("searchHour") searchHour: LocalDateTime,
        @Param("minSearchCount") minSearchCount: Long,
        pageable: Pageable,
    ): List<PopularSearchKeywordRankProjection>
}
