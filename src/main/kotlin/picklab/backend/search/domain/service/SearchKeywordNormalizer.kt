package picklab.backend.search.domain.service

import org.springframework.stereotype.Component
import java.util.Locale

@Component
class SearchKeywordNormalizer {
    fun normalize(keyword: String): String = keyword.trim().lowercase(Locale.ROOT)
}
