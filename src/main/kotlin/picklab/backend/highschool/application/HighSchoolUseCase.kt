package picklab.backend.highschool.application

import org.springframework.stereotype.Component
import picklab.backend.highschool.domain.entity.HighSchool
import picklab.backend.highschool.domain.service.HighSchoolService

@Component
class HighSchoolUseCase(
    private val highSchoolService: HighSchoolService,
) {
    fun search(
        query: String?,
        size: Int,
    ): List<HighSchool> = highSchoolService.search(query, size)
}
