package picklab.backend.search

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.servlet.get
import org.springframework.transaction.support.TransactionTemplate
import picklab.backend.search.domain.entity.BlockedSearchKeyword
import picklab.backend.search.domain.enums.PopularSearchKeywordTrend
import picklab.backend.search.domain.repository.BlockedSearchKeywordRepository
import picklab.backend.search.domain.repository.PopularSearchKeywordEventRepository
import picklab.backend.search.domain.service.PopularSearchKeywordService
import picklab.backend.template.IntegrationTest
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class PopularSearchKeywordServiceTest : IntegrationTest() {
    @Autowired
    lateinit var popularSearchKeywordService: PopularSearchKeywordService

    @Autowired
    lateinit var popularSearchKeywordEventRepository: PopularSearchKeywordEventRepository

    @Autowired
    lateinit var blockedSearchKeywordRepository: BlockedSearchKeywordRepository

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @BeforeEach
    fun setUp() {
        cleanUp.all()
    }

    @Test
    @DisplayName("같은 시간대 동일 검색자와 동일 키워드는 한 번만 기록한다")
    fun recordOncePerSearchHour() {
        popularSearchKeywordService.recordSearch(" React ", "MEMBER:1", totalCount = 3)
        popularSearchKeywordService.recordSearch("react", "MEMBER:1", totalCount = 3)
        popularSearchKeywordService.recordSearch("empty", "MEMBER:2", totalCount = 0)

        val searchHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        val ranks =
            popularSearchKeywordEventRepository.findRanksBySearchHour(
                searchHour = searchHour,
                minSearchCount = 1,
                pageable = PageRequest.of(0, 10),
            )

        assertThat(ranks).hasSize(1)
        assertThat(ranks[0].keyword).isEqualTo("react")
        assertThat(ranks[0].searchCount).isEqualTo(1)
    }

    @Test
    @DisplayName("직전 1시간 인기 검색어와 순위 변동을 조회한다")
    fun getPopularKeywords() {
        val aggregatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        val currentHour = aggregatedAt.minusHours(1)
        val previousHour = currentHour.minusHours(1)

        blockedSearchKeywordRepository.save(BlockedSearchKeyword("blocked"))

        insertEvents("kotlin", currentHour, 3, currentHour.plusMinutes(10))
        insertEvents("java", currentHour, 2, currentHour.plusMinutes(20))
        insertEvents("spring", currentHour, 2, currentHour.plusMinutes(5))
        insertEvents("react", currentHour, 1, currentHour.plusMinutes(30))
        insertEvents("blocked", currentHour, 3, currentHour.plusMinutes(40))

        insertEvents("java", previousHour, 3, previousHour.plusMinutes(10))
        insertEvents("kotlin", previousHour, 2, previousHour.plusMinutes(20))

        val response = popularSearchKeywordService.getPopularKeywords()

        assertThat(response.aggregatedAt).isEqualTo(aggregatedAt)
        assertThat(response.keywords.map { it.keyword }).containsExactly("kotlin", "java", "spring")
        assertThat(response.keywords.map { it.rank }).containsExactly(1, 2, 3)
        assertThat(response.keywords.map { it.trend })
            .containsExactly(
                PopularSearchKeywordTrend.UP,
                PopularSearchKeywordTrend.DOWN,
                PopularSearchKeywordTrend.NEW,
            )
    }

    @Test
    @DisplayName("인기 검색어 조회 API는 인증 없이 호출할 수 있다")
    fun getPopularKeywordsWithoutAuthentication() {
        mockMvc
            .get("/v1/search/popular-keywords")
            .andExpect { status { isOk() } }
    }

    private fun insertEvents(
        keyword: String,
        searchHour: LocalDateTime,
        count: Int,
        lastSearchedAt: LocalDateTime,
    ) {
        (1..count).forEach { index ->
            transactionTemplate.executeWithoutResult {
                popularSearchKeywordEventRepository.insertIgnore(
                    keyword = keyword,
                    searcherKey = "MEMBER:$keyword:$index",
                    searchHour = searchHour,
                    searchedAt = if (index == count) lastSearchedAt else searchHour.plusMinutes(index.toLong()),
                )
            }
        }
    }
}
