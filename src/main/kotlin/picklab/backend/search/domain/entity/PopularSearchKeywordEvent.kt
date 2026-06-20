package picklab.backend.search.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Comment
import picklab.backend.common.model.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(
    name = "popular_search_keyword_event",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_popular_search_keyword_event_hour",
            columnNames = ["keyword", "searcher_key", "search_hour"],
        ),
    ],
)
class PopularSearchKeywordEvent(
    @Column(name = "keyword", nullable = false, length = 255)
    @Comment("정규화된 검색 키워드")
    val keyword: String,
    @Column(name = "searcher_key", nullable = false, length = 100)
    @Comment("검색자 식별 키")
    val searcherKey: String,
    @Column(name = "search_hour", nullable = false)
    @Comment("검색 집계 시간대")
    val searchHour: LocalDateTime,
    @Column(name = "searched_at", nullable = false)
    @Comment("검색 실행 시간")
    val searchedAt: LocalDateTime,
) : BaseEntity()
