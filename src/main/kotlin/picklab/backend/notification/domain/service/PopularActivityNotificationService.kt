package picklab.backend.notification.domain.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.common.util.logger
import picklab.backend.member.domain.MemberService
import picklab.backend.member.domain.service.NotificationPreferenceService
import picklab.backend.notification.domain.config.NotificationProperties
import picklab.backend.notification.domain.entity.Notification
import picklab.backend.notification.domain.entity.NotificationType
import picklab.backend.notification.domain.repository.NotificationRepository

@Service
class PopularActivityNotificationService(
    private val activityService: ActivityService,
    private val notificationRepository: NotificationRepository,
    private val notificationPreferenceService: NotificationPreferenceService,
    private val memberService: MemberService,
    private val sseEmitterService: SseEmitterService,
    private val notificationProperties: NotificationProperties,
) {
    private val logger = this.logger()

    fun sendPopularActivityNotifications(): Int {
        logger.info("인기 공고 알림 전송 시작")

        // 1. 가장 인기 있는 활동 조회
        val popularActivity = activityService.getMostPopularActivity()
        if (popularActivity == null) {
            logger.info("인기 공고 알림 전송 완료: 전송할 인기 공고 없음")
            return 0
        }

        var totalSentCount = 0
        var currentPage = 0
        val batchSize = notificationProperties.popular.batchSize

        while (true) {
            val pageable = PageRequest.of(currentPage, batchSize)
            val memberIdsPage = notificationPreferenceService.getMembersWithPopularNotificationEnabledPaged(pageable)

            if (memberIdsPage.isEmpty) {
                break
            }

            val memberIds = memberIdsPage.content

            val batchSentCount =
                runCatching {
                    sendNotificationBatch(popularActivity.id, popularActivity.title, memberIds, currentPage + 1)
                }.getOrElse { exception ->
                    logger.error("인기 공고 알림 배치 ${currentPage + 1} 처리 실패: ${exception.message}", exception)
                    0 // 실패한 배치는 0건으로 처리
                }

            totalSentCount += batchSentCount

            currentPage++

            // 마지막 페이지면 종료
            if (memberIdsPage.isLast) {
                break
            }
        }

        if (totalSentCount == 0) {
            logger.info("인기 공고 알림 전송 완료: 알림 수신 동의한 사용자 없음")
        } else {
            logger.info("인기 공고 알림 전송 완료: 총 ${totalSentCount}건 전송")
        }

        return totalSentCount
    }

    /**
     * 배치 단위로 알림을 전송합니다 (독립적인 트랜잭션)
     */
    fun sendNotificationBatch(
        activityId: Long,
        activityTitle: String,
        memberIds: List<Long>,
        batchNumber: Int,
    ): Int {
        // 해당 활동에 대한 인기 공고 알림을 이미 받지 않은 사용자들만 필터링
        val filteredMemberIds =
            memberIds.filter { memberId ->
                !notificationRepository.existsByTypeAndReferenceIdAndMemberId(
                    NotificationType.POPULAR_ACTIVITY,
                    activityId.toString(),
                    memberId,
                )
            }

        if (filteredMemberIds.isEmpty()) {
            logger.info("인기 공고 알림 배치 $batchNumber 처리 완료: 0건 전송 (모든 사용자가 이미 알림 수신)")
            return 0
        }

        val notifications =
            filteredMemberIds.map { memberId ->
                createPopularActivityNotification(activityId, activityTitle, memberId)
            }

        val savedNotifications = notificationRepository.saveAll(notifications)

        // 실시간 알림 전송 (트랜잭션 외부에서 처리)
        savedNotifications.forEach { notification ->
            sendRealtimeNotification(notification)
        }

        logger.info(
            "인기 공고 알림 배치 $batchNumber 처리 완료: ${savedNotifications.size}건 전송 (전체 ${memberIds.size}명 중 ${filteredMemberIds.size}명에게 전송)",
        )
        return savedNotifications.size
    }

    /**
     * 인기 공고 알림 엔티티를 생성합니다
     */
    private fun createPopularActivityNotification(
        activityId: Long,
        activityTitle: String,
        memberId: Long,
    ): Notification {
        val title = "🔥 오늘의 인기 공고! '$activityTitle' 많은 관심을 받고 있어요"
        val member = memberService.findActiveMember(memberId)

        return Notification(
            title = title,
            type = NotificationType.POPULAR_ACTIVITY,
            link = "/activities/$activityId",
            member = member,
            referenceId = activityId.toString(),
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

            val eventData =
                mapOf(
                    "id" to notification.id,
                    "title" to notification.title,
                    "type" to notification.type.name,
                    "link" to notification.link,
                    "createdAt" to notification.createdAt,
                )

            val success =
                sseEmitterService.sendEventToUser(
                    notification.member.id,
                    "popular_notification",
                    eventData,
                )

            if (!success) {
                logger.warn("실시간 알림 전송 실패: 사용자 ${notification.member.id}")
            }
        }.onFailure { exception ->
            logger.warn("실시간 알림 전송 중 오류 (사용자 ${notification.member.id}): ${exception.message}")
        }
    }
}
