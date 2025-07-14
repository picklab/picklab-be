package picklab.backend.notification.infrastructure.scheduler

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.enums.EmploymentType
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.notification.domain.entity.Notification
import picklab.backend.notification.domain.entity.NotificationType
import picklab.backend.notification.domain.repository.NotificationRepository
import picklab.backend.template.IntegrationTest
import java.time.LocalDateTime

@TestPropertySource(
    properties = [
        "app.notification.cleanup.enabled=true",
        "app.notification.cleanup.retention-days=30",
        "app.notification.cleanup.batch-size=10",
    ],
)
class NotificationSchedulerTest : IntegrationTest() {
    @Autowired
    private lateinit var notificationScheduler: NotificationScheduler

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    private lateinit var testMember: Member

    @BeforeEach
    fun setUp() {
        cleanUp.all()

        testMember =
            memberRepository.save(
                Member(
                    name = "테스트 사용자",
                    email = "test@example.com",
                    company = "테스트 회사",
                    school = "테스트 대학교",
                    department = "컴퓨터공학과",
                    nickname = "테스트닉네임",
                    educationLevel = "대학교 졸업",
                    graduationStatus = "졸업",
                    employmentStatus = "재직중",
                    employmentType = EmploymentType.FULL_TIME,
                    isCompleted = true,
                ),
            )
    }

    @Test
    @DisplayName("[성공] 30일 이전에 생성된 알림을 soft delete 처리한다")
    @Transactional
    fun `30일 이전에 생성된 알림을 soft delete 처리한다`() {
        // Given: 30일 이전 알림과 30일 이내 알림 생성
        val oldNotification =
            createNotificationWithCreatedAt(
                title = "30일 이전 알림",
                createdAt = LocalDateTime.now().minusDays(35),
            )

        val recentNotification =
            createNotificationWithCreatedAt(
                title = "최근 알림",
                createdAt = LocalDateTime.now().minusDays(10),
            )

        val todayNotification =
            createNotificationWithCreatedAt(
                title = "오늘 알림",
                createdAt = LocalDateTime.now(),
            )

        // 저장 전 상태 확인
        assertThat(oldNotification.deletedAt).isNull()
        assertThat(recentNotification.deletedAt).isNull()
        assertThat(todayNotification.deletedAt).isNull()

        // When: 스케줄러 실행
        notificationScheduler.cleanupOldNotifications()
        entityManager.flush()
        entityManager.clear()

        // Then: 30일 이전 알림만 soft delete 되어야 함
        val updatedOldNotification = notificationRepository.findByIdIgnoreDelete(oldNotification.id)
        val updatedRecentNotification = notificationRepository.findById(recentNotification.id).get()
        val updatedTodayNotification = notificationRepository.findById(todayNotification.id).get()

        // 30일 이전 알림은 조회되어야 함
        assertThat(updatedOldNotification)
            .withFailMessage("30일 이전 알림이 조회되지 않았습니다")
            .isNotNull()

        // null이 아님을 확인했으므로 안전하게 사용
        val oldNotificationNotNull =
            checkNotNull(updatedOldNotification) {
                "30일 이전 알림이 null입니다"
            }

        assertThat(oldNotificationNotNull.deletedAt)
            .withFailMessage("30일 이전 알림의 deletedAt이 설정되지 않았습니다")
            .isNotNull()

        // 30일 이내 알림들은 그대로 유지
        assertThat(updatedRecentNotification.deletedAt)
            .withFailMessage("최근 알림이 삭제되었습니다")
            .isNull()

        assertThat(updatedTodayNotification.deletedAt)
            .withFailMessage("오늘 알림이 삭제되었습니다")
            .isNull()

        // Repository 메서드로는 soft delete된 알림이 조회되지 않음 (SQLRestriction 적용)
        val activeNotifications = notificationRepository.findAll()
        assertThat(activeNotifications).hasSize(2)
        assertThat(activeNotifications.map { it.title })
            .containsExactlyInAnyOrder("최근 알림", "오늘 알림")
    }

    /**
     * 특정 생성일시를 가진 알림을 생성하는 헬퍼 메서드
     */
    private fun createNotificationWithCreatedAt(
        title: String,
        createdAt: LocalDateTime,
    ): Notification {
        val notification =
            Notification(
                title = title,
                type = NotificationType.ACTIVITY_CREATED,
                link = "/test-link",
                member = testMember,
            )

        val savedNotification = notificationRepository.saveAndFlush(notification)

        // Native query를 사용해서 created_at을 직접 업데이트 (테스트용)
        entityManager
            .createNativeQuery(
                "UPDATE notification SET created_at = ? WHERE id = ?",
            ).apply {
                setParameter(1, createdAt)
                setParameter(2, savedNotification.id)
            }.executeUpdate()

        entityManager.flush()
        entityManager.clear()

        return findNotificationByIdIgnoringDeletedAt(savedNotification.id)!!
    }

    /**
     * deleted_at 조건을 무시하고 알림을 조회하는 헬퍼 메서드
     */
    private fun findNotificationByIdIgnoringDeletedAt(notificationId: Long): Notification? =
        entityManager
            .createNativeQuery(
                "SELECT * FROM notification WHERE id = ?",
                Notification::class.java,
            ).apply {
                setParameter(1, notificationId)
            }.resultList
            .firstOrNull() as Notification?
}
