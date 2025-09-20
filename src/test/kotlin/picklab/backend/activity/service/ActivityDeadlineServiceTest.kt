package picklab.backend.activity.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import picklab.backend.activity.domain.entity.ActivityGroup
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.domain.repository.ActivityGroupRepository
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.template.IntegrationTest
import java.time.LocalDate

@DisplayName("활동 마감일 서비스 테스트")
class ActivityDeadlineServiceTest : IntegrationTest() {
    @Autowired
    private lateinit var activityService: ActivityService

    @Autowired
    private lateinit var activityRepository: ActivityRepository

    @Autowired
    private lateinit var activityGroupRepository: ActivityGroupRepository

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
    @DisplayName("특정 마감일에 해당하는 활동들을 조회한다")
    fun `getActivitiesEndingOnDate_should_return_activities_ending_on_specific_date`() {
        // Given
        val targetDate = LocalDate.now().plusDays(5)
        val activity1 = createTestActivity("마감일 활동 1", targetDate)
        val activity2 = createTestActivity("마감일 활동 2", targetDate)
        val activity3 = createTestActivity("다른 날 활동", targetDate.plusDays(1))

        activityRepository.saveAll(listOf(activity1, activity2, activity3))

        // When
        val result = activityService.getActivitiesEndingOnDate(targetDate)

        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.title }).containsExactlyInAnyOrder(
            "마감일 활동 1",
            "마감일 활동 2",
        )
    }

    @Test
    @DisplayName("마감된 활동은 조회되지 않는다")
    fun `getActivitiesEndingInDays_should_not_return_closed_activities`() {
        // Given
        val baseDate = LocalDate.now()
        val threeDaysLater = baseDate.plusDays(3)
        val openActivity = createTestActivity("모집 중 활동", threeDaysLater, RecruitmentStatus.OPEN)
        val closedActivity = createTestActivity("마감된 활동", threeDaysLater, RecruitmentStatus.CLOSED)

        activityRepository.saveAll(listOf(openActivity, closedActivity))

        // When
        val result = activityService.getActivitiesEndingInDays(baseDate, 3)

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].title).isEqualTo("모집 중 활동")
        assertThat(result[0].status).isEqualTo(RecruitmentStatus.OPEN)
    }

    @Test
    @DisplayName("기준 날짜로부터 특정 일수 후의 마감일에 해당하는 활동들을 조회한다")
    fun `getActivitiesEndingInDays_should_return_activities_for_specific_days`() {
        // Given
        val baseDate = LocalDate.now()
        val activity5Days = createTestActivity("5일 후 마감", baseDate.plusDays(5))
        val activity7Days = createTestActivity("7일 후 마감", baseDate.plusDays(7))
        val activity10Days = createTestActivity("10일 후 마감", baseDate.plusDays(10))

        activityRepository.saveAll(listOf(activity5Days, activity7Days, activity10Days))

        // When
        val result5Days = activityService.getActivitiesEndingInDays(baseDate, 5)
        val result7Days = activityService.getActivitiesEndingInDays(baseDate, 7)
        val result10Days = activityService.getActivitiesEndingInDays(baseDate, 10)

        // Then
        assertThat(result5Days).hasSize(1)
        assertThat(result5Days[0].title).isEqualTo("5일 후 마감")

        assertThat(result7Days).hasSize(1)
        assertThat(result7Days[0].title).isEqualTo("7일 후 마감")

        assertThat(result10Days).hasSize(1)
        assertThat(result10Days[0].title).isEqualTo("10일 후 마감")
    }

    @Test
    @DisplayName("동적으로 설정된 advance-days 값으로 활동을 조회한다")
    fun `getActivitiesEndingInDays_should_work_dynamically_with_any_days`() {
        // Given
        val baseDate = LocalDate.now()
        val testDays = listOf(2, 4, 6, 8) // 임의의 일수들
        val activities =
            testDays.map { days ->
                createTestActivity("${days}일 후 마감", baseDate.plusDays(days.toLong()))
            }

        activityRepository.saveAll(activities)

        // When & Then
        testDays.forEach { days ->
            val result = activityService.getActivitiesEndingInDays(baseDate, days)

            assertThat(result).hasSize(1)
            assertThat(result[0].title).isEqualTo("${days}일 후 마감")
            assertThat(result[0].recruitmentEndDate).isEqualTo(baseDate.plusDays(days.toLong()))
        }
    }

    private fun createTestActivity(
        title: String,
        recruitmentEndDate: LocalDate,
        status: RecruitmentStatus = RecruitmentStatus.OPEN,
    ): ExternalActivity =
        ExternalActivity(
            title = title,
            organizer = OrganizerType.PUBLIC_ORGANIZATION,
            targetAudience = ParticipantType.ALL,
            location = LocationType.SEOUL_INCHEON,
            recruitmentStartDate = recruitmentEndDate.minusDays(30),
            recruitmentEndDate = recruitmentEndDate,
            startDate = recruitmentEndDate.plusDays(7),
            endDate = recruitmentEndDate.plusDays(37),
            status = status,
            viewCount = 0L,
            duration = 30,
            activityHomepageUrl = null,
            activityApplicationUrl = null,
            activityThumbnailUrl = null,
            activityGroup = activityGroup,
            activityField = ActivityFieldType.MENTORING,
            benefit = "테스트 혜택",
        )
}
