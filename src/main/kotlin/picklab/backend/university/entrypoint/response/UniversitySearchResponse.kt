package picklab.backend.university.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.university.domain.entity.University

@Schema(description = "대학교 검색 응답")
data class UniversitySearchResponse(
    @Schema(description = "대학교 검색 결과")
    val items: List<UniversityResponse>,
) {
    companion object {
        fun from(universities: List<University>): UniversitySearchResponse =
            UniversitySearchResponse(
                items = universities.map(UniversityResponse::from),
            )
    }
}
