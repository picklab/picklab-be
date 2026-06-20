package picklab.backend.search

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.servlet.get
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.OrganizerType
import picklab.backend.activity.domain.enums.ParticipantType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.domain.repository.ActivityRepository
import picklab.backend.activitygroup.domain.entity.ActivityGroup
import picklab.backend.activitygroup.domain.repository.ActivityGroupRepository
import picklab.backend.search.domain.repository.PopularSearchKeywordEventRepository
import picklab.backend.template.IntegrationTest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class PopularSearchKeywordIntegrationTest : IntegrationTest() {
    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Autowired
    lateinit var activityGroupRepository: ActivityGroupRepository

    @Autowired
    lateinit var popularSearchKeywordEventRepository: PopularSearchKeywordEventRepository

    @BeforeEach
    fun setUp() {
        cleanUp.all()
    }

    @Test
    @DisplayName("비로그인 통합 검색 요청은 인기 검색어 집계 이벤트로 기록된다")
    fun recordGuestSearchEvent() {
        saveActivity("테스트 검색 활동")

        mockMvc
            .get("/v1/search") {
                param("keyword", " 테스트 ")
                header("X-Forwarded-For", "203.0.113.10")
            }.andExpect { status { isOk() } }

        val searchHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        val ranks =
            popularSearchKeywordEventRepository.findRanksBySearchHour(
                searchHour = searchHour,
                minSearchCount = 1,
                pageable = PageRequest.of(0, 10),
            )
        val savedEvent = popularSearchKeywordEventRepository.findAll().single()

        assertThat(ranks).hasSize(1)
        assertThat(ranks[0].keyword).isEqualTo("테스트")
        assertThat(savedEvent.searcherKey).startsWith("GUEST:")
        assertThat(savedEvent.searcherKey).doesNotContain("203.0.113.10")
    }

    @Test
    @DisplayName("자동완성 요청은 인기 검색어 집계 이벤트로 기록하지 않는다")
    fun doNotRecordAutocomplete() {
        saveActivity("테스트 검색 활동")

        mockMvc
            .get("/v1/search/autocomplete") {
                param("keyword", "테스트")
            }.andExpect { status { isOk() } }

        assertThat(popularSearchKeywordEventRepository.findAll()).isEmpty()
    }

    private fun saveActivity(title: String) {
        val activityGroup =
            activityGroupRepository.save(
                ActivityGroup(
                    name = "테스트 그룹",
                    description = "테스트 그룹 설명",
                ),
            )

        activityRepository.save(
            ExternalActivity(
                title = title,
                organizer = "테스트 주최",
                organizerType = OrganizerType.PUBLIC_ORGANIZATION,
                targetAudience = ParticipantType.WORKER,
                location = LocationType.SEOUL_INCHEON,
                recruitmentStartDate = LocalDate.now().minusDays(1),
                recruitmentEndDate = LocalDate.now().plusMonths(1),
                startDate = LocalDate.now().plusMonths(2),
                endDate = LocalDate.now().plusMonths(3),
                status = RecruitmentStatus.OPEN,
                viewCount = 0L,
                duration = 30,
                activityHomepageUrl = null,
                activityApplicationUrl = null,
                activityThumbnailUrl = null,
                description = "테스트 설명",
                benefit = "테스트 혜택",
                activityGroup = activityGroup,
                activityField = ActivityFieldType.MENTORING,
            ),
        )
    }
}
