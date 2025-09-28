package picklab.backend.notification.domain.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.entity.ActivityBookmark
import picklab.backend.activity.domain.entity.ActivityGroup
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.domain.repository.ActivityBookmarkRepository
import picklab.backend.activity.domain.repository.ActivityGroupRepository
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.entity.NotificationPreference
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.member.domain.repository.NotificationPreferenceRepository
import picklab.backend.notification.domain.entity.NotificationType
import picklab.backend.notification.domain.repository.NotificationRepository
import picklab.backend.template.IntegrationTest
import java.time.LocalDate

class PopularActivityNotificationServiceTest : IntegrationTest() {
    @Autowired
    lateinit var popularActivityNotificationService: PopularActivityNotificationService

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Autowired
    lateinit var activityGroupRepository: ActivityGroupRepository

    @Autowired
    lateinit var activityBookmarkRepository: ActivityBookmarkRepository

    @Autowired
    lateinit var notificationRepository: NotificationRepository

    @Autowired
    lateinit var notificationPreferenceRepository: NotificationPreferenceRepository

    private lateinit var activityGroup: ActivityGroup

    @BeforeEach
    fun setUp() {
        cleanUp.all()
        activityGroup =
            activityGroupRepository.save(
                ActivityGroup(
                    name = "테스트 그룹",
                    description = "테스트 그룹 설명",
                ),
            )
    }

    @Test
    @DisplayName("인기 공고 알림을 끈 사용자에게는 알림이 가지 않는다")
    fun `should not send notification to users who disabled popular activity notification`() {
        // given
        val popularActivity = createPopularActivity("인기 공고", viewCount = 100L, bookmarkCount = 3)
        val userWithNotificationOff = createMember("알림끈사용자")
        val userWithNotificationOn = createMember("알림켠사용자")

        // 알림 설정 - 한 명은 OFF, 한 명은 ON
        createNotificationPreference(userWithNotificationOff, popularEnabled = false)
        createNotificationPreference(userWithNotificationOn, popularEnabled = true)

        // when
        val result = popularActivityNotificationService.sendPopularActivityNotifications()

        // then
        assertThat(result).isEqualTo(1) // 1건만 전송 (알림 켠 사용자만)

        val notifications = notificationRepository.findAll()
        assertThat(notifications).hasSize(1)
        assertThat(notifications[0].member.id).isEqualTo(userWithNotificationOn.id)
        assertThat(notifications[0].type).isEqualTo(NotificationType.POPULAR_ACTIVITY)
        assertThat(notifications[0].title).contains("오늘의 인기 공고!")
        assertThat(notifications[0].link).isEqualTo("/activities/${popularActivity.id}")
    }

    @Test
    @DisplayName("인기 공고 알림을 킨 사용자에게는 인기 공고 알림이 간다")
    fun `should send notification to users who enabled popular activity notification`() {
        // given
        val popularActivity = createPopularActivity("인기 공고", viewCount = 50L, bookmarkCount = 5)
        val user1 = createMember("사용자1")
        val user2 = createMember("사용자2")
        val user3 = createMember("사용자3")

        // 모든 사용자 알림 설정 ON
        createNotificationPreference(user1, popularEnabled = true)
        createNotificationPreference(user2, popularEnabled = true)
        createNotificationPreference(user3, popularEnabled = true)

        // when
        val result = popularActivityNotificationService.sendPopularActivityNotifications()

        // then
        assertThat(result).isEqualTo(3) // 3건 전송

        val notifications = notificationRepository.findAll()
        assertThat(notifications).hasSize(3)

        val memberIds = notifications.map { it.member.id }.toSet()
        assertThat(memberIds).containsExactlyInAnyOrder(user1.id, user2.id, user3.id)

        notifications.forEach { notification ->
            assertThat(notification.type).isEqualTo(NotificationType.POPULAR_ACTIVITY)
            assertThat(notification.title).contains("인기 공고")
            assertThat(notification.link).isEqualTo("/activities/${popularActivity.id}")
        }
    }

    @Test
    @DisplayName("같은 사용자가 같은 인기 공고 알림을 2번 이상 받지 않는다")
    fun `should not send duplicate notification for same popular activity to same user`() {
        // given
        createPopularActivity("인기 공고", viewCount = 200L, bookmarkCount = 10)
        val user1 = createMember("사용자1")
        val user2 = createMember("사용자2")

        // 모든 사용자 알림 설정 ON
        createNotificationPreference(user1, popularEnabled = true)
        createNotificationPreference(user2, popularEnabled = true)

        // when - 첫 번째 실행
        val firstResult = popularActivityNotificationService.sendPopularActivityNotifications()

        // then - 첫 번째 실행 결과 확인
        assertThat(firstResult).isEqualTo(2) // 2건 전송
        assertThat(notificationRepository.findAll()).hasSize(2)

        // when - 두 번째 실행 (같은 인기 공고)
        val secondResult = popularActivityNotificationService.sendPopularActivityNotifications()

        // then - 두 번째 실행에서는 사용자별 중복 방지로 0건 전송
        assertThat(secondResult).isEqualTo(0) // 0건 전송 (사용자별 중복 방지)
        assertThat(notificationRepository.findAll()).hasSize(2) // 여전히 2건만 존재
    }

    @Test
    @DisplayName("다른 인기 공고가 나타나면 사용자들이 새로운 알림을 받을 수 있다")
    fun `should send notification for different popular activity even after previous notification`() {
        // given
        val user1 = createMember("사용자1")
        val user2 = createMember("사용자2")

        // 모든 사용자 알림 설정 ON
        createNotificationPreference(user1, popularEnabled = true)
        createNotificationPreference(user2, popularEnabled = true)

        // 첫 번째 인기 공고
        createPopularActivity("첫 번째 인기 공고", viewCount = 200L, bookmarkCount = 10)

        // when - 첫 번째 인기 공고 알림 전송
        val firstResult = popularActivityNotificationService.sendPopularActivityNotifications()

        // then
        assertThat(firstResult).isEqualTo(2)
        assertThat(notificationRepository.findAll()).hasSize(2)

        // given - 두 번째 인기 공고가 더 인기해짐
        createPopularActivity("두 번째 인기 공고", viewCount = 300L, bookmarkCount = 15)

        // when - 두 번째 인기 공고 알림 전송
        val secondResult = popularActivityNotificationService.sendPopularActivityNotifications()

        // then - 새로운 활동이므로 다시 알림 전송
        assertThat(secondResult).isEqualTo(2) // 새로운 활동에 대해 2건 전송
        assertThat(notificationRepository.findAll()).hasSize(4) // 총 4건 존재
    }

    @Test
    @DisplayName("인기 공고가 없는 경우 알림을 전송하지 않는다")
    fun `should not send notification when no popular activity exists`() {
        // given
        val user = createMember("사용자")
        createNotificationPreference(user, popularEnabled = true)
        // 인기 공고 없음 (Activity 생성하지 않음)

        // when
        val result = popularActivityNotificationService.sendPopularActivityNotifications()

        // then
        assertThat(result).isEqualTo(0)
        assertThat(notificationRepository.findAll()).isEmpty()
    }

    @Test
    @DisplayName("모든 사용자가 인기 공고 알림을 끈 경우 알림을 전송하지 않는다")
    fun `should not send notification when all users disabled popular activity notification`() {
        // given
        createPopularActivity("인기 공고", viewCount = 100L, bookmarkCount = 5)
        val user1 = createMember("사용자1")
        val user2 = createMember("사용자2")

        // 모든 사용자 알림 설정 OFF
        createNotificationPreference(user1, popularEnabled = false)
        createNotificationPreference(user2, popularEnabled = false)

        // when
        val result = popularActivityNotificationService.sendPopularActivityNotifications()

        // then
        assertThat(result).isEqualTo(0)
        assertThat(notificationRepository.findAll()).isEmpty()
    }

    private fun createPopularActivity(
        title: String,
        viewCount: Long,
        bookmarkCount: Int,
    ): Activity {
        val activity =
            activityRepository.save(
                ExternalActivity(
                    title = title,
                    organizer = OrganizerType.PUBLIC_ORGANIZATION,
                    targetAudience = ParticipantType.ALL,
                    location = LocationType.SEOUL_INCHEON,
                    recruitmentStartDate = LocalDate.now().minusDays(10),
                    recruitmentEndDate = LocalDate.now().plusDays(10), // 모집 중
                    startDate = LocalDate.now().plusDays(15),
                    endDate = LocalDate.now().plusDays(45),
                    status = RecruitmentStatus.OPEN,
                    viewCount = viewCount,
                    duration = 30,
                    activityHomepageUrl = null,
                    activityApplicationUrl = null,
                    activityThumbnailUrl = null,
                    activityGroup = activityGroup,
                    activityField = ActivityFieldType.MENTORING,
                    benefit = "테스트 혜택",
                ),
            )

        // 북마크 추가 (인기도 증가)
        repeat(bookmarkCount) { index ->
            val dummyMember =
                memberRepository.save(
                    Member(
                        name = "더미사용자$index",
                        email = "dummy$index@test.com",
                        nickname = "더미$index",
                    ).apply {
                        this.isCompleted = true
                    },
                )
            activityBookmarkRepository.save(ActivityBookmark(dummyMember, activity))
        }

        return activity
    }

    private fun createMember(nickname: String): Member =
        memberRepository.save(
            Member(
                name = "테스트",
                email = "$nickname@test.com",
                nickname = nickname,
            ).apply {
                this.isCompleted = true
            },
        )

    private fun createNotificationPreference(
        member: Member,
        popularEnabled: Boolean,
    ): NotificationPreference =
        notificationPreferenceRepository.save(
            NotificationPreference(
                member = member,
                notifyPopularActivity = popularEnabled,
                notifyBookmarkedActivity = true,
            ),
        )
}
