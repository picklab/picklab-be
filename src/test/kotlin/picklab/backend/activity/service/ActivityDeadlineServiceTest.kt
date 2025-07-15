package picklab.backend.activity.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import picklab.backend.activity.domain.entity.ActivityGroup
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.repository.ActivityGroupRepository
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.helper.CleanUp
import picklab.backend.job.template.IntegrationTest
import java.time.LocalDate
import java.time.ZoneId

@DisplayName("활동 마감일 서비스 테스트")
class ActivityDeadlineServiceTest : IntegrationTest() {

    @Autowired
    private lateinit var cleanUp: CleanUp

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
        activityGroup = activityGroupRepository.save(
            ActivityGroup(
                name = "테스트 그룹",
                description = "테스트 그룹 설명"
            )
        )
    }

    @Test
    @DisplayName("설정된 시간대 기준 현재 날짜를 정확히 반환한다")
    fun `getCurrentDateInKST_should_return_current_date_in_configured_timezone`() {
        // When
        val currentDate = activityService.getCurrentDateInKST()
        val expectedDate = LocalDate.now(ZoneId.of("Asia/Seoul")) // 기본 설정값 사용

        // Then
        assertThat(currentDate).isEqualTo(expectedDate)
    }



    @Test
    @DisplayName("마감된 활동은 조회되지 않는다")
    fun `getActivitiesEndingInDays_should_not_return_closed_activities`() {
        // Given
        val threeDaysLater = activityService.getCurrentDateInKST().plusDays(3)
        val openActivity = createTestActivity("모집 중 활동", threeDaysLater, RecruitmentStatus.OPEN)
        val closedActivity = createTestActivity("마감된 활동", threeDaysLater, RecruitmentStatus.CLOSED)

        activityRepository.saveAll(listOf(openActivity, closedActivity))

        // When
        val result = activityService.getActivitiesEndingInDays(3)

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].title).isEqualTo("모집 중 활동")
        assertThat(result[0].status).isEqualTo(RecruitmentStatus.OPEN)
    }

    @Test
    @DisplayName("특정 일수 후의 마감일에 해당하는 활동들을 조회한다")
    fun `getActivitiesEndingInDays_should_return_activities_for_specific_days`() {
        // Given
        val currentDate = activityService.getCurrentDateInKST()
        val activity5Days = createTestActivity("5일 후 마감", currentDate.plusDays(5))
        val activity7Days = createTestActivity("7일 후 마감", currentDate.plusDays(7))
        val activity10Days = createTestActivity("10일 후 마감", currentDate.plusDays(10))

        activityRepository.saveAll(listOf(activity5Days, activity7Days, activity10Days))

        // When
        val result5Days = activityService.getActivitiesEndingInDays(5)
        val result7Days = activityService.getActivitiesEndingInDays(7)
        val result10Days = activityService.getActivitiesEndingInDays(10)

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
        val currentDate = activityService.getCurrentDateInKST()
        val testDays = listOf(2, 4, 6, 8) // 임의의 일수들
        val activities = testDays.map { days ->
            createTestActivity("${days}일 후 마감", currentDate.plusDays(days.toLong()))
        }

        activityRepository.saveAll(activities)

        // When & Then
        testDays.forEach { days ->
            val result = activityService.getActivitiesEndingInDays(days)
            
            assertThat(result).hasSize(1)
            assertThat(result[0].title).isEqualTo("${days}일 후 마감")
            assertThat(result[0].recruitmentEndDate).isEqualTo(currentDate.plusDays(days.toLong()))
        }
    }

    private fun createTestActivity(
        title: String,
        recruitmentEndDate: LocalDate,
        status: RecruitmentStatus = RecruitmentStatus.OPEN
    ): ExternalActivity {
        return ExternalActivity(
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
            activityThumbnailUrl = null,
            activityGroup = activityGroup,
            activityField = ActivityFieldType.MENTORING,
            benefit = "테스트 혜택"
        )
    }
} 