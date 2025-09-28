package picklab.backend.notification.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.notification")
data class NotificationProperties(
    val deadline: DeadlineNotificationConfig = DeadlineNotificationConfig(),
    val popular: PopularNotificationConfig = PopularNotificationConfig(),
)

data class DeadlineNotificationConfig(
    /**
     * 활동 마감일 알림 배치 작업 활성화 여부
     */
    var enabled: Boolean = true,
    /**
     * 배치 작업 실행 스케줄 (Cron 표현식)
     */
    var schedule: String = "0 0 9 * * *",
    /**
     * 시간대 설정
     */
    var timezone: String = "Asia/Seoul",
    /**
     * 알림을 보낼 마감일 전 일수 목록
     */
    var advanceDays: List<Int> = listOf(1, 3),
)

data class PopularNotificationConfig(
    /**
     * 인기 공고 알림 배치 작업 활성화 여부
     */
    var enabled: Boolean = true,
    /**
     * 배치 작업 실행 스케줄 (Cron 표현식) - 매일 KST 12시
     */
    var schedule: String = "0 0 12 * * *",
    /**
     * 시간대 설정
     */
    var timezone: String = "Asia/Seoul",
    /**
     * 배치 처리 시 한 번에 처리할 사용자 수
     */
    var batchSize: Int = 100,
)
