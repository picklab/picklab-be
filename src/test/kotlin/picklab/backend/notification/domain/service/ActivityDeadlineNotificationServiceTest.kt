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
import picklab.backend.notification.domain.repository.NotificationRepository
import picklab.backend.template.IntegrationTest
import java.time.LocalDate

class ActivityDeadlineNotificationServiceTest : IntegrationTest() {
    @Autowired
    lateinit var activityDeadlineNotificationService: ActivityDeadlineNotificationService

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Autowired
    lateinit var activityGroupRepository: ActivityGroupRepository

    @Autowired
    lateinit var bookmarkRepository: ActivityBookmarkRepository

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
    @DisplayName("북마크한 사용자만 마감일 알림을 받는다")
    fun `should send deadline notification only to bookmarked users`() {
        // given
        val activity = createActivity(LocalDate.now().plusDays(1)) // 내일 마감
        val bookmarkedUser = createMember("북마크한사용자")
        val nonBookmarkedUser = createMember("북마크안한사용자")

        // 두 사용자 모두 알림 설정은 ON
        createNotificationPreference(bookmarkedUser, bookmarkEnabled = true)
        createNotificationPreference(nonBookmarkedUser, bookmarkEnabled = true)

        // 한 명만 북마크
        bookmarkRepository.save(ActivityBookmark(bookmarkedUser, activity))

        // when
        val result =
            activityDeadlineNotificationService.sendDeadlineNotificationsForDays(
                LocalDate.now(),
                1,
            )

        // then
        assertThat(result).isEqualTo(1) // 1건 전송

        val notifications = notificationRepository.findAll()
        assertThat(notifications).hasSize(1)
        assertThat(notifications[0].member.id).isEqualTo(bookmarkedUser.id)
        assertThat(notifications[0].title).contains("내일 마감!")
        assertThat(notifications[0].link).isEqualTo("/activities/${activity.id}")
    }

    @Test
    @DisplayName("북마크했지만 알림 설정이 OFF면 알림을 받지 않는다")
    fun `should not send notification when bookmark notification is disabled`() {
        // given
        val activity = createActivity(LocalDate.now().plusDays(3)) // 3일 후 마감
        val userWithNotificationOff = createMember("알림끈사용자")
        val userWithNotificationOn = createMember("알림켠사용자")

        // 알림 설정 - 한 명은 OFF, 한 명은 ON
        createNotificationPreference(userWithNotificationOff, bookmarkEnabled = false)
        createNotificationPreference(userWithNotificationOn, bookmarkEnabled = true)

        // 둘 다 북마크
        bookmarkRepository.save(ActivityBookmark(userWithNotificationOff, activity))
        bookmarkRepository.save(ActivityBookmark(userWithNotificationOn, activity))

        // when
        val result =
            activityDeadlineNotificationService.sendDeadlineNotificationsForDays(
                LocalDate.now(),
                3,
            )

        // then
        assertThat(result).isEqualTo(1) // 1건만 전송 (알림 켠 사용자만)

        val notifications = notificationRepository.findAll()
        assertThat(notifications).hasSize(1)
        assertThat(notifications[0].member.id).isEqualTo(userWithNotificationOn.id)
        assertThat(notifications[0].title).contains("3일 후 마감!")
    }

    @Test
    @DisplayName("북마크한 사용자가 없으면 알림을 전송하지 않는다")
    fun `should not send notification when no users bookmarked`() {
        // given
        createActivity(LocalDate.now().plusDays(1))
        val user = createMember("사용자")
        createNotificationPreference(user, bookmarkEnabled = true)
        // 북마크하지 않음

        // when
        val result =
            activityDeadlineNotificationService.sendDeadlineNotificationsForDays(
                LocalDate.now(),
                1,
            )

        // then
        assertThat(result).isEqualTo(0)
        assertThat(notificationRepository.findAll()).isEmpty()
    }

    @Test
    @DisplayName("모든 북마크 사용자가 알림 설정을 OFF하면 알림을 전송하지 않는다")
    fun `should not send notification when all bookmarked users disabled notification`() {
        // given
        val activity = createActivity(LocalDate.now().plusDays(1))
        val user1 = createMember("사용자1")
        val user2 = createMember("사용자2")

        // 모든 사용자 알림 설정 OFF
        createNotificationPreference(user1, bookmarkEnabled = false)
        createNotificationPreference(user2, bookmarkEnabled = false)

        // 모두 북마크
        bookmarkRepository.save(ActivityBookmark(user1, activity))
        bookmarkRepository.save(ActivityBookmark(user2, activity))

        // when
        val result =
            activityDeadlineNotificationService.sendDeadlineNotificationsForDays(
                LocalDate.now(),
                1,
            )

        // then
        assertThat(result).isEqualTo(0)
        assertThat(notificationRepository.findAll()).isEmpty()
    }

    private fun createActivity(recruitmentEndDate: LocalDate): Activity =
        activityRepository.save(
            ExternalActivity(
                title = "테스트 활동",
                organizer = OrganizerType.PUBLIC_ORGANIZATION,
                targetAudience = ParticipantType.ALL,
                location = LocationType.SEOUL_INCHEON,
                recruitmentStartDate = recruitmentEndDate.minusDays(30),
                recruitmentEndDate = recruitmentEndDate,
                startDate = recruitmentEndDate.plusDays(7),
                endDate = recruitmentEndDate.plusDays(37),
                status = RecruitmentStatus.OPEN,
                viewCount = 0L,
                duration = 30,
                activityHomepageUrl = null,
                activityApplicationUrl = null,
                activityThumbnailUrl = null,
                activityGroup = activityGroup,
                activityField = ActivityFieldType.MENTORING,
                benefit = "테스트 혜택",
            ),
        )

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
        bookmarkEnabled: Boolean,
    ): NotificationPreference =
        notificationPreferenceRepository.save(
            NotificationPreference(
                member = member,
                notifyPopularActivity = true,
                notifyBookmarkedActivity = bookmarkEnabled,
            ),
        )
}
