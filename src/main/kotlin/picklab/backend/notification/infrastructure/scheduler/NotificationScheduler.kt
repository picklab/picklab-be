package picklab.backend.notification.infrastructure.scheduler

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.dao.DataAccessException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.common.util.logger
import picklab.backend.notification.domain.repository.NotificationRepository
import picklab.backend.notification.domain.service.ActivityDeadlineNotificationService
import java.sql.SQLException
import java.time.LocalDateTime

/**
 * 알림 관련 배치 작업을 처리하는 스케줄러
 */
@Component
@ConditionalOnProperty(
    name = ["app.notification.cleanup.enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class NotificationScheduler(
    private val notificationRepository: NotificationRepository,
    private val activityDeadlineNotificationService: ActivityDeadlineNotificationService,
    @Value("\${app.notification.cleanup.retention-days:30}")
    private val retentionDays: Long,
    @Value("\${app.notification.cleanup.batch-size:100}")
    private val batchSize: Int
) {

    private val logger = this.logger()

    /**
     * 설정된 보관 기간을 초과한 알림을 자동으로 삭제합니다.
     * 매일 새벽 자정(UTC)에 실행됩니다.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    @Transactional
    fun cleanupOldNotifications() {
        try {
            logger.info("알림 정리 배치 작업 시작")
            
            val cutoffDate = calculateCutoffDate()
            val deletedCount = processCleanupInBatches(cutoffDate)
            
            logger.info("알림 정리 완료: $deletedCount 건 삭제")
            
        } catch (e: Exception) {
            logger.error("알림 정리 배치 작업 중 오류 발생", e)
            throw e
        }
    }

    /**
     * 배치 단위로 알림을 조회하고 삭제합니다
     */
    private fun processCleanupInBatches(cutoffDate: LocalDateTime): Int {
        var totalDeletedCount = 0
        
        while (true) {
            val pageable: Pageable = PageRequest.of(0, batchSize)
            val notificationsPage = notificationRepository.findByCreatedAtBeforeOrderByCreatedAtAsc(cutoffDate, pageable)
            
            if (notificationsPage.isEmpty) {
                break
            }
            
            val notifications = notificationsPage.content
            notificationRepository.deleteAll(notifications)
            totalDeletedCount += notifications.size
        }
        
        return totalDeletedCount
    }

    /**
     * 삭제 기준 날짜를 계산합니다
     */
    private fun calculateCutoffDate(): LocalDateTime = LocalDateTime.now().minusDays(retentionDays)

    /**
     * 활동 마감일 알림을 전송합니다.
     * 설정된 스케줄(기본: 매일 오전 9시)과 시간대(기본: Asia/Seoul)에 따라 실행됩니다.
     * 설정된 advance-days 목록에 따라 동적으로 알림을 전송합니다.
     */
    @Scheduled(
        cron = "\${app.notification.deadline.schedule:0 0 9 * * *}",
        zone = "\${app.notification.deadline.timezone:Asia/Seoul}"
    )
    @ConditionalOnProperty(
        name = ["app.notification.deadline.enabled"],
        havingValue = "true",
        matchIfMissing = true
    )
    @Transactional
    fun sendActivityDeadlineNotifications() {
        val startTime = System.currentTimeMillis()
        
        runCatching {
            logger.info("활동 마감일 알림 배치 작업 시작")
            
            val results = activityDeadlineNotificationService.sendAllConfiguredDeadlineNotifications()
            val totalNotifications = results.values.sum()
            val duration = System.currentTimeMillis() - startTime
            
            if (totalNotifications > 0) {
                logger.info("활동 마감일 알림 배치 작업 완료: 총 $totalNotifications 건 전송 (소요시간: ${duration}ms)")
                // 결과 상세 로깅
                results.forEach { (days, count) ->
                    if (count > 0) {
                        logger.info("  - 마감 ${days}일 전: $count 건")
                    }
                }
            } else {
                logger.info("활동 마감일 알림 배치 작업 완료: 전송할 알림 없음 (소요시간: ${duration}ms)")
            }
            
        }.onFailure { exception ->
            val duration = System.currentTimeMillis() - startTime
            val errorType = when (exception) {
                is IllegalStateException -> "데이터 처리 오류"
                is DataAccessException -> "데이터베이스 오류"
                is SQLException -> "SQL 실행 오류"
                else -> "예상치 못한 오류"
            }
            logger.error("활동 마감일 알림 배치 작업 실패 - $errorType (소요시간: ${duration}ms): ${exception.message}", exception)
        }
    }
} 