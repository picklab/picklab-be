package picklab.backend.search.entrypoint

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.domain.enums.ActivitySortType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.common.model.toPageResponse
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.search.application.SearchUseCase
import picklab.backend.search.entrypoint.request.CreateSearchHistoryRequest
import picklab.backend.search.entrypoint.response.AutocompleteResponse
import picklab.backend.search.entrypoint.response.PopularSearchKeywordsResponse
import picklab.backend.search.entrypoint.response.RecentKeywordsResponse
import picklab.backend.search.entrypoint.response.SearchHistoryResponse
import picklab.backend.search.entrypoint.response.SearchResultResponse

@RestController
@RequestMapping("/v1/search")
class SearchController(
    private val searchUseCase: SearchUseCase,
    private val searcherKeyResolver: SearcherKeyResolver,
) : SearchApi {
    @GetMapping("")
    override fun search(
        @RequestParam keyword: String,
    ): ResponseEntity<ResponseWrapper<SearchResultResponse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val memberId: Long? = (authentication?.principal as? MemberPrincipal)?.memberId
        val response =
            searchUseCase.search(
                keyword = keyword,
                memberId = memberId,
                searcherKey = searcherKeyResolver.resolve(memberId, currentRequest()),
            )
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.SEARCH_SUCCESS, response))
    }

    @GetMapping("/popular-keywords")
    override fun getPopularKeywords(): ResponseEntity<ResponseWrapper<PopularSearchKeywordsResponse>> {
        val response = PopularSearchKeywordsResponse.from(searchUseCase.getPopularKeywords())
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.POPULAR_SEARCH_KEYWORDS_RETRIEVED, response),
        )
    }

    @GetMapping("/activities")
    override fun searchActivities(
        @RequestParam keyword: String,
        @RequestParam type: String,
        @RequestParam(required = false) status: RecruitmentStatus?,
        @RequestParam(required = false) jobGroups: List<JobGroup>?,
        @RequestParam(defaultValue = "LATEST") sort: ActivitySortType,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityItemWithBookmark>>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val memberId: Long? = (authentication?.principal as? MemberPrincipal)?.memberId
        val response =
            searchUseCase
                .searchActivities(
                    keyword = keyword,
                    activityType = type,
                    status = status,
                    jobGroups = jobGroups,
                    sort = sort,
                    page = page,
                    size = size,
                    memberId = memberId,
                ).toPageResponse()
        return ResponseEntity.ok(ResponseWrapper.success(SuccessCode.SEARCH_ACTIVITIES_SUCCESS, response))
    }

    @GetMapping("/autocomplete")
    override fun autocomplete(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "10") limit: Int,
    ): ResponseEntity<ResponseWrapper<AutocompleteResponse>> {
        val response = searchUseCase.getAutocompleteResults(keyword, limit)
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.SEARCH_AUTOCOMPLETE_SUCCESS, response),
        )
    }

    @PostMapping("/history")
    override fun createSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestBody request: CreateSearchHistoryRequest,
    ): ResponseEntity<ResponseWrapper<SearchHistoryResponse>> {
        val response = searchUseCase.createSearchHistory(member.memberId, request.keyword)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseWrapper.success(SuccessCode.SEARCH_HISTORY_CREATED, response),
        )
    }

    @GetMapping("/history")
    override fun getSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<ResponseWrapper<PageResponse<SearchHistoryResponse>>> {
        val response =
            searchUseCase
                .getSearchHistory(member.memberId, page, size)
                .toPageResponse()
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.SEARCH_HISTORY_RETRIEVED, response),
        )
    }

    @GetMapping("/history/recent-keywords")
    override fun getRecentKeywords(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestParam(defaultValue = "10") limit: Int,
    ): ResponseEntity<ResponseWrapper<RecentKeywordsResponse>> {
        val response = searchUseCase.getRecentKeywords(member.memberId, limit)
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.RECENT_KEYWORDS_RETRIEVED, response),
        )
    }

    @DeleteMapping("/history/{historyId}")
    override fun deleteSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        @PathVariable historyId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        searchUseCase.deleteSearchHistory(member.memberId, historyId)
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.SEARCH_HISTORY_DELETED),
        )
    }

    @DeleteMapping("/history")
    override fun deleteAllSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<Unit>> {
        searchUseCase.deleteAllSearchHistory(member.memberId)
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.SEARCH_HISTORY_ALL_DELETED),
        )
    }

    private fun currentRequest() = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
}
