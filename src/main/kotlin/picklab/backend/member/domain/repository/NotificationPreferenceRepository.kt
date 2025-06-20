package picklab.backend.member.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.member.domain.entity.NotificationPreference

interface NotificationPreferenceRepository : JpaRepository<NotificationPreference, Long> {
    fun findByMemberId(memberId: Long): NotificationPreference
}
