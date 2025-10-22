package picklab.backend.activity.domain

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageRequest
import picklab.backend.activity.application.ActivityQueryRepository
import picklab.backend.activity.application.model.ActivitySearchCondition
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.enums.*
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.job.domain.enums.JobDetail
import java.time.LocalDate
import java.util.*
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class ActivityServiceUnitTest {
    @MockK
    private lateinit var activityRepository: ActivityRepository

    @MockK
    private lateinit var activityQueryRepository: ActivityQueryRepository

    @InjectMockKs
    private lateinit var activityService: ActivityService

    @Test
    @DisplayName("존재하지 않는 활동을 조회할 경우 BusinessException(ErrorCode.NOT_FOUND_ACTIVITY)이 발생한다")
    fun mustFindId_withNoExistingId_throwBusinessException() {
        // given
        val activityId = 999L
        every { activityRepository.findById(activityId) } returns Optional.empty()

        // when
        val exception =
            assertThrows(BusinessException::class.java) { activityService.mustFindById(activityId) }

        // then
        assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND_ACTIVITY)
        verify(exactly = 1) { activityRepository.findById(activityId) }
    }

    @Nested
    @DisplayName("adjustQueryByCategory 메소드 테스트")
    inner class AdjustQueryByCategoryTest {
        @Test
        @DisplayName("대외활동일 경우 format, costType, award, duration, domain 필드가 null로 보정된다")
        fun adjustQueryByCategory_Extracurricular() {
            // given
            val query =
                ActivitySearchCondition(
                    category = ActivityType.EXTRACURRICULAR,
                    jobTag = listOf(JobDetail.BACKEND),
                    organizer = null,
                    target = null,
                    field = null,
                    location = null,
                    format = listOf(EducationFormatType.ONLINE),
                    costType = listOf(EducationCostType.FREE),
                    award = listOf(100000L),
                    duration = listOf(10L, 30L),
                    domain = listOf(DomainType.WEB),
                    sort = ActivitySortType.LATEST,
                )

            // when
            val result = activityService.adjustQueryByCategory(query)

            // then
            Assertions.assertThat(result.category).isEqualTo(ActivityType.EXTRACURRICULAR)
            Assertions.assertThat(result.format).isNull()
            Assertions.assertThat(result.costType).isNull()
            Assertions.assertThat(result.award).isNull()
            Assertions.assertThat(result.duration).isNull()
            Assertions.assertThat(result.domain).isNull()
        }

        @Test
        @DisplayName("강연/세미나의 경우 field, format, costType, award, duration, domain이 null로 보정된다.")
        fun adjustQueryByCategory_Seminar() {
            // given
            val query =
                ActivitySearchCondition(
                    category = ActivityType.SEMINAR,
                    jobTag = null,
                    organizer = null,
                    target = null,
                    field = listOf(ActivityFieldType.MENTORING),
                    location = null,
                    format = listOf(EducationFormatType.ONLINE),
                    costType = listOf(EducationCostType.FREE),
                    award = listOf(10L, 30L),
                    duration = listOf(30L),
                    domain = listOf(DomainType.WEB),
                    sort = ActivitySortType.LATEST,
                )

            // when
            val result = activityService.adjustQueryByCategory(query)

            // then
            Assertions.assertThat(result.category).isEqualTo(ActivityType.SEMINAR)
            Assertions.assertThat(result.field).isNull()
            Assertions.assertThat(result.format).isNull()
            Assertions.assertThat(result.costType).isNull()
            Assertions.assertThat(result.award).isNull()
            Assertions.assertThat(result.duration).isNull()
            Assertions.assertThat(result.domain).isNull()
        }

        @Test
        @DisplayName("교육활동일 때 format이 ALL 타입이면 ONLINE, OFFLINE 리스트 타입으로 변환한다.")
        fun adjustQueryByCategory_Education_format_convert() {
            // given
            val query =
                ActivitySearchCondition(
                    category = ActivityType.EDUCATION,
                    jobTag = null,
                    organizer = null,
                    target = null,
                    field = null,
                    location = null,
                    format = listOf(EducationFormatType.ALL),
                    costType = null,
                    award = null,
                    duration = null,
                    domain = null,
                    sort = ActivitySortType.LATEST,
                )

            // when
            val result = activityService.adjustQueryByCategory(query)

            // then
            Assertions.assertThat(result.category).isEqualTo(ActivityType.EDUCATION)
            Assertions
                .assertThat(result.format)
                .isEqualTo(listOf(EducationFormatType.ONLINE, EducationFormatType.OFFLINE))
        }

        @Test
        @DisplayName("교육활동일 때 field, award, domain이 null로 보정된다.")
        fun adjustQueryByCategory_Education() {
            // given
            val query =
                ActivitySearchCondition(
                    category = ActivityType.EDUCATION,
                    jobTag = null,
                    organizer = null,
                    target = null,
                    field = listOf(ActivityFieldType.MENTORING),
                    location = null,
                    format = null,
                    costType = null,
                    award = listOf(10L, 30L),
                    duration = null,
                    domain = listOf(DomainType.WEB),
                    sort = ActivitySortType.LATEST,
                )

            // when
            val result = activityService.adjustQueryByCategory(query)

            // then
            Assertions.assertThat(result.category).isEqualTo(ActivityType.EDUCATION)
            Assertions.assertThat(result.field).isNull()
            Assertions.assertThat(result.award).isNull()
            Assertions.assertThat(result.domain).isNull()
        }

        @Test
        @DisplayName("교육활동 일 때 duration이 음수 값을 포함할 경우 null이 된다.")
        fun adjustQueryByCategory_Education_duration_negative() {
            // given
            val query =
                ActivitySearchCondition(
                    category = ActivityType.EDUCATION,
                    jobTag = null,
                    organizer = null,
                    target = null,
                    field = null,
                    location = null,
                    format = null,
                    costType = null,
                    award = null,
                    duration = listOf(-30L, -10L),
                    domain = null,
                    sort = ActivitySortType.LATEST,
                )

            // when
            val result = activityService.adjustQueryByCategory(query)

            // then
            Assertions.assertThat(result.category).isEqualTo(ActivityType.EDUCATION)
            Assertions.assertThat(result.duration).isNull()
        }

        @Test
        @DisplayName("교육활동 일 때 duration이 한 쪽이 음수일 경우 다른 한 쪽만 반환한다.")
        fun adjustQueryByCategory_Education_duration_one_negative() {
            // given
            val query =
                ActivitySearchCondition(
                    category = ActivityType.EDUCATION,
                    jobTag = null,
                    organizer = null,
                    target = null,
                    field = null,
                    location = null,
                    format = null,
                    costType = null,
                    award = null,
                    duration = listOf(-30L, 10L),
                    domain = null,
                    sort = ActivitySortType.LATEST,
                )

            // when
            val result = activityService.adjustQueryByCategory(query)

            // then
            Assertions.assertThat(result.category).isEqualTo(ActivityType.EDUCATION)
            Assertions.assertThat(result.duration).isEqualTo(listOf(10L))
        }

        @Test
        @DisplayName("공모전/해커톤일 때 field, location, format, costType, duration이 null로 보정된다.")
        fun adjustQueryByCategory_Competition() {
            // given
            val query =
                ActivitySearchCondition(
                    category = ActivityType.COMPETITION,
                    jobTag = null,
                    organizer = null,
                    target = null,
                    field = listOf(ActivityFieldType.MENTORING),
                    location = listOf(LocationType.SEOUL_INCHEON),
                    format = listOf(EducationFormatType.ONLINE),
                    costType = listOf(EducationCostType.FREE),
                    award = null,
                    duration = listOf(30L),
                    domain = null,
                    sort = ActivitySortType.LATEST,
                )

            // when
            val result = activityService.adjustQueryByCategory(query)

            // then
            Assertions.assertThat(result.category).isEqualTo(ActivityType.COMPETITION)
            Assertions.assertThat(result.field).isNull()
            Assertions.assertThat(result.location).isNull()
            Assertions.assertThat(result.format).isNull()
            Assertions.assertThat(result.costType).isNull()
            Assertions.assertThat(result.duration).isNull()
        }

        @Test
        @DisplayName("공모전/해커톤일 때 단일 상금 값이 적절한 범위로 보정된다.")
        fun adjustQueryByCategory_Competition_single_award_sanitize() {
            // given
            val query =
                ActivitySearchCondition(
                    category = ActivityType.COMPETITION,
                    jobTag = null,
                    organizer = null,
                    target = null,
                    field = null,
                    location = null,
                    format = null,
                    costType = null,
                    award = listOf(3000000L),
                    duration = null,
                    domain = null,
                    sort = ActivitySortType.LATEST,
                )

            // when
            val result = activityService.adjustQueryByCategory(query)

            // then
            Assertions.assertThat(result.award).isEqualTo(listOf(0L, 5000000L))
        }

        @Test
        @DisplayName("공모전/해커톤일 때 상금 값이 적절한 범위로 보정된다.")
        fun adjustQueryByCategory_Competition_award_sanitize() {
            // given
            val query =
                ActivitySearchCondition(
                    category = ActivityType.COMPETITION,
                    jobTag = null,
                    organizer = null,
                    target = null,
                    field = null,
                    location = null,
                    format = null,
                    costType = null,
                    award = listOf(3000000L, 10000000L),
                    duration = null,
                    domain = null,
                    sort = ActivitySortType.LATEST,
                )

            // when
            val result = activityService.adjustQueryByCategory(query)

            // then
            Assertions.assertThat(result.award).isEqualTo(listOf(0L, 10000000L))
        }
    }

    @Nested
    @DisplayName("getActivityTitlesForAutocomplete 메소드 테스트")
    inner class GetActivityTitlesForAutocompleteTest {
        @Test
        @DisplayName("빈 문자열이 들어올 경우 빈 리스트를 반환한다.")
        fun empty_query_shouldReturnEmptyList() {
            // given
            val keyword = ""

            // when
            val result = activityService.getActivityTitlesForAutocomplete(keyword, 10)

            // then
            Assertions.assertThat(result).isEmpty()
        }

        @Test
        @DisplayName("공백 문자열이 들어올 경우 빈 리스트를 반환한다.")
        fun blank_query_shouldReturnEmptyList() {
            // given
            val keyword = "   "

            // when
            val result = activityService.getActivityTitlesForAutocomplete(keyword, 10)

            // then
            Assertions.assertThat(result).isEmpty()
        }

        @Test
        @DisplayName("limit이 범위를 벗어나도 1~50 사이로 제한된다")
        fun limit_out_of_range_shouldBeAdjusted() {
            // given
            val keyword = "test"
            val expectedSingleTitle = listOf("test1")
            val expectedTitles = listOf("test2", "test3", "test4")

            every {
                activityRepository.findActivityTitlesForAutocomplete(keyword, 1)
            } returns expectedSingleTitle
            every {
                activityRepository.findActivityTitlesForAutocomplete(keyword, 50)
            } returns expectedTitles

            // when
            val resultUnder = activityService.getActivityTitlesForAutocomplete(keyword, 0)
            val resultOver = activityService.getActivityTitlesForAutocomplete(keyword, 100)

            // then
            Assertions.assertThat(resultUnder).isEqualTo(expectedSingleTitle)
            verify(exactly = 1) { activityRepository.findActivityTitlesForAutocomplete(keyword, 1) }

            Assertions.assertThat(resultOver).isEqualTo(expectedTitles)
            verify(exactly = 1) { activityRepository.findActivityTitlesForAutocomplete(keyword, 50) }
        }
    }

    @Nested
    @DisplayName("getActivitiesEndingOnDate 메소드 테스트")
    inner class GetActivitiesEndingOnDateTest {
        @Test
        @DisplayName("특정 마감일에 해당하는 모집 중인 활동들을 조회한다")
        fun getActivitiesEndingOnDate_success() {
            // given
            val targetDate = LocalDate.of(2025, 10, 15)
            val expectedActivities = listOf(createMockActivity(1L), createMockActivity(2L))

            every {
                activityRepository.findByRecruitmentEndDateAndStatus(targetDate, RecruitmentStatus.OPEN)
            } returns expectedActivities

            // when
            val result = activityService.getActivitiesEndingOnDate(targetDate)

            // then
            Assertions.assertThat(result).isEqualTo(expectedActivities)
            verify(exactly = 1) {
                activityRepository.findByRecruitmentEndDateAndStatus(targetDate, RecruitmentStatus.OPEN)
            }
        }

        @Test
        @DisplayName("해당 마감일에 모집 중인 활동이 없으면 빈 리스트를 반환한다")
        fun getActivitiesEndingOnDate_noActivities() {
            // given
            val targetDate = LocalDate.of(2025, 10, 15)

            every {
                activityRepository.findByRecruitmentEndDateAndStatus(targetDate, RecruitmentStatus.OPEN)
            } returns emptyList()

            // when
            val result = activityService.getActivitiesEndingOnDate(targetDate)

            // then
            Assertions.assertThat(result).isEmpty()
            verify(exactly = 1) {
                activityRepository.findByRecruitmentEndDateAndStatus(targetDate, RecruitmentStatus.OPEN)
            }
        }
    }

    @Nested
    @DisplayName("getActivitiesEndingInDays 메소드 테스트")
    inner class GetActivitiesEndingInDaysTest {
        @Test
        @DisplayName("기준 날짜로부터 N일 후의 마감 활동들을 조회한다")
        fun getActivitiesEndingInDays_success() {
            // given
            val baseDate = LocalDate.of(2025, 10, 1)
            val daysUntilDeadline = 7
            val expectedDate = LocalDate.of(2025, 10, 8)
            val expectedActivities = listOf(createMockActivity(1L))

            every {
                activityRepository.findByRecruitmentEndDateAndStatus(expectedDate, RecruitmentStatus.OPEN)
            } returns expectedActivities

            // when
            val result = activityService.getActivitiesEndingInDays(baseDate, daysUntilDeadline)

            // then
            Assertions.assertThat(result).isEqualTo(expectedActivities)
            verify(exactly = 1) {
                activityRepository.findByRecruitmentEndDateAndStatus(expectedDate, RecruitmentStatus.OPEN)
            }
        }

        @Test
        @DisplayName("0일 후(당일) 마감 활동들을 조회한다")
        fun getActivitiesEndingInDays_sameDay() {
            // given
            val baseDate = LocalDate.of(2025, 10, 1)
            val daysUntilDeadline = 0
            val expectedActivities = listOf(createMockActivity(1L))

            every {
                activityRepository.findByRecruitmentEndDateAndStatus(baseDate, RecruitmentStatus.OPEN)
            } returns expectedActivities

            // when
            val result = activityService.getActivitiesEndingInDays(baseDate, daysUntilDeadline)

            // then
            Assertions.assertThat(result).isEqualTo(expectedActivities)
            verify(exactly = 1) {
                activityRepository.findByRecruitmentEndDateAndStatus(baseDate, RecruitmentStatus.OPEN)
            }
        }
    }

    @Nested
    @DisplayName("getMostPopularActivity 메소드 테스트")
    inner class GetMostPopularActivityTest {
        @Test
        @DisplayName("가장 인기 있는 활동을 반환한다")
        fun getMostPopularActivity_success() {
            // given
            val expectedActivity = createMockActivity(1L)

            every { activityRepository.findMostPopularActivity() } returns expectedActivity

            // when
            val result = activityService.getMostPopularActivity()

            // then
            Assertions.assertThat(result).isEqualTo(expectedActivity)
            verify(exactly = 1) { activityRepository.findMostPopularActivity() }
        }

        @Test
        @DisplayName("인기 활동이 없으면 null을 반환한다")
        fun getMostPopularActivity_noActivity() {
            // given
            every { activityRepository.findMostPopularActivity() } returns null

            // when
            val result = activityService.getMostPopularActivity()

            // then
            Assertions.assertThat(result).isNull()
            verify(exactly = 1) { activityRepository.findMostPopularActivity() }
        }
    }

    @Nested
    @DisplayName("getPopularActivities 메소드 테스트")
    inner class GetPopularActivitiesTest {
        @Test
        @DisplayName("인기 활동들을 페이징하여 반환한다")
        fun getPopularActivities_success() {
            // given
            val pageable = PageRequest.of(0, 10)
            val mockPage = mockk<org.springframework.data.domain.Page<picklab.backend.activity.application.model.ActivityView>>()

            every { activityQueryRepository.findPopularActivities(pageable) } returns mockPage

            // when
            val result = activityService.getPopularActivities(pageable)

            // then
            Assertions.assertThat(result).isEqualTo(mockPage)
            verify(exactly = 1) { activityQueryRepository.findPopularActivities(pageable) }
        }
    }

    // Mock 객체 생성 헬퍼 메소드
    private fun createMockActivity(id: Long): Activity =
        mockk<Activity>(relaxed = true) {
            every { this@mockk.id } returns id
        }
}
