package picklab.backend.search.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.domain.enums.ActivitySortType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.common.model.MemberPrincipal
import picklab.backend.common.model.PageResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.search.entrypoint.request.CreateSearchHistoryRequest
import picklab.backend.search.entrypoint.response.AutocompleteResponse
import picklab.backend.search.entrypoint.response.RecentKeywordsResponse
import picklab.backend.search.entrypoint.response.SearchHistoryResponse
import picklab.backend.search.entrypoint.response.SearchResultResponse

@Tag(name = "Search", description = "검색 API")
interface SearchApi {
    @Operation(
        summary = "통합 검색",
        description = "키워드로 전체 도메인을 검색합니다. 카테고리별 건수와 미리보기 항목(최대 5개)을 반환합니다.",
    )
    fun search(
        @Parameter(description = "검색 키워드", required = true, example = "개발")
        keyword: String,
    ): ResponseEntity<ResponseWrapper<SearchResultResponse>>

    @Operation(
        summary = "카테고리별 검색 결과",
        description = "특정 카테고리의 검색 결과를 페이지네이션으로 반환합니다.",
    )
    fun searchActivities(
        @Parameter(description = "검색 키워드", required = true, example = "개발")
        keyword: String,
        @Parameter(description = "활동 타입 (EXTRACURRICULAR, SEMINAR, EDUCATION, COMPETITION)", required = true)
        type: String,
        @Parameter(description = "모집 상태 (OPEN, CLOSED)")
        status: RecruitmentStatus? = null,
        @Parameter(description = "직무 그룹 (PLANNING, DEVELOPMENT, DESIGN, MARKETING, AI)")
        jobGroups: List<JobGroup>? = null,
        @Parameter(description = "정렬 기준 (LATEST, DEADLINE_ASC, DEADLINE_DESC)", example = "LATEST")
        sort: ActivitySortType = ActivitySortType.LATEST,
        @Parameter(description = "페이지 번호", example = "1")
        page: Int = 1,
        @Parameter(description = "페이지 크기", example = "10")
        size: Int = 10,
    ): ResponseEntity<ResponseWrapper<PageResponse<ActivityItemWithBookmark>>>

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
            """,
    )
    fun autocomplete(
        @Parameter(description = "검색 키워드 (앞글자부터 매칭)", required = true, example = "개발")
        keyword: String,
        @Parameter(description = "반환할 최대 결과 수", example = "10")
        limit: Int = 10,
    ): ResponseEntity<ResponseWrapper<AutocompleteResponse>>

    @Operation(summary = "검색 기록 생성", description = "새로운 검색 기록을 생성합니다.")
    fun createSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        request: CreateSearchHistoryRequest,
    ): ResponseEntity<ResponseWrapper<SearchHistoryResponse>>

    @Operation(summary = "검색 기록 조회", description = "개인의 검색 기록을 페이징으로 조회합니다.")
    fun getSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "페이지 번호", example = "1")
        page: Int = 1,
        @Parameter(description = "페이지 크기", example = "20")
        size: Int = 20,
    ): ResponseEntity<ResponseWrapper<PageResponse<SearchHistoryResponse>>>

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
            """,
    )
    fun getRecentKeywords(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "반환할 최대 검색어 수 (1~20)", example = "10")
        limit: Int = 10,
    ): ResponseEntity<ResponseWrapper<RecentKeywordsResponse>>

    @Operation(summary = "검색 기록 삭제", description = "특정 검색 기록을 삭제합니다.")
    fun deleteSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
        @Parameter(description = "검색 기록 ID", required = true)
        historyId: Long,
    ): ResponseEntity<ResponseWrapper<Unit>>

    @Operation(summary = "전체 검색 기록 삭제", description = "모든 검색 기록을 삭제합니다.")
    fun deleteAllSearchHistory(
        @AuthenticationPrincipal member: MemberPrincipal,
    ): ResponseEntity<ResponseWrapper<Unit>>
}
