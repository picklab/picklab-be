package picklab.backend.university.domain.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.university.domain.entity.University
import picklab.backend.university.domain.repository.UniversityRepository

@Service
class UniversityService(
    private val universityRepository: UniversityRepository,
) {
    @Transactional(readOnly = true)
    fun search(
        query: String?,
        size: Int,
    ): List<University> {
        if (size <= 0) {
            throw BusinessException(ErrorCode.BAD_REQUEST)
        }

        val trimmedQuery = query?.trim().orEmpty()
        if (trimmedQuery.isBlank()) {
            return emptyList()
        }

        return universityRepository.searchByName(
            query = trimmedQuery,
            pageable = PageRequest.of(0, size.coerceAtMost(MAX_SIZE)),
        )
    }

    private companion object {
        const val MAX_SIZE = 20
    }
}
