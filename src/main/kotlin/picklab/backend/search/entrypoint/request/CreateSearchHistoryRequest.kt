package picklab.backend.search.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "검색 기록 생성 요청")
data class CreateSearchHistoryRequest(
    @Schema(
        description = "검색 키워드", 
        example = "스프링 부트 개발자",
        required = true
    )
    val keyword: String,
) 