package picklab.backend.notification.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.repository.ActivityBookmarkRepository
import picklab.backend.activity.domain.service.ActivityService

import picklab.backend.common.util.logger
import picklab.backend.member.domain.MemberService
import picklab.backend.notification.domain.config.NotificationDeadlineProperties
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
    private val notificationDeadlineProperties: NotificationDeadlineProperties
) {

    private val logger = this.logger()

    /**
     * 설정된 시간대 기준으로 현재 날짜를 반환합니다
     */
    private fun getCurrentDateInConfiguredTimezone(): LocalDate {
        return LocalDate.now(ZoneId.of(notificationDeadlineProperties.timezone))
    }

    /**
     * 설정된 advance-days 기준으로 모든 마감일 알림을 생성하고 전송합니다
     */
    fun sendAllConfiguredDeadlineNotifications(): Map<Int, Int> {
        logger.info("마감일 알림 전송 시작: ${notificationDeadlineProperties.advanceDays} 일 전 대상")
        
        val baseDate = getCurrentDateInConfiguredTimezone()
        val results = notificationDeadlineProperties.advanceDays.associateWith { days ->
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
            logger.debug("마감 ${daysRemaining}일 전 알림 대상 활동 없음")
            return 0
        }

        logger.info("마감 ${daysRemaining}일 전 대상 활동: ${activities.size}개")
        
        val totalNotificationsSent = activities.sumOf { activity ->
            val sentCount = sendNotificationForActivity(activity, daysRemaining)
            logger.debug("활동 '${activity.title}' (ID: ${activity.id}): $sentCount 건 전송")
            sentCount
        }

        logger.info("마감 ${daysRemaining}일 전 알림: 총 $totalNotificationsSent 건 전송")
        return totalNotificationsSent
    }

    /**
     * 특정 활동에 대한 마감일 알림을 해당 활동을 북마크한 사용자들에게 전송합니다
     */
    private fun sendNotificationForActivity(activity: Activity, daysRemaining: Int): Int {
        val bookmarks = try {
            bookmarkRepository.findAllByActivityId(activity.id)
        } catch (e: Exception) {
            logger.error("북마크 조회 실패 (활동: ${activity.title}, ID: ${activity.id}): ${e.message}")
            throw IllegalStateException("활동 '${activity.title}'의 북마크 조회 실패", e)
        }
        
        if (bookmarks.isEmpty()) {
            logger.debug("활동 '${activity.title}' (ID: ${activity.id})을 북마크한 사용자 없음")
            return 0
        }

        logger.debug("활동 '${activity.title}' 북마크 사용자: ${bookmarks.size}명 (마감 ${daysRemaining}일 전)")

        val notifications = try {
            bookmarks.map { bookmark ->
                createDeadlineNotification(activity, bookmark.member.id, daysRemaining)
            }
        } catch (e: Exception) {
            logger.error("알림 생성 실패 (활동: ${activity.title}, 북마크 사용자: ${bookmarks.size}명): ${e.message}")
            throw IllegalStateException("활동 '${activity.title}'에 대한 알림 생성 실패", e)
        }

        // 배치로 알림 저장
        val savedNotifications = try {
            notificationRepository.saveAll(notifications)
        } catch (e: Exception) {
            logger.error("알림 저장 실패 (활동: ${activity.title}, 알림 ${notifications.size}건): ${e.message}")
            throw IllegalStateException("활동 '${activity.title}'에 대한 알림 저장 실패", e)
        }
        
        // 실시간 알림 전송 (실패해도 핵심 기능에는 영향 없음)
        savedNotifications.forEach { notification ->
            sendRealtimeNotification(notification)
        }

        logger.debug("활동 '${activity.title}' 마감일 알림 처리 완료: ${savedNotifications.size}건")
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

        val member = try {
            memberService.findActiveMember(memberId)
        } catch (e: Exception) {
            logger.warn("마감일 알림 생성 실패 - 유효하지 않은 사용자 (활동: ${activity.title}, 사용자 ID: $memberId): ${e.message}")
            throw IllegalStateException("활동 '${activity.title}'에 대한 마감일 알림 생성 실패: 사용자 ID $memberId 를 찾을 수 없음", e)
        }

        return Notification(
            title = title,
            type = NotificationType.ACTIVITY_DEADLINE_REMINDER,
            link = "/activities/${activity.id}",
            member = member
        )
    }

    /**
     * 실시간 알림을 전송합니다 (실패해도 핵심 기능에 영향 없음)
     */
    private fun sendRealtimeNotification(notification: Notification) {
        runCatching {
            if (!sseEmitterService.isUserConnected(notification.member.id)) {
                return // 연결되지 않은 사용자는 조용히 스킵
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