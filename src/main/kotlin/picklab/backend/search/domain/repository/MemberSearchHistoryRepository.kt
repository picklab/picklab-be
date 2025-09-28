package picklab.backend.search.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import picklab.backend.search.domain.entity.MemberSearchHistory
import java.time.LocalDateTime

interface MemberSearchHistoryRepository : JpaRepository<MemberSearchHistory, Long> {
    /**
     * 특정 회원의 검색 기록을 최신순으로 조회
     */
    fun findByMemberIdOrderBySearchedAtDesc(
        memberId: Long,
        pageable: Pageable,
    ): Page<MemberSearchHistory>

    /**
     * 인기 검색어 조회 (전체 사용자 기준)
     */
    @Query(
        """
        SELECT msh.keyword, COUNT(msh.keyword) as searchCount
        FROM MemberSearchHistory msh 
        WHERE msh.searchedAt >= :fromDate
        GROUP BY msh.keyword 
        ORDER BY searchCount DESC
    """,
    )
    fun findPopularKeywords(
        @Param("fromDate") fromDate: LocalDateTime,
        pageable: Pageable,
    ): List<Array<Any>>

    /**
     * 특정 회원의 특정 키워드 검색 기록이 있는지 확인
     */
    fun existsByMemberIdAndKeyword(
        memberId: Long,
        keyword: String,
    ): Boolean

    /**
     * 특정 회원의 특정 키워드 검색 기록 조회 (unique constraint로 인해 최대 1개)
     */
    fun findByMemberIdAndKeyword(
        memberId: Long,
        keyword: String,
    ): MemberSearchHistory?
}
