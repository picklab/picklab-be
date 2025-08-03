package picklab.backend.search.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.search.entrypoint.request.CreateSearchHistoryRequest
import picklab.backend.search.entrypoint.response.AutocompleteResponse
import picklab.backend.search.entrypoint.response.RecentKeywordsResponse
import picklab.backend.search.entrypoint.response.SearchHistoryListResponse
import picklab.backend.search.entrypoint.response.SearchHistoryResponse

@Tag(name = "Search", description = "검색 API")
interface SearchApi {
    
    @Operation(summary = "통합 검색", description = "전체 도메인에서 키워드 검색을 수행합니다.")
    fun search(): String // TODO: 반환 타입 및 파라미터 정의 예정
    
    @Operation(
        summary = "자동완성 검색",
        description = """
            활동명을 기준으로 자동완성 검색을 수행합니다.
            입력한 키워드로 시작하는 활동명들을 알파벳 순으로 반환합니다.
            
            ## 응답 코드
            - **SEARCH_AUTOCOMPLETE_SUCCESS**: 자동완성 검색에 성공했습니다.
            
            ## 예시 요청
            ```
            GET /v1/search/autocomplete?keyword=개발&limit=5
            ```
            
            ## 예시 응답
            ```json
            {
              "code": "SEARCH_AUTOCOMPLETE_SUCCESS",
              "message": "자동완성 검색에 성공했습니다.",
              "data": {
                "suggestions": ["개발자 대회", "개발 부트캠프", "개발자 컨퍼런스"]
              }
            }
            ```
        """
    )
    fun autocomplete(
        @Parameter(description = "검색 키워드 (앞글자부터 매칭)", required = true, example = "개발")
        keyword: String,
        @Parameter(description = "반환할 최대 결과 수", example = "10")
        limit: Int = 10
    ): ResponseEntity<ResponseWrapper<AutocompleteResponse>>
    
    @Operation(summary = "검색 기록 생성", description = "새로운 검색 기록을 생성합니다.")
    fun createSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        request: CreateSearchHistoryRequest
    ): ResponseEntity<ResponseWrapper<SearchHistoryResponse>>
    
    @Operation(summary = "검색 기록 조회", description = "개인의 검색 기록을 페이징으로 조회합니다.")
    fun getSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "페이지 번호", example = "1")
        page: Int = 1,
        @Parameter(description = "페이지 크기", example = "20")
        size: Int = 20
    ): ResponseEntity<ResponseWrapper<SearchHistoryListResponse>>
    
    @Operation(
        summary = "최근 검색어 조회", 
        description = """
            최근 검색어를 최신순으로 조회합니다.
            삭제 기능을 위해 각 검색어의 ID를 포함하여 반환합니다.
            
            ## 응답 특징
            - 사용자별 고유한 키워드만 최신순으로 반환
            - 각 항목에 삭제 가능한 ID 포함
            - 같은 키워드를 다시 검색하면 기존 기록의 시간만 업데이트됨
            - 중복 키워드 없이 깔끔한 검색 기록 관리
        """
    )
    fun getRecentKeywords(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "반환할 최대 검색어 수 (1~20)", example = "10")
        limit: Int = 10
    ): ResponseEntity<ResponseWrapper<RecentKeywordsResponse>>
    
    @Operation(summary = "검색 기록 삭제", description = "특정 검색 기록을 삭제합니다.")
    fun deleteSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "검색 기록 ID", required = true)
        historyId: Long
    ): ResponseEntity<ResponseWrapper<Unit>>
    
    @Operation(summary = "전체 검색 기록 삭제", description = "모든 검색 기록을 삭제합니다.")
    fun deleteAllSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal
    ): ResponseEntity<ResponseWrapper<Unit>>
} 