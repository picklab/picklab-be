package picklab.backend.university.entrypoint

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.university.entrypoint.response.UniversitySearchResponse

@Tag(name = "University", description = "대학교 API")
interface UniversityApi {
    @Operation(
        summary = "대학교 검색",
        description = "회원가입 학교명 자동완성을 위한 대학교 목록을 검색합니다. query가 없거나 공백이면 빈 목록을 반환합니다.",
    )
    fun search(
        @Parameter(description = "검색어", example = "서울")
        query: String?,
        @Parameter(description = "반환할 최대 개수. 기본 10, 최대 20", example = "10")
        size: Int,
    ): ResponseEntity<ResponseWrapper<UniversitySearchResponse>>
}
