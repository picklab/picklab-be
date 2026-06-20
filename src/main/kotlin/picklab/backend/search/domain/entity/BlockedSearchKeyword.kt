package picklab.backend.search.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity

@Entity
@Table(name = "blocked_search_keyword")
class BlockedSearchKeyword(
    @Column(name = "keyword", nullable = false, unique = true, length = 255)
    @Comment("정규화된 차단 검색어")
    val keyword: String,
) : BaseEntity()
