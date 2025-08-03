package picklab.backend.search.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "자동완성 검색 응답")
data class AutocompleteResponse(
    @Schema(
        description = "자동완성 결과 목록 (키워드로 시작하는 활동명들을 알파벳순으로 정렬)",
        example = "[\"개발자 대회\", \"개발 부트캠프\", \"개발자 컨퍼런스\"]"
    )
    val suggestions: List<String>
) 