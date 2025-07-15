package picklab.backend.member.domain.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.member.domain.entity.NotificationPreference
import picklab.backend.member.domain.enums.NotificationType
import picklab.backend.member.domain.repository.NotificationPreferenceRepository

@Service
@Transactional(readOnly = true)
class NotificationPreferenceService(
    private val notificationPreferenceRepository: NotificationPreferenceRepository
) {

    /**
     * 특정 사용자의 알림 설정을 조회합니다
     */
    fun getNotificationPreference(memberId: Long): NotificationPreference {
        return notificationPreferenceRepository.findByMemberId(memberId)
    }

    /**
     * 여러 사용자의 알림 설정을 배치로 조회합니다 (성능 최적화용)
     */
    fun getNotificationPreferences(memberIds: List<Long>): Map<Long, NotificationPreference> {
        return notificationPreferenceRepository.findAllByMemberIdIn(memberIds)
            .associateBy { it.member.id }
    }

    /**
     * 북마크 알림 수신에 동의한 사용자들만 필터링합니다
     */
    fun filterMembersWithBookmarkNotificationEnabled(memberIds: List<Long>): List<Long> {
        val preferences = getNotificationPreferences(memberIds)
        return memberIds
            .filter { memberId -> preferences[memberId]?.notifyBookmarkedActivity == true }
    }


    /**
     * 인기 공고 알림 수신에 동의한 사용자들을 페이징으로 조회합니다 (성능 최적화용)
     */
    fun getMembersWithPopularNotificationEnabledPaged(pageable: Pageable): Page<Long> {
        return notificationPreferenceRepository.findMemberIdsWithPopularNotificationEnabled(pageable)
    }

    /**
     * 사용자의 알림 설정을 토글합니다
     */
    @Transactional
    fun toggleNotification(memberId: Long, type: NotificationType) {
        val preference = getNotificationPreference(memberId)

        when (type) {
            NotificationType.POPULAR -> preference.togglePopular()
            NotificationType.BOOKMARKED -> preference.toggleBookmarked()
        }

        notificationPreferenceRepository.save(preference)
    }

    @Transactional
    fun save(entity: NotificationPreference) = notificationPreferenceRepository.save(entity)
} 