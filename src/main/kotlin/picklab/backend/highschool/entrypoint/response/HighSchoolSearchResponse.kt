package picklab.backend.highschool.entrypoint.response

import io.swagger.v3.oas.annotations.media.Schema
import picklab.backend.highschool.domain.entity.HighSchool

@Schema(description = "고등학교 검색 응답")
data class HighSchoolSearchResponse(
    @Schema(description = "고등학교 검색 결과")
    val items: List<HighSchoolResponse>,
) {
    companion object {
        fun from(highSchools: List<HighSchool>): HighSchoolSearchResponse =
            HighSchoolSearchResponse(
                items = highSchools.map(HighSchoolResponse::from),
            )
    }
}
