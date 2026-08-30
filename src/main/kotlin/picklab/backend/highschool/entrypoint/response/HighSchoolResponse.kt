package picklab.backend.highschool.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.highschool.domain.entity.HighSchool

@Schema(description = "고등학교 검색 항목")
data class HighSchoolResponse(
    @Schema(description = "고등학교 ID", example = "1")
    val id: Long,
    @Schema(description = "고등학교명", example = "서울고등학교")
    val name: String,
) {
    companion object {
        fun from(highSchool: HighSchool): HighSchoolResponse =
            HighSchoolResponse(
                id = highSchool.id,
                name = highSchool.name,
            )
    }
}
