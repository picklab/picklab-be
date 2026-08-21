package picklab.backend.university.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.university.domain.entity.University

@Schema(description = "대학교 검색 항목")
data class UniversityResponse(
    @Schema(description = "대학교 ID", example = "1")
    val id: Long,
    @Schema(description = "대학교명", example = "서울대학교")
    val name: String,
) {
    companion object {
        fun from(university: University): UniversityResponse =
            UniversityResponse(
                id = university.id,
                name = university.name,
            )
    }
}
