package picklab.backend.search.domain.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.entity.Member
import picklab.backend.search.domain.entity.MemberSearchHistory
import picklab.backend.search.domain.repository.MemberSearchHistoryRepository

@Service
class MemberSearchHistoryService(
    private val memberSearchHistoryRepository: MemberSearchHistoryRepository,
) {

    /**
     * 검색 기록 생성 또는 업데이트 (upsert)
     */
    @Transactional
    fun createSearchHistory(member: Member, keyword: String): MemberSearchHistory {
        val trimmedKeyword = keyword.trim()
        if (trimmedKeyword.isEmpty()) {
            throw BusinessException(ErrorCode.INVALID_INPUT_VALUE)
        }

        val existingHistory = memberSearchHistoryRepository.findByMemberIdAndKeyword(member.id, trimmedKeyword)

        return if (existingHistory != null) {
            existingHistory.updateSearchedAt()
            memberSearchHistoryRepository.save(existingHistory)
        } else {
            // 새로운 검색 기록 생성
            val searchHistory = MemberSearchHistory.create(member, trimmedKeyword)
            memberSearchHistoryRepository.save(searchHistory)
        }
    }

    /**
     * 개인 검색 기록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    fun getSearchHistory(memberId: Long, page: Int, size: Int): Page<MemberSearchHistory> {
        val pageable = PageRequest.of(page - 1, size)
        return memberSearchHistoryRepository.findByMemberIdOrderBySearchedAtDesc(memberId, pageable)
    }

    /**
     * 최근 검색어 조회 (최신순) - unique constraint로 중복 없음
     */
    @Transactional(readOnly = true)
    fun getRecentKeywords(memberId: Long, limit: Int): List<MemberSearchHistory> {
        val validatedLimit = limit.coerceIn(1, 20)
        val pageable = PageRequest.of(0, validatedLimit)
        return memberSearchHistoryRepository.findByMemberIdOrderBySearchedAtDesc(memberId, pageable).content
    }

    /**
     * 검색 기록 조회 (본인 확인)
     */
    @Transactional(readOnly = true)
    fun findSearchHistoryWithOwnerCheck(memberId: Long, historyId: Long): MemberSearchHistory {
        val searchHistory = memberSearchHistoryRepository.findById(historyId)
            .orElseThrow { BusinessException(ErrorCode.SEARCH_HISTORY_NOT_FOUND) }

        // 본인의 검색 기록인지 확인
        if (searchHistory.member.id != memberId) {
            throw BusinessException(ErrorCode.FORBIDDEN)
        }

        return searchHistory
    }

    /**
     * 개별 검색 기록 삭제
     */
    @Transactional
    fun deleteSearchHistory(memberId: Long, historyId: Long) {
        val searchHistory = findSearchHistoryWithOwnerCheck(memberId, historyId)
        searchHistory.delete() // SoftDelete
        memberSearchHistoryRepository.save(searchHistory)
    }

    /**
     * 전체 검색 기록 삭제
     */
    @Transactional
    fun deleteAllSearchHistory(memberId: Long) {
        val searchHistories = memberSearchHistoryRepository.findByMemberIdOrderBySearchedAtDesc(
            memberId,
            Pageable.unpaged()
        ).content

        searchHistories.forEach { it.delete() }
        memberSearchHistoryRepository.saveAll(searchHistories)
    }
} 