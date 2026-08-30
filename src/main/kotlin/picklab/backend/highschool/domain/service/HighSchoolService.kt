package picklab.backend.highschool.domain.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.highschool.domain.entity.HighSchool
import picklab.backend.highschool.domain.repository.HighSchoolRepository

@Service
class HighSchoolService(
    private val highSchoolRepository: HighSchoolRepository,
) {
    @Transactional(readOnly = true)
    fun search(
        query: String?,
        size: Int,
    ): List<HighSchool> {
        if (size <= 0) {
            throw BusinessException(ErrorCode.BAD_REQUEST)
        }

        val trimmedQuery = query?.trim().orEmpty()
        if (trimmedQuery.isBlank()) {
            return emptyList()
        }

        return highSchoolRepository.searchByName(
            query = trimmedQuery,
            pageable = PageRequest.of(0, size.coerceAtMost(MAX_SIZE)),
        )
    }

    private companion object {
        const val MAX_SIZE = 20
    }
}
