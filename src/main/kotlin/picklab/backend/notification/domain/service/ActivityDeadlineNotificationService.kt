package picklab.backend.notification.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.repository.ActivityBookmarkRepository
import picklab.backend.activity.domain.service.ActivityService

import picklab.backend.common.util.logger
import picklab.backend.member.domain.MemberService
import picklab.backend.member.domain.service.NotificationPreferenceService
import picklab.backend.notification.domain.config.NotificationProperties
import picklab.backend.notification.domain.entity.Notification
import picklab.backend.notification.domain.entity.NotificationType
import picklab.backend.notification.domain.repository.NotificationRepository
import java.time.LocalDate
import java.time.ZoneId

@Service
@Transactional
class ActivityDeadlineNotificationService(
    private val activityService: ActivityService,
    private val bookmarkRepository: ActivityBookmarkRepository,
    private val notificationRepository: NotificationRepository,
    private val sseEmitterService: SseEmitterService,
    private val memberService: MemberService,
    private val notificationPreferenceService: NotificationPreferenceService,
    private val notificationProperties: NotificationProperties
) {

    private val logger = this.logger()

    /**
     * 설정된 advance-days 기준으로 모든 마감일 알림을 생성하고 전송합니다
     */
    fun sendAllConfiguredDeadlineNotifications(): Map<Int, Int> {
        logger.info("마감일 알림 전송 시작: ${notificationProperties.deadline.advanceDays} 일 전 대상")
        
        val baseDate = LocalDate.now(ZoneId.of(notificationProperties.deadline.timezone))
        val results = notificationProperties.deadline.advanceDays.associateWith { days ->
            val sentCount = sendDeadlineNotificationsForDays(baseDate, days)
            logger.info("마감 ${days}일 전 알림: $sentCount 건 전송 완료")
            sentCount
        }
        
        val totalSent = results.values.sum()
        logger.info("마감일 알림 전송 완료: 총 $totalSent 건")
        
        return results
    }

    /**
     * 특정 일수 후 마감되는 활동들에 대한 알림을 생성하고 전송합니다
     */
    fun sendDeadlineNotificationsForDays(baseDate: LocalDate, daysUntilDeadline: Int): Int {
        val activities = activityService.getActivitiesEndingInDays(baseDate, daysUntilDeadline)
        return sendDeadlineNotifications(activities, daysUntilDeadline)
    }

    /**
     * 마감일 알림을 생성하고 전송합니다
     */
    private fun sendDeadlineNotifications(activities: List<Activity>, daysRemaining: Int): Int {
        if (activities.isEmpty()) {
            return 0
        }

        logger.info("마감 ${daysRemaining}일 전 대상 활동: ${activities.size}개")
        
        val totalNotificationsSent = activities.sumOf { activity ->
            sendNotificationForActivity(activity, daysRemaining)
        }

        logger.info("마감 ${daysRemaining}일 전 알림: 총 $totalNotificationsSent 건 전송")
        return totalNotificationsSent
    }

    /**
     * 특정 활동에 대한 마감일 알림을 해당 활동을 북마크한 사용자들에게 전송합니다
     */
    private fun sendNotificationForActivity(activity: Activity, daysRemaining: Int): Int {
        val bookmarks = bookmarkRepository.findAllByActivityId(activity.id)

        if (bookmarks.isEmpty()) {
            return 0
        }

        val memberIds = bookmarks.map { it.member.id }
        val eligibleMemberIds = notificationPreferenceService.filterMembersWithBookmarkNotificationEnabled(memberIds)

        if (eligibleMemberIds.isEmpty()) {
            return 0
        }

        val notifications = eligibleMemberIds.map { memberId ->
            createDeadlineNotification(activity, memberId, daysRemaining)
        }

        val savedNotifications = notificationRepository.saveAll(notifications)
        
        savedNotifications.forEach { notification ->
            sendRealtimeNotification(notification)
        }

        return savedNotifications.size
    }

    /**
     * 마감일 알림 엔티티를 생성합니다
     */
    private fun createDeadlineNotification(activity: Activity, memberId: Long, daysRemaining: Int): Notification {
        val title = if (daysRemaining == 1) {
            "⏰ 내일 마감! '${activity.title}' 지원 마감 하루 전입니다"
        } else {
            "⚠️ ${daysRemaining}일 후 마감! '${activity.title}' 지원 마감이 ${daysRemaining}일 남았습니다"
        }

        val member = memberService.findActiveMember(memberId)

        return Notification(
            title = title,
            type = NotificationType.ACTIVITY_DEADLINE_REMINDER,
            link = "/activities/${activity.id}",
            member = member
        )
    }

    /**
     * 실시간 알림을 전송합니다
     */
    private fun sendRealtimeNotification(notification: Notification) {
        runCatching {
            if (!sseEmitterService.isUserConnected(notification.member.id)) {
                return
            }

            val eventData = mapOf(
                "id" to notification.id,
                "title" to notification.title,
                "type" to notification.type.name,
                "link" to notification.link,
                "createdAt" to notification.createdAt
            )

            val success = sseEmitterService.sendEventToUser(
                notification.member.id,
                "deadline_notification",
                eventData
            )

            if (!success) {
                logger.warn("실시간 알림 전송 실패: 사용자 ${notification.member.id}")
            }
        }.onFailure { exception ->
            logger.warn("실시간 알림 전송 중 오류 (사용자 ${notification.member.id}): ${exception.message}")
        }
    }
} 