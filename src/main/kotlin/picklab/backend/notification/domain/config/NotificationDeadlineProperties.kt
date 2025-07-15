package picklab.backend.notification.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.notification.deadline")
data class NotificationDeadlineProperties(
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
    var advanceDays: List<Int> = listOf(1, 3)
) 