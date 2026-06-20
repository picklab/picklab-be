package picklab.backend.search.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.application.mapper.withBookmark
import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.domain.enums.ActivitySortType
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.domain.service.ActivityBookmarkService
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.member.domain.MemberService
import picklab.backend.search.domain.model.PopularSearchKeywords
import picklab.backend.search.domain.service.MemberSearchHistoryService
import picklab.backend.search.domain.service.PopularSearchKeywordService
import picklab.backend.search.entrypoint.response.AutocompleteResponse
import picklab.backend.search.entrypoint.response.RecentKeywordItem
import picklab.backend.search.entrypoint.response.RecentKeywordsResponse
import picklab.backend.search.entrypoint.response.SearchCategoryGroup
import picklab.backend.search.entrypoint.response.SearchHistoryResponse
import picklab.backend.search.entrypoint.response.SearchResultResponse

@Component
class SearchUseCase(
    private val activityService: ActivityService,
    private val activityBookmarkService: ActivityBookmarkService,
    private val memberService: MemberService,
    private val memberSearchHistoryService: MemberSearchHistoryService,
    private val popularSearchKeywordService: PopularSearchKeywordService,
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
     * 통합 검색 - 카테고리별 미리보기 그룹 반환 (전체 탭)
     */
    @Transactional(readOnly = true)
    fun search(
        keyword: String,
        memberId: Long?,
        searcherKey: String,
    ): SearchResultResponse {
        val trimmed = keyword.trim()
        val countPerType = activityService.countActivitiesByKeywordPerType(trimmed)
        val totalCount = countPerType.values.sum()

        val groups =
            ActivityType.entries.mapNotNull { type ->
                val count = countPerType[type.discriminator] ?: 0L
                if (count == 0L) return@mapNotNull null

                val previewPage =
                    activityService.searchActivitiesByKeyword(
                        keyword = trimmed,
                        activityType = type.discriminator,
                        status = null,
                        jobGroups = null,
                        sort = ActivitySortType.LATEST,
                        pageable = PageRequest.of(0, 5),
                    )
                val previewIds = previewPage.content.map { it.id }
                val bookmarkedIds =
                    memberId
                        ?.let { activityBookmarkService.getMyBookmarkedActivityIds(it, previewIds) }
                        ?: emptySet()

                SearchCategoryGroup(
                    activityType = type.discriminator,
                    activityTypeName = type.label,
                    count = count,
                    items = previewPage.content.map { it.withBookmark(it.id in bookmarkedIds) },
                )
            }

        popularSearchKeywordService.recordSearch(
            keyword = trimmed,
            searcherKey = searcherKey,
            totalCount = totalCount,
        )

        return SearchResultResponse(
            keyword = trimmed,
            totalCount = totalCount,
            groups = groups,
        )
    }

    fun getPopularKeywords(): PopularSearchKeywords = popularSearchKeywordService.getPopularKeywords()

    /**
     * 카테고리별 검색 결과 페이지네이션 (카테고리 탭)
     */
    @Transactional(readOnly = true)
    fun searchActivities(
        keyword: String,
        activityType: String,
        status: RecruitmentStatus?,
        jobGroups: List<JobGroup>?,
        sort: ActivitySortType,
        page: Int,
        size: Int,
        memberId: Long?,
    ): Page<ActivityItemWithBookmark> {
        val trimmed = keyword.trim()
        val pageable = PageRequest.of(page - 1, size)
        val activityPage =
            activityService.searchActivitiesByKeyword(
                keyword = trimmed,
                activityType = activityType,
                status = status,
                jobGroups = jobGroups,
                sort = sort,
                pageable = pageable,
            )
        val activityIds = activityPage.content.map { it.id }
        val bookmarkedIds =
            memberId
                ?.let { activityBookmarkService.getMyBookmarkedActivityIds(it, activityIds) }
                ?: emptySet()
        return activityPage.map { it.withBookmark(it.id in bookmarkedIds) }
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
    ) = memberSearchHistoryService.getSearchHistory(memberId, page, size).map { history ->
        SearchHistoryResponse(
            id = history.id,
            keyword = history.keyword,
            searchedAt = history.searchedAt,
            createdAt = history.createdAt,
        )
    }

    /**
     * 최근 검색어 조회 (최신순)
     */
    @Transactional(readOnly = true)
    fun getRecentKeywords(
        memberId: Long,
        limit: Int,
    ): RecentKeywordsResponse {
        val keywords =
            memberSearchHistoryService.getRecentKeywords(memberId, limit).map { history ->
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
