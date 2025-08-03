package picklab.backend.search.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import picklab.backend.common.model.SoftDeleteEntity
import picklab.backend.member.domain.entity.Member
import java.time.LocalDateTime

@Entity
@Table(
    name = "member_search_history",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_member_search_history_member_keyword",
            columnNames = ["member_id", "keyword"]
        )
    ]
)
@SQLDelete(sql = "UPDATE member_search_history SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
class MemberSearchHistory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("검색한 회원")
    val member: Member,
    
    @Column(name = "keyword", nullable = false, columnDefinition = "TEXT")
    @Comment("검색 키워드")
    var keyword: String,
    
    @Column(name = "searched_at", nullable = false)
    @Comment("검색 실행 시간")
    var searchedAt: LocalDateTime = LocalDateTime.now(),
) : SoftDeleteEntity() {
    
    companion object {
        fun create(member: Member, keyword: String): MemberSearchHistory {
            return MemberSearchHistory(
                member = member,
                keyword = keyword.trim(),
                searchedAt = LocalDateTime.now()
            )
        }
    }

    /**
     * 검색 시간 업데이트 (동일 키워드 재검색 시)
     */
    fun search() {
        this.searchedAt = LocalDateTime.now()
    }
}