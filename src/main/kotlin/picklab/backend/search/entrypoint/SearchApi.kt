package picklab.backend.search.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.search.entrypoint.response.AutocompleteResponse

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
} 