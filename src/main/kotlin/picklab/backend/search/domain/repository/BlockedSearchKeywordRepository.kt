package picklab.backend.search.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.search.domain.entity.BlockedSearchKeyword

interface BlockedSearchKeywordRepository : JpaRepository<BlockedSearchKeyword, Long> {
    fun existsByKeyword(keyword: String): Boolean
}
