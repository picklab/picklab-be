package picklab.backend.search.application

import org.springframework.stereotype.Component
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.search.entrypoint.response.AutocompleteResponse

@Component
class SearchUseCase(
    private val activityRepository: ActivityRepository,
) {
    
    /**
     * 활동명 자동완성 검색
     */
    fun getAutocompleteResults(keyword: String, limit: Int): AutocompleteResponse {
        // 키워드가 비어있거나 공백만 있으면 빈 결과 반환
        val trimmedKeyword = keyword.trim()
        if (trimmedKeyword.isEmpty()) {
            return AutocompleteResponse(emptyList())
        }
        
        // limit 값 검증 및 제한 (1~50 사이로 제한)
        val validatedLimit = limit.coerceIn(1, 50)
        
        val suggestions = activityRepository.findActivityTitlesForAutocomplete(trimmedKeyword, validatedLimit)
        return AutocompleteResponse(suggestions)
    }
} 