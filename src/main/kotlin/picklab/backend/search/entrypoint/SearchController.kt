package picklab.backend.search.entrypoint

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.search.application.SearchUseCase
import picklab.backend.search.entrypoint.request.CreateSearchHistoryRequest
import picklab.backend.search.entrypoint.response.AutocompleteResponse
import picklab.backend.search.entrypoint.response.RecentKeywordsResponse
import picklab.backend.search.entrypoint.response.SearchHistoryListResponse
import picklab.backend.search.entrypoint.response.SearchHistoryResponse

@RestController
@RequestMapping("/v1/search")
class SearchController(
    private val searchUseCase: SearchUseCase,
) : SearchApi {

    @GetMapping("")
    override fun search(): String {
        // TODO: 검색 로직 구현 예정
        return "Search endpoint ready"
    }

    @GetMapping("/autocomplete")
    override fun autocomplete(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ResponseWrapper<AutocompleteResponse>> {
        val response = searchUseCase.getAutocompleteResults(keyword, limit)
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.SEARCH_AUTOCOMPLETE_SUCCESS, response)
        )
    }

    @PostMapping("/history")
    override fun createSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestBody request: CreateSearchHistoryRequest
    ): ResponseEntity<ResponseWrapper<SearchHistoryResponse>> {
        val response = searchUseCase.createSearchHistory(member.memberId, request.keyword)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseWrapper.success(SuccessCode.SEARCH_HISTORY_CREATED, response)
        )
    }

    @GetMapping("/history")
    override fun getSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ResponseWrapper<SearchHistoryListResponse>> {
        val response = searchUseCase.getSearchHistory(member.memberId, page, size)
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.SEARCH_HISTORY_RETRIEVED, response)
        )
    }

    @GetMapping("/history/recent-keywords")
    override fun getRecentKeywords(
        @AuthenticationPrincipal member: MemberPrincipal,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ResponseWrapper<RecentKeywordsResponse>> {
        val response = searchUseCase.getRecentKeywords(member.memberId, limit)
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.RECENT_KEYWORDS_RETRIEVED, response)
        )
    }

    @DeleteMapping("/history/{historyId}")
    override fun deleteSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        @PathVariable historyId: Long
    ): ResponseEntity<ResponseWrapper<Unit>> {
        searchUseCase.deleteSearchHistory(member.memberId, historyId)
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.SEARCH_HISTORY_DELETED)
        )
    }

    @DeleteMapping("/history")
    override fun deleteAllSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal
    ): ResponseEntity<ResponseWrapper<Unit>> {
        searchUseCase.deleteAllSearchHistory(member.memberId)
        return ResponseEntity.ok(
            ResponseWrapper.success(SuccessCode.SEARCH_HISTORY_ALL_DELETED)
        )
    }
} 