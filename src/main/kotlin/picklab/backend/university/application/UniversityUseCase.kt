package picklab.backend.university.application

import org.springframework.stereotype.Component
import picklab.backend.university.domain.entity.University
import picklab.backend.university.domain.service.UniversityService

@Component
class UniversityUseCase(
    private val universityService: UniversityService,
) {
    fun search(
        query: String?,
        size: Int,
    ): List<University> = universityService.search(query, size)
}
