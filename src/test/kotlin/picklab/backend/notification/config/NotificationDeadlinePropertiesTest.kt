package picklab.backend.notification.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import picklab.backend.job.template.IntegrationTest
import picklab.backend.notification.domain.config.NotificationDeadlineProperties

@DisplayName("알림 마감일 설정 프로퍼티 테스트")
class NotificationDeadlinePropertiesTest : IntegrationTest() {

    @Autowired
    private lateinit var notificationDeadlineProperties: NotificationDeadlineProperties

    @Test
    @DisplayName("설정 파일의 advance-days 값이 정상적으로 로드된다")
    fun `advanceDays_should_be_loaded_from_configuration`() {
        // Then
        assertThat(notificationDeadlineProperties.advanceDays).isNotEmpty
        assertThat(notificationDeadlineProperties.advanceDays).contains(1, 3)
    }

    @Test
    @DisplayName("설정 파일의 timezone 값이 정상적으로 로드된다")
    fun `timezone_should_be_loaded_from_configuration`() {
        // Then
        assertThat(notificationDeadlineProperties.timezone).isEqualTo("Asia/Seoul")
    }

    @Test
    @DisplayName("설정 파일의 enabled 값이 정상적으로 로드된다")
    fun `enabled_should_be_loaded_from_configuration`() {
        // Then
        assertThat(notificationDeadlineProperties.enabled).isTrue
    }

    @Test
    @DisplayName("설정 파일의 schedule 값이 정상적으로 로드된다")
    fun `schedule_should_be_loaded_from_configuration`() {
        // Then
        assertThat(notificationDeadlineProperties.schedule).isEqualTo("0 0 9 * * *")
    }
} 