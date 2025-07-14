package picklab.backend.activity.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import picklab.backend.activity.application.model.ActivitySearchCommand
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.ActivitySortType
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.enums.DomainType
import picklab.backend.activity.domain.enums.EducationCostType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.notification.domain.config.NotificationDeadlineProperties
import kotlin.test.Test

@DisplayName("활동 분야에 따른 쿼리 보정 로직 테스트")
@ExtendWith(MockitoExtension::class)
class ActivityQuerySanitizeTest {
    @InjectMocks
    private lateinit var activityService: ActivityService

    @Mock
    private lateinit var activityRepository: ActivityRepository
    @Mock
    private lateinit var notificationDeadlineProperties: NotificationDeadlineProperties

    @Test
    fun `대외활동 활동의 경우 온,오프라인 여부, 비용, 상금, 기간, 도메인이 null로 보정된다`() {
        // given
        val query =
            ActivitySearchCommand(
                category = ActivityType.EXTRACURRICULAR,
                jobTag = listOf(JobDetail.BACKEND),
                organizer = listOf(OrganizerType.LARGE_CORPORATION),
                target = listOf(ParticipantType.ALL),
                field = listOf(ActivityFieldType.MENTORING),
                location = listOf(LocationType.SEOUL_INCHEON),
                format = listOf(EducationFormatType.ONLINE),
                costType = listOf(EducationCostType.FREE),
                award = listOf(1000),
                duration = listOf(10, 30),
                domain = listOf(DomainType.AI),
                sort = ActivitySortType.LATEST,
            )

        // when
        val queryData = activityService.adjustQueryByCategory(query)

        // then
        assertThat(queryData.format).isNull()
        assertThat(queryData.costType).isNull()
        assertThat(queryData.award).isNull()
        assertThat(queryData.duration).isNull()
        assertThat(queryData.domain).isNull()
    }

    @Test
    fun `세미나 활동의 경우 분야, 온,오프라인 여부, 비용, 상금, 기간, 도메인이 null로 보정된다`() {
        // given
        val query =
            ActivitySearchCommand(
                category = ActivityType.SEMINAR,
                jobTag = listOf(JobDetail.BACKEND),
                organizer = listOf(OrganizerType.LARGE_CORPORATION),
                target = listOf(ParticipantType.ALL),
                field = listOf(ActivityFieldType.MENTORING),
                location = listOf(LocationType.SEOUL_INCHEON),
                format = listOf(EducationFormatType.ONLINE),
                costType = listOf(EducationCostType.FREE),
                award = listOf(1000),
                duration = listOf(10, 30),
                domain = listOf(DomainType.AI),
                sort = ActivitySortType.LATEST,
            )

        // when
        val queryData = activityService.adjustQueryByCategory(query)

        // then
        assertThat(queryData.field).isNull()
        assertThat(queryData.format).isNull()
        assertThat(queryData.costType).isNull()
        assertThat(queryData.award).isNull()
        assertThat(queryData.duration).isNull()
        assertThat(queryData.domain).isNull()
    }

    @Nested
    @DisplayName("교육 활동의 경우")
    inner class EducationCategoryTest {
        @Test
        fun `분야, 상금, 도메인이 null로 보정된다`() {
            // given
            val query =
                ActivitySearchCommand(
                    category = ActivityType.EDUCATION,
                    jobTag = listOf(JobDetail.BACKEND),
                    organizer = listOf(OrganizerType.LARGE_CORPORATION),
                    target = listOf(ParticipantType.ALL),
                    field = listOf(ActivityFieldType.MENTORING),
                    location = listOf(LocationType.SEOUL_INCHEON),
                    format = listOf(EducationFormatType.ONLINE),
                    costType = listOf(EducationCostType.FREE),
                    award = listOf(1000),
                    duration = listOf(10, 30),
                    domain = listOf(DomainType.AI),
                    sort = ActivitySortType.LATEST,
                )

            // when
            val queryData = activityService.adjustQueryByCategory(query)

            // then
            assertThat(queryData.field).isNull()
            assertThat(queryData.award).isNull()
            assertThat(queryData.domain).isNull()
        }

        @Test
        fun `요청 기간이 0보다 작을 경우 처리되지 않는다`() {
            // given
            val query =
                ActivitySearchCommand(
                    category = ActivityType.EDUCATION,
                    jobTag = listOf(JobDetail.BACKEND),
                    organizer = listOf(OrganizerType.LARGE_CORPORATION),
                    target = listOf(ParticipantType.ALL),
                    field = listOf(ActivityFieldType.MENTORING),
                    location = listOf(LocationType.SEOUL_INCHEON),
                    format = listOf(EducationFormatType.ONLINE),
                    costType = listOf(EducationCostType.FREE),
                    award = listOf(100, 1000),
                    duration = listOf(-1, 30),
                    domain = listOf(DomainType.AI),
                    sort = ActivitySortType.LATEST,
                )

            // when
            val queryData = activityService.adjustQueryByCategory(query)

            // then
            assertThat(queryData.duration).size().isEqualTo(1)
            assertThat(queryData.duration!![0]).isEqualTo(30)
        }

        @Nested
        @DisplayName("공모전,해커톤 활동의 경우")
        inner class CompetitionCategoryTest {
            @Test
            fun `분야, 지역, 온,오프라인 여부, 비용타입 이 null로 보정된다`() {
                // given
                val query =
                    ActivitySearchCommand(
                        category = ActivityType.COMPETITION,
                        jobTag = listOf(JobDetail.BACKEND),
                        organizer = listOf(OrganizerType.LARGE_CORPORATION),
                        target = listOf(ParticipantType.ALL),
                        field = listOf(ActivityFieldType.MENTORING),
                        location = listOf(LocationType.SEOUL_INCHEON),
                        format = listOf(EducationFormatType.ONLINE),
                        costType = listOf(EducationCostType.FREE),
                        award = listOf(1000),
                        duration = listOf(10, 30),
                        domain = listOf(DomainType.AI),
                        sort = ActivitySortType.LATEST,
                    )

                // when
                val queryData = activityService.adjustQueryByCategory(query)

                // then
                assertThat(queryData.field).isNull()
                assertThat(queryData.location).isNull()
                assertThat(queryData.format).isNull()
                assertThat(queryData.costType).isNull()
            }

            @Test
            fun `최소 상금이 0보다 작을 경우 0으로 보정된다`() {
                // given
                val query =
                    ActivitySearchCommand(
                        category = ActivityType.COMPETITION,
                        jobTag = listOf(JobDetail.BACKEND),
                        organizer = listOf(OrganizerType.LARGE_CORPORATION),
                        target = listOf(ParticipantType.ALL),
                        field = listOf(ActivityFieldType.MENTORING),
                        location = listOf(LocationType.SEOUL_INCHEON),
                        format = listOf(EducationFormatType.ONLINE),
                        costType = listOf(EducationCostType.FREE),
                        award = listOf(-100, 1000),
                        duration = listOf(10, 30),
                        domain = listOf(DomainType.AI),
                        sort = ActivitySortType.LATEST,
                    )

                // when
                val queryData = activityService.adjustQueryByCategory(query)

                // then
                assertThat(queryData.award).isNotNull
                assertThat(queryData.award).size().isEqualTo(2)
                assertThat(queryData.award!![0]).isEqualTo(0)
                assertThat(queryData.award!![1]).isEqualTo(5000000)
            }

            @Test
            fun `상금의 범위가 조작되더라도 요구한 범위로 보정된다`() {
                // given
                val query =
                    ActivitySearchCommand(
                        category = ActivityType.COMPETITION,
                        jobTag = listOf(JobDetail.BACKEND),
                        organizer = listOf(OrganizerType.LARGE_CORPORATION),
                        target = listOf(ParticipantType.ALL),
                        field = listOf(ActivityFieldType.MENTORING),
                        location = listOf(LocationType.SEOUL_INCHEON),
                        format = listOf(EducationFormatType.ONLINE),
                        costType = listOf(EducationCostType.FREE),
                        award = listOf(100, 50),
                        duration = listOf(10, 30),
                        domain = listOf(DomainType.AI),
                        sort = ActivitySortType.LATEST,
                    )

                // when
                val queryData = activityService.adjustQueryByCategory(query)

                // then
                assertThat(queryData.award).isNotNull
                assertThat(queryData.award).size().isEqualTo(2)
                assertThat(queryData.award!![0]).isEqualTo(0)
                assertThat(queryData.award!![1]).isEqualTo(5000000)
            }
        }
    }
}
