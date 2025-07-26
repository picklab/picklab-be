package picklab.backend.activity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import picklab.backend.activity.application.ViewCountLimiterPort.Companion.MAX_VIEW_ATTEMPTS
import picklab.backend.activity.domain.entity.ActivityBookmark
import picklab.backend.activity.domain.entity.ActivityGroup
import picklab.backend.activity.domain.entity.ActivityJobCategory
import picklab.backend.activity.domain.entity.CompetitionActivity
import picklab.backend.activity.domain.entity.EducationActivity
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.enums.DomainType
import picklab.backend.activity.domain.enums.EducationCostType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.domain.repository.ActivityBookmarkRepository
import picklab.backend.activity.domain.repository.ActivityGroupRepository
import picklab.backend.activity.domain.repository.ActivityJobCategoryRepository
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.activity.entrypoint.response.GetActivityDetailResponse
import picklab.backend.activity.entrypoint.response.GetActivityListResponse
import picklab.backend.common.model.ResponseWrapper
import picklab.backend.common.model.SuccessCode
import picklab.backend.helper.WithMockUser
import picklab.backend.helper.extractBody
import picklab.backend.job.domain.JobCategoryRepository
import picklab.backend.job.domain.entity.JobCategory
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.member.domain.entity.InterestedJobCategory
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.repository.InterestedJobCategoryRepository
import picklab.backend.member.domain.repository.MemberRepository
import picklab.backend.template.IntegrationTest
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ActivityIntegrationTest : IntegrationTest() {
    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Autowired
    lateinit var activityGroupRepository: ActivityGroupRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var activityBookmarkRepository: ActivityBookmarkRepository

    @Autowired
    lateinit var activityJobCategoryRepository: ActivityJobCategoryRepository

    @Autowired
    lateinit var jobCategoryRepository: JobCategoryRepository

    @Autowired
    lateinit var interestedJobCategoryRepository: InterestedJobCategoryRepository

    @Autowired
    lateinit var cacheManager: CacheManager

    lateinit var activityGroup: ActivityGroup

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

    @Nested
    @DisplayName("활동 조회 통합 테스트")
    inner class GetActivityListTest {
        @Nested
        @DisplayName("활동 리스트를 조회한다.")
        inner class GetExternalActivityListTest {
            @Test
            @DisplayName("[성공] 대외활동 목록을 조회한다.")
            fun `대외활동 목록을 조회한다`() {
                // Given
                val given1 =
                    activityRepository.save(
                        ExternalActivity(
                            title = "테스트 대외활동",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                val given2 =
                    activityRepository.save(
                        ExternalActivity(
                            title = "테스트 대외활동",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "extracurricular")
                            param("sort", "latest")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(20)
                assertThat(got.totalElements).isEqualTo(2)
                assertThat(got.totalPages).isEqualTo(1)
                assertThat(items[0].id).isEqualTo(given1.id)
                assertThat(items[1].id).isEqualTo(given2.id)
            }

            @Test
            @DisplayName("[성공] 대외활동 목록을 조회할 때 주최기관 필터링 조건을 적용한다.")
            fun `주최기관 필터링 조건을 적용한다`() {
                val given1 =
                    activityRepository.save(
                        ExternalActivity(
                            title = "테스트 대외활동1",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                activityRepository.save(
                    ExternalActivity(
                        title = "테스트 대외활동2",
                        organizer = OrganizerType.LARGE_CORPORATION,
                        targetAudience = ParticipantType.WORKER,
                        location = LocationType.SEOUL_INCHEON,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusMonths(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        activityField = ActivityFieldType.MENTORING,
                        benefit = "",
                    ),
                )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "extracurricular")
                            param("organizer", "public_organization")
                            param("sort", "latest")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(20)
                assertThat(got.totalElements).isEqualTo(1)
                assertThat(got.totalPages).isEqualTo(1)
                assertThat(items[0].id).isEqualTo(given1.id)
            }

            @Test
            @DisplayName("[성공] 대외활동 목록을 조회할 때 참여 대상 필터링 조건을 적용한다.")
            fun `참여 대상 필터를 조건을 적용한다`() {
                // Given
                val given1 =
                    activityRepository.save(
                        ExternalActivity(
                            title = "테스트 대외활동",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                activityRepository.save(
                    ExternalActivity(
                        title = "테스트 대외활동2",
                        organizer = OrganizerType.PUBLIC_ORGANIZATION,
                        targetAudience = ParticipantType.UNIVERSITY_STUDENT,
                        location = LocationType.SEOUL_INCHEON,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusMonths(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        activityField = ActivityFieldType.MENTORING,
                        benefit = "",
                    ),
                )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "extracurricular")
                            param("target", "worker")
                            param("sort", "latest")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(20)
                assertThat(got.totalElements).isEqualTo(1)
                assertThat(got.totalPages).isEqualTo(1)
                assertThat(items[0].id).isEqualTo(given1.id)
            }

            @Test
            @DisplayName("[성공] 대외활동 목록을 조회할 때 지역 필터링 조건을 적용한다.")
            fun `지역 필터를 조건을 적용한다`() {
                // Given
                val given1 =
                    activityRepository.save(
                        ExternalActivity(
                            title = "테스트 대외활동",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                activityRepository.save(
                    ExternalActivity(
                        title = "테스트 대외활동2",
                        organizer = OrganizerType.PUBLIC_ORGANIZATION,
                        targetAudience = ParticipantType.WORKER,
                        location = LocationType.BUSAN_DAEGU_GYEONGSANG,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusMonths(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        activityField = ActivityFieldType.MENTORING,
                        benefit = "",
                    ),
                )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "extracurricular")
                            param("location", "seoul_incheon")
                            param("sort", "latest")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(20)
                assertThat(got.totalElements).isEqualTo(1)
                assertThat(got.totalPages).isEqualTo(1)
                assertThat(items[0].id).isEqualTo(given1.id)
            }

            @Test
            @DisplayName("[성공] 대외활동 목록을 조회할 때 활동 분야 필터링 조건을 적용한다.")
            fun `활동 분야 필터를 조건을 적용한다`() {
                // Given
                val given1 =
                    activityRepository.save(
                        ExternalActivity(
                            title = "테스트 대외활동",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                activityRepository.save(
                    ExternalActivity(
                        title = "테스트 대외활동2",
                        organizer = OrganizerType.PUBLIC_ORGANIZATION,
                        targetAudience = ParticipantType.WORKER,
                        location = LocationType.SEOUL_INCHEON,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusMonths(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        activityField = ActivityFieldType.DOMESTIC_VOLUNTEER,
                        benefit = "",
                    ),
                )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "extracurricular")
                            param("field", "mentoring")
                            param("sort", "latest")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(20)
                assertThat(got.totalElements).isEqualTo(1)
                assertThat(got.totalPages).isEqualTo(1)
                assertThat(items[0].id).isEqualTo(given1.id)
            }

            @Nested
            @DisplayName("[성공] 교육활동 목록을 조회할 때 온/오프라인 여부 필터링 조건을 적용한다.")
            inner class EducationFormatTest {
                @Test
                @DisplayName("[성공] 온/오프라인 여부 필터링 조건을 적용한다.")
                fun `온,오프라인 여부 필터링 조건을 적용한다`() {
                    // Given
                    val given1 =
                        activityRepository.save(
                            EducationActivity(
                                title = "테스트 교육활동",
                                organizer = OrganizerType.PUBLIC_ORGANIZATION,
                                targetAudience = ParticipantType.WORKER,
                                location = LocationType.SEOUL_INCHEON,
                                recruitmentStartDate = LocalDate.now().plusDays(1),
                                recruitmentEndDate = LocalDate.now().plusMonths(1),
                                startDate = LocalDate.now().plusMonths(3),
                                endDate = LocalDate.now().plusMonths(6),
                                status = RecruitmentStatus.OPEN,
                                viewCount = 0L,
                                duration =
                                    ChronoUnit.DAYS
                                        .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                        .toInt(),
                                activityHomepageUrl = null,
                                activityApplicationUrl = null,
                                activityThumbnailUrl = null,
                                activityGroup = activityGroup,
                                cost = 0L,
                                costType = EducationCostType.FULLY_GOVERNMENT,
                                format = EducationFormatType.ONLINE,
                                benefit = "",
                            ),
                        )

                    activityRepository.save(
                        EducationActivity(
                            title = "테스트 교육활동",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            cost = 0L,
                            costType = EducationCostType.FULLY_GOVERNMENT,
                            format = EducationFormatType.OFFLINE,
                            benefit = "",
                        ),
                    )

                    // When
                    val result =
                        mockMvc
                            .get("/v1/activities") {
                                param("category", "education")
                                param("format", "online")
                                param("sort", "latest")
                                param("size", "20")
                                param("page", "1")
                            }.andExpect { status { isOk() } }
                            .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                            .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                            .andReturn()

                    val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                    val got = body.data!!

                    // Then
                    val items = got.items
                    assertThat(got.page).isEqualTo(1)
                    assertThat(got.size).isEqualTo(20)
                    assertThat(got.totalElements).isEqualTo(1)
                    assertThat(got.totalPages).isEqualTo(1)
                    assertThat(items[0].id).isEqualTo(given1.id)
                }

                @Test
                @DisplayName("[성공] all인 경우 온/오프라인 모두 조회한다.")
                fun `all 일 경우 온,오프라인 모두 조회한다`() {
                    // Given
                    val given1 =
                        activityRepository.save(
                            EducationActivity(
                                title = "테스트 교육활동",
                                organizer = OrganizerType.PUBLIC_ORGANIZATION,
                                targetAudience = ParticipantType.WORKER,
                                location = LocationType.SEOUL_INCHEON,
                                recruitmentStartDate = LocalDate.now().plusDays(1),
                                recruitmentEndDate = LocalDate.now().plusMonths(1),
                                startDate = LocalDate.now().plusMonths(3),
                                endDate = LocalDate.now().plusMonths(6),
                                status = RecruitmentStatus.OPEN,
                                viewCount = 0L,
                                duration =
                                    ChronoUnit.DAYS
                                        .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                        .toInt(),
                                activityHomepageUrl = null,
                                activityApplicationUrl = null,
                                activityThumbnailUrl = null,
                                activityGroup = activityGroup,
                                cost = 0L,
                                costType = EducationCostType.FULLY_GOVERNMENT,
                                format = EducationFormatType.ONLINE,
                                benefit = "",
                            ),
                        )

                    activityRepository.save(
                        EducationActivity(
                            title = "테스트 교육활동2",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            cost = 0L,
                            costType = EducationCostType.FULLY_GOVERNMENT,
                            format = EducationFormatType.OFFLINE,
                            benefit = "",
                        ),
                    )

                    // When
                    val result =
                        mockMvc
                            .get("/v1/activities") {
                                param("category", "education")
                                param("sort", "latest")
                                param("format", "all")
                                param("size", "20")
                                param("page", "1")
                            }.andExpect { status { isOk() } }
                            .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                            .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                            .andReturn()

                    val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                    val got = body.data!!

                    // Then
                    val items = got.items
                    assertThat(got.page).isEqualTo(1)
                    assertThat(got.size).isEqualTo(20)
                    assertThat(got.totalElements).isEqualTo(2)
                    assertThat(got.totalPages).isEqualTo(1)
                    assertThat(items[0].id).isEqualTo(given1.id)
                }
            }

            @Test
            @DisplayName("[성공] 교육활동 목록을 조회할 때 비용 필터링 조건을 적용한다.")
            fun `비용 필터를 적용한다`() {
                // Given
                val given1 =
                    activityRepository.save(
                        EducationActivity(
                            title = "테스트 교육활동",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            cost = 0L,
                            costType = EducationCostType.FULLY_GOVERNMENT,
                            format = EducationFormatType.ONLINE,
                            benefit = "",
                        ),
                    )

                activityRepository.save(
                    EducationActivity(
                        title = "테스트 교육활동2",
                        organizer = OrganizerType.PUBLIC_ORGANIZATION,
                        targetAudience = ParticipantType.WORKER,
                        location = LocationType.SEOUL_INCHEON,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusMonths(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        cost = 0L,
                        costType = EducationCostType.FREE,
                        format = EducationFormatType.OFFLINE,
                        benefit = "",
                    ),
                )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "education")
                            param("costType", "fully_government")
                            param("sort", "latest")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(20)
                assertThat(got.totalElements).isEqualTo(1)
                assertThat(got.totalPages).isEqualTo(1)
                assertThat(items[0].id).isEqualTo(given1.id)
            }

            @Test
            @DisplayName("[성공] 교육활동 목록을 조회할 때 교육 기간 필터 조건을 적용한다.")
            fun `교육 기간 필터 조건을 적용한다`() {
                // Given
                val given1 =
                    activityRepository.save(
                        EducationActivity(
                            title = "테스트 교육활동",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusDays(3),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 6, 27), LocalDate.of(2025, 6, 30))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            cost = 0L,
                            costType = EducationCostType.FULLY_GOVERNMENT,
                            format = EducationFormatType.ONLINE,
                            benefit = "",
                        ),
                    )

                activityRepository.save(
                    EducationActivity(
                        title = "테스트 교육활동2",
                        organizer = OrganizerType.PUBLIC_ORGANIZATION,
                        targetAudience = ParticipantType.WORKER,
                        location = LocationType.SEOUL_INCHEON,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusMonths(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        cost = 0L,
                        costType = EducationCostType.FULLY_GOVERNMENT,
                        format = EducationFormatType.OFFLINE,
                        benefit = "",
                    ),
                )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "education")
                            param("duration", "1")
                            param("sort", "latest")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(20)
                assertThat(got.totalElements).isEqualTo(1)
                assertThat(got.totalPages).isEqualTo(1)
                assertThat(items[0].id).isEqualTo(given1.id)
            }

            @Test
            @DisplayName("[성공] 공모전/해커톤 활동을 조회할 때 도메인 필터를 적용한다.")
            fun `도메인 필터 조건을 적용한다`() {
                val given1 =
                    activityRepository.save(
                        CompetitionActivity(
                            title = "테스트 공모전",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.UNIVERSITY_STUDENT,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            domain = DomainType.EDUCATION,
                            cost = 10000000,
                            benefit = "",
                        ),
                    )

                activityRepository.save(
                    CompetitionActivity(
                        title = "테스트 공모전2",
                        organizer = OrganizerType.PUBLIC_ORGANIZATION,
                        targetAudience = ParticipantType.UNIVERSITY_STUDENT,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusMonths(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        domain = DomainType.SAAS,
                        cost = 10000000,
                        benefit = "",
                    ),
                )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "competition")
                            param("domain", "education")
                            param("sort", "latest")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(20)
                assertThat(got.totalElements).isEqualTo(1)
                assertThat(got.totalPages).isEqualTo(1)
                assertThat(items[0].id).isEqualTo(given1.id)
            }

            @Test
            @DisplayName("[성공] 공모전/해커톤 활동을 조회할 때 시상 규모 필터를 적용한다.")
            fun `시상 규모 필터를 적용한다`() {
                val given1 =
                    activityRepository.save(
                        CompetitionActivity(
                            title = "테스트 공모전",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.UNIVERSITY_STUDENT,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            domain = DomainType.EDUCATION,
                            cost = 3000000,
                            benefit = "",
                        ),
                    )

                activityRepository.save(
                    CompetitionActivity(
                        title = "테스트 공모전2",
                        organizer = OrganizerType.PUBLIC_ORGANIZATION,
                        targetAudience = ParticipantType.UNIVERSITY_STUDENT,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusMonths(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        domain = DomainType.SAAS,
                        cost = 9000000,
                        benefit = "",
                    ),
                )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "competition")
                            param("award", "100")
                            param("award", "5000000")
                            param("sort", "latest")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(20)
                assertThat(got.totalElements).isEqualTo(1)
                assertThat(got.totalPages).isEqualTo(1)
                assertThat(items[0].id).isEqualTo(given1.id)
            }

            @Test
            @DisplayName("[성공] 활동 조회 시 마감 임박 순 정렬을 적용한다.")
            fun `마감 임박 순 정렬 테스트`() {
                activityRepository.save(
                    ExternalActivity(
                        title = "테스트 대외활동1",
                        organizer = OrganizerType.PUBLIC_ORGANIZATION,
                        targetAudience = ParticipantType.WORKER,
                        location = LocationType.SEOUL_INCHEON,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusMonths(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        activityField = ActivityFieldType.MENTORING,
                        benefit = "",
                    ),
                )

                val given2 =
                    activityRepository.save(
                        ExternalActivity(
                            title = "테스트 대외활동2",
                            organizer = OrganizerType.LARGE_CORPORATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusDays(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "extracurricular")
                            param("sort", "deadline_asc")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(20)
                assertThat(got.totalElements).isEqualTo(2)
                assertThat(got.totalPages).isEqualTo(1)
                assertThat(items[0].id).isEqualTo(given2.id)
            }

            @Test
            @DisplayName("[성공] 활동 조회 시 여유 있는 순 정렬을 적용한다.")
            fun `여유 있는 순 정렬 테스트`() {
                val given1 =
                    activityRepository.save(
                        ExternalActivity(
                            title = "테스트 대외활동1",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                activityRepository.save(
                    ExternalActivity(
                        title = "테스트 대외활동2",
                        organizer = OrganizerType.LARGE_CORPORATION,
                        targetAudience = ParticipantType.WORKER,
                        location = LocationType.SEOUL_INCHEON,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusDays(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        activityField = ActivityFieldType.MENTORING,
                        benefit = "",
                    ),
                )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "extracurricular")
                            param("sort", "deadline_desc")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(20)
                assertThat(got.totalElements).isEqualTo(2)
                assertThat(got.totalPages).isEqualTo(1)
                assertThat(items[0].id).isEqualTo(given1.id)
            }

            @Test
            @WithMockUser
            @DisplayName("[성공] 유저 북마크 여부 확인 테스트")
            fun `유저 북마크 확인 여부 테스트`() {
                val given1 =
                    activityRepository.save(
                        ExternalActivity(
                            title = "테스트 대외활동1",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 0L,
                            duration =
                                ChronoUnit.DAYS
                                    .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                    .toInt(),
                            activityHomepageUrl = null,
                            activityApplicationUrl = null,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                val member =
                    memberRepository.save(
                        Member(
                            name = "테스트유저",
                            email = "test@example.com",
                        ),
                    )

                activityBookmarkRepository.save(
                    ActivityBookmark(
                        member = member,
                        activity = given1,
                    ),
                )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities") {
                            param("category", "extracurricular")
                            param("sort", "latest")
                            param("size", "20")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                val items = got.items
                assertThat(items[0].isBookmarked).isTrue
            }
        }

        @Nested
        @DisplayName("홈 화면에 관련된 활동 리스트를 조회한다")
        inner class GetHomeActivitiesTest {
            @Test
            @WithMockUser
            @DisplayName("[성공] 유저 기반 직무 추천 활동 리스트를 조회한다")
            fun getUserJobRecommendedActivitiesTest() {
                // Given
                val member =
                    memberRepository.save(
                        Member(
                            name = "테스트 유저",
                            email = "test@example.com",
                        ),
                    )

                // 유저 관심 직무 설정 (개발, 기획)
                val developmentJobCategory =
                    jobCategoryRepository.save(
                        JobCategory(jobGroup = JobGroup.DEVELOPMENT),
                    )
                val planningJobCategory =
                    jobCategoryRepository.save(
                        JobCategory(jobGroup = JobGroup.PLANNING),
                    )
                val marketingJobCategory =
                    jobCategoryRepository.save(
                        JobCategory(jobGroup = JobGroup.MARKETING),
                    )

                interestedJobCategoryRepository.save(
                    InterestedJobCategory(member = member, jobCategory = developmentJobCategory),
                )
                interestedJobCategoryRepository.save(
                    InterestedJobCategory(member = member, jobCategory = planningJobCategory),
                )

                // 유저 관심 직무 기반 활동 2개 생성
                val interestedActivity1 =
                    activityRepository.save(
                        ExternalActivity(
                            title = "관심 직무 활동 1",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 10L,
                            duration = 90,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                val interestedActivity2 =
                    activityRepository.save(
                        ExternalActivity(
                            title = "관심 직무 활동 2",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 10L,
                            duration = 90,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                // 활동과 직무 카테고리 연결
                activityJobCategoryRepository.save(
                    ActivityJobCategory(activity = interestedActivity1, jobCategory = developmentJobCategory),
                )
                activityJobCategoryRepository.save(
                    ActivityJobCategory(activity = interestedActivity2, jobCategory = planningJobCategory),
                )

                // activity2 북마크 추가
                activityBookmarkRepository.save(
                    ActivityBookmark(member = member, activity = interestedActivity2),
                )
                activityBookmarkRepository.save(
                    ActivityBookmark(
                        member = memberRepository.save(Member(name = "테스트 유저 1", email = "test1@example.com")),
                        activity = interestedActivity2,
                    ),
                )
                activityBookmarkRepository.save(
                    ActivityBookmark(
                        member = memberRepository.save(Member(name = "테스트 유저 2", email = "test2@example.com")),
                        activity = interestedActivity2,
                    ),
                )

                // 유저가 관심없는 직무 기반 활동 1개 생성
                val nonInterestedActivity =
                    activityRepository.save(
                        ExternalActivity(
                            title = "관심없는 직무 활동",
                            organizer = OrganizerType.PUBLIC_ORGANIZATION,
                            targetAudience = ParticipantType.WORKER,
                            location = LocationType.SEOUL_INCHEON,
                            recruitmentStartDate = LocalDate.now().plusDays(1),
                            recruitmentEndDate = LocalDate.now().plusMonths(1),
                            startDate = LocalDate.now().plusMonths(3),
                            endDate = LocalDate.now().plusMonths(6),
                            status = RecruitmentStatus.OPEN,
                            viewCount = 200L,
                            duration = 90,
                            activityThumbnailUrl = null,
                            activityGroup = activityGroup,
                            activityField = ActivityFieldType.MENTORING,
                            benefit = "",
                        ),
                    )

                activityJobCategoryRepository.save(
                    ActivityJobCategory(activity = nonInterestedActivity, jobCategory = marketingJobCategory),
                )

                // When - 데이터 1개만 조회되도록 mockMvc 설정
                val result =
                    mockMvc
                        .get("/v1/activities/recommendations") {
                            param("size", "1")
                            param("page", "1")
                        }.andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITIES.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITIES.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityListResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then - 관심 직무 기반 활동 2개중, 북마크 수 + 조회수가 높은 활동을 가져오는지 체크
                val items = got.items
                assertThat(got.page).isEqualTo(1)
                assertThat(got.size).isEqualTo(1)
                assertThat(got.totalElements).isEqualTo(2)
                assertThat(got.totalPages).isEqualTo(2)
                assertThat(items[0].id).isEqualTo(interestedActivity2.id)
                assertThat(items[0].isBookmarked).isTrue
            }
        }

    @Nested
    @DisplayName("활동 상세 조회 통합 테스트")
    inner class GetActivityDetailTest {
        @Test
        @WithMockUser
        @DisplayName("[성공] 활동 상세 조회 - 유저 북마크 포함")
        fun `활동 상세 조회`() {
            // Given
            val given =
                activityRepository.save(
                    ExternalActivity(
                        title = "테스트 대외활동",
                        organizer = OrganizerType.PUBLIC_ORGANIZATION,
                        targetAudience = ParticipantType.WORKER,
                        location = LocationType.SEOUL_INCHEON,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusMonths(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        activityField = ActivityFieldType.MENTORING,
                        benefit = "테스트 혜택",
                    ),
                )

                val member =
                    memberRepository.save(
                        Member(
                            name = "기존유저",
                            email = "test@example.com",
                        ),
                    )

                activityBookmarkRepository.save(
                    ActivityBookmark(
                        member = member,
                        activity = given,
                    ),
                )

                // When
                val result =
                    mockMvc
                        .get("/v1/activities/${given.id}")
                        .andExpect { status { isOk() } }
                        .andExpect { jsonPath("$.code") { value(SuccessCode.GET_ACTIVITY_DETAIL.status.value()) } }
                        .andExpect { jsonPath("$.message") { value(SuccessCode.GET_ACTIVITY_DETAIL.message) } }
                        .andReturn()

                val body: ResponseWrapper<GetActivityDetailResponse> = result.extractBody(mapper)
                val got = body.data!!

                // Then
                assertThat(got).isNotNull
                assertThat(got.id).isEqualTo(given.id)
                assertThat(got.title).isEqualTo(given.title)
                assertThat(got.category).isEqualTo(ActivityType.EXTRACURRICULAR.name)
                assertThat(got.domains).isEmpty()
                assertThat(got.regions).isEqualTo(listOf("서울", "인천"))
                assertThat(got.benefits).isEqualTo("테스트 혜택")
                assertThat(got.isBookmarked).isTrue
            }
        }

        @Nested
        @DisplayName("활동 조회 수 증가 중복 방지 테스트")
        inner class IncreaseViewCountTest {
            @BeforeEach
            fun cacheClear() {
                cacheManager.getCache("activityViewCount")?.clear()
            }

        @Test
        @DisplayName("[성공] 2개의 다른 ip + user-agent 에서 짧은 시간 내에 대량의 요청을 날려도 조회수에 제한이 걸린다")
        fun increaseViewCountLimitOtherUserAgentIp() {
            // given
            val given =
                activityRepository.save(
                    ExternalActivity(
                        title = "테스트 대외활동",
                        organizer = OrganizerType.PUBLIC_ORGANIZATION,
                        targetAudience = ParticipantType.WORKER,
                        location = LocationType.SEOUL_INCHEON,
                        recruitmentStartDate = LocalDate.now().plusDays(1),
                        recruitmentEndDate = LocalDate.now().plusMonths(1),
                        startDate = LocalDate.now().plusMonths(3),
                        endDate = LocalDate.now().plusMonths(6),
                        status = RecruitmentStatus.OPEN,
                        viewCount = 0L,
                        duration =
                            ChronoUnit.DAYS
                                .between(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 12, 31))
                                .toInt(),
                        activityHomepageUrl = null,
                        activityApplicationUrl = null,
                        activityThumbnailUrl = null,
                        activityGroup = activityGroup,
                        activityField = ActivityFieldType.MENTORING,
                        benefit = "테스트 혜택",
                    ),
                )
            assertThat(given.viewCount).isEqualTo(0)

                // when
                var requestCount = 0
                val userAgents = listOf("Test1", "Test2")
                val repeatCnt = 30

                userAgents.forEach { userAgent ->
                    repeat(repeatCnt) {
                        requestCount++
                        mockMvc
                            .post("/v1/activities/${given.id}/view") {
                                header("User-Agent", userAgent)
                            }.andExpect { status { isOk() } }
                    }
                }

                // then
                val updated = activityRepository.findById(given.id).get()
                assertThat(requestCount).isEqualTo(userAgents.size * repeatCnt)
                assertThat(updated.viewCount).isEqualTo((userAgents.size * MAX_VIEW_ATTEMPTS).toLong())
            }
        }
    }
}
