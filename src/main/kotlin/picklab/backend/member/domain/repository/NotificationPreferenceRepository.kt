package picklab.backend.member.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import picklab.backend.member.domain.entity.NotificationPreference

interface NotificationPreferenceRepository : JpaRepository<NotificationPreference, Long> {
    fun findByMemberId(memberId: Long): NotificationPreference

    /**
     * 여러 사용자의 알림 설정을 한 번에 조회합니다 (성능 최적화용)
     */
    fun findAllByMemberIdIn(memberIds: List<Long>): List<NotificationPreference>

    /**
     * 인기 공고 알림이 활성화된 사용자들을 페이징으로 조회합니다
     */
    @Query("SELECT np.member.id FROM NotificationPreference np WHERE np.notifyPopularActivity = true")
    fun findMemberIdsWithPopularNotificationEnabled(pageable: Pageable): Page<Long>
}
