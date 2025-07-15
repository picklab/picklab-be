package picklab.backend.member.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.member.domain.entity.NotificationPreference

interface NotificationPreferenceRepository : JpaRepository<NotificationPreference, Long> {
    fun findByMemberId(memberId: Long): NotificationPreference
    
    /**
     * 여러 사용자의 알림 설정을 한 번에 조회합니다 (성능 최적화용)
     */
    fun findAllByMemberIdIn(memberIds: List<Long>): List<NotificationPreference>
}
