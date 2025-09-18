package picklab.backend.search.application

import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.member.domain.MemberService
import picklab.backend.search.domain.service.MemberSearchHistoryService
import picklab.backend.search.entrypoint.response.*

@Component
class SearchUseCase(
    private val activityService: ActivityService,
    private val memberService: MemberService,
    private val memberSearchHistoryService: MemberSearchHistoryService,
) {
    /**
     * 활동명 자동완성 검색
     */
    fun getAutocompleteResults(
        keyword: String,
        limit: Int,
    ): AutocompleteResponse {
        val suggestions = activityService.getActivityTitlesForAutocomplete(keyword, limit)
        return AutocompleteResponse(suggestions)
    }

    /**
     * 검색 기록 생성
     */
    @Transactional
    fun createSearchHistory(
        memberId: Long,
        keyword: String,
    ): SearchHistoryResponse {
        val member = memberService.findActiveMember(memberId)
        val savedHistory = memberSearchHistoryService.createSearchHistory(member, keyword)

        return SearchHistoryResponse(
            id = savedHistory.id,
            keyword = savedHistory.keyword,
            searchedAt = savedHistory.searchedAt,
            createdAt = savedHistory.createdAt,
        )
    }

    /**
     * 개인 검색 기록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    fun getSearchHistory(
        memberId: Long,
        page: Int,
        size: Int,
    ): Page<SearchHistoryResponse> {
        val searchHistoryPage = memberSearchHistoryService.getSearchHistory(memberId, page, size)

        return searchHistoryPage.map { history ->
            SearchHistoryResponse(
                id = history.id,
                keyword = history.keyword,
                searchedAt = history.searchedAt,
                createdAt = history.createdAt,
            )
        }
    }

    /**
     * 최근 검색어 조회 (최신순)
     */
    @Transactional(readOnly = true)
    fun getRecentKeywords(
        memberId: Long,
        limit: Int,
    ): RecentKeywordsResponse {
        val searchHistories = memberSearchHistoryService.getRecentKeywords(memberId, limit)

        val keywords =
            searchHistories.map { history ->
                RecentKeywordItem(
                    id = history.id,
                    keyword = history.keyword,
                    searchedAt = history.searchedAt,
                )
            }

        return RecentKeywordsResponse(keywords)
    }

    /**
     * 개별 검색 기록 삭제
     */
    @Transactional
    fun deleteSearchHistory(
        memberId: Long,
        historyId: Long,
    ) {
        memberSearchHistoryService.deleteSearchHistory(memberId, historyId)
    }

    /**
     * 전체 검색 기록 삭제
     */
    @Transactional
    fun deleteAllSearchHistory(memberId: Long) {
        memberSearchHistoryService.deleteAllSearchHistory(memberId)
    }
} 
