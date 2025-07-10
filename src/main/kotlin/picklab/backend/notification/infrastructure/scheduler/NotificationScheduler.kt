package picklab.backend.notification.infrastructure.scheduler

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.common.util.logger
import picklab.backend.notification.domain.repository.NotificationRepository
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
    @Value("\${app.notification.cleanup.retention-days:30}")
    private val retentionDays: Long,
    @Value("\${app.notification.cleanup.batch-size:100}")
    private val batchSize: Int
) {

    private val logger = this.logger()

    /**
     * 설정된 보관 기간을 초과한 알림을 자동으로 삭제합니다.
     * 매일 새벽 2시(UTC)에 실행됩니다.
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
} 