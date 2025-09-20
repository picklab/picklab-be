package picklab.backend.search.entrypoint.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "자동완성 검색 요청")
data class AutocompleteRequest(
    @Schema(
        description = "검색 키워드 (앞글자부터 매칭하여 활동명 검색)",
        example = "개발",
        required = true,
    )
    val keyword: String,
    @Schema(
        description = "반환할 최대 결과 수 (기본값: 10, 최대: 50)",
        example = "10",
        minimum = "1",
        maximum = "50",
    )
    val limit: Int = 10,
)
