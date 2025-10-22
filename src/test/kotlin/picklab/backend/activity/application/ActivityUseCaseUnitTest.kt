package picklab.backend.activity.application

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import jakarta.servlet.http.HttpServletRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import picklab.backend.activity.application.model.*
import picklab.backend.activity.domain.entity.ExternalActivity
import picklab.backend.activity.domain.enums.ActivityType
import picklab.backend.activity.domain.service.ActivityBookmarkService
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.member.domain.MemberService
import picklab.backend.member.domain.entity.Member
import picklab.backend.member.domain.service.MemberActivityViewHistoryService

@ExtendWith(MockKExtension::class)
class ActivityUseCaseUnitTest {
    @MockK
    lateinit var memberService: MemberService

    @MockK
    lateinit var activityService: ActivityService

    @MockK
    lateinit var activityQueryService: ActivityQueryService

    @MockK
    lateinit var activityBookmarkService: ActivityBookmarkService

    @MockK
    lateinit var viewCountLimiterPort: ViewCountLimiterPort

    @MockK
    lateinit var memberActivityViewHistoryService: MemberActivityViewHistoryService

    @InjectMockKs
    lateinit var activityUseCase: ActivityUseCase

    @Nested
    @DisplayName("getActivities 유즈케이스 테스트")
    inner class GetActivitiesTest {
        @Test
        @DisplayName("로그인하지 않은 사용자가 활동 목록을 조회한다.")
        fun getActivities_notLogInUser() {
            // given
            val queryParams = createMockActivitySearchCondition()
            val size = 10
            val page = 1
            val memberId = null

            val mockActivityView = createMockActivityView(1L)
            val activityPage = PageImpl(listOf(mockActivityView), PageRequest.of(0, size), 1)
            val adjustedQuery =
                queryParams.copy(
                    format = null,
                    costType = null,
                    award = null,
                    duration = null,
                    domain = null,
                )

            every { activityService.adjustQueryByCategory(queryParams) } returns adjustedQuery
            every { activityService.getActivities(adjustedQuery, any()) } returns activityPage
            every { activityBookmarkService.getMyBookmarkedActivityIds(null, listOf(1L)) } returns emptySet()

            // when
            val result = activityUseCase.getActivities(queryParams, size, page, memberId)

            // then
            assertThat(result.items).hasSize(1)
            assertThat(result.items[0].isBookmarked).isFalse()
            assertThat(result.page).isEqualTo(1)
            assertThat(result.size).isEqualTo(size)

            verify(exactly = 1) { activityService.adjustQueryByCategory(queryParams) }
            verify(exactly = 1) { activityService.getActivities(adjustedQuery, any()) }
            verify(exactly = 1) { activityBookmarkService.getMyBookmarkedActivityIds(null, listOf(1L)) }
        }

        @Test
        @DisplayName("로그인한 사용자가 활동 목록을 조회하고 북마크 정보가 포함된다")
        fun getActivities_logInUser() {
            // given
            val queryParams = createMockActivitySearchCondition()
            val size = 10
            val page = 1
            val memberId = 1L

            val mockActivityView = createMockActivityView(1L)
            val activityPage = PageImpl(listOf(mockActivityView), PageRequest.of(0, size), 1)
            val adjustedQuery =
                queryParams.copy(
                    format = null,
                    costType = null,
                    award = null,
                    duration = null,
                    domain = null,
                )
            val bookmarkedActivityIds = setOf(1L)

            every { activityService.adjustQueryByCategory(queryParams) } returns adjustedQuery
            every { activityService.getActivities(adjustedQuery, any()) } returns activityPage
            every {
                activityBookmarkService.getMyBookmarkedActivityIds(
                    memberId,
                    listOf(1L),
                )
            } returns bookmarkedActivityIds

            // when
            val result = activityUseCase.getActivities(queryParams, size, page, memberId)

            // then
            assertThat(result.items).hasSize(1)
            assertThat(result.items[0].isBookmarked).isTrue() // 북마크된 상태

            verify(exactly = 1) { activityBookmarkService.getMyBookmarkedActivityIds(memberId, listOf(1L)) }
        }
    }

    @Nested
    @DisplayName("getActivityDetail 유즈케이스 테스트")
    inner class GetActivityDetailTest {
        @Test
        @DisplayName("로그인하지 않은 사용자가 활동을 조회한다")
        fun getActivityUser_notLogInUser() {
            // given
            val activityId = 1L
            val memberId: Long? = null
            val mockActivity = createMockActivity(activityId)
            val bookmarkCount = 5L

            every { activityService.mustFindById(activityId) } returns mockActivity
            every { activityBookmarkService.countByActivityId(activityId) } returns bookmarkCount

            // when
            val result = activityUseCase.getActivityDetail(activityId, memberId)

            // then
            assertThat(result.id).isEqualTo(activityId)
            assertThat(result.isBookmarked).isFalse()

            verify(exactly = 1) { activityService.mustFindById(activityId) }
            verify(exactly = 1) { activityBookmarkService.countByActivityId(activityId) }
            verify(exactly = 0) { activityBookmarkService.existsByMemberIdAndActivityId(any(), any()) }
        }

        @Test
        @DisplayName("로그인한 사용자가 북마크하지 않은 활동을 조회한다")
        fun getActivityUser_LogInUser_NotBookmarked() {
            // given
            val activityId = 1L
            val memberId = 123L
            val mockActivity = createMockActivity(activityId)
            val bookmarkCount = 5L

            every { activityService.mustFindById(activityId) } returns mockActivity
            every { activityBookmarkService.countByActivityId(activityId) } returns bookmarkCount
            every { activityBookmarkService.existsByMemberIdAndActivityId(memberId, activityId) } returns false

            // when
            val result = activityUseCase.getActivityDetail(activityId, memberId)

            // then
            assertThat(result.isBookmarked).isFalse()

            verify(exactly = 1) { activityBookmarkService.existsByMemberIdAndActivityId(memberId, activityId) }
        }

        @Test
        @DisplayName("로그인한 사용자가 북마크한 활동을 조회한다")
        fun getActivityUser_LogInUser_Bookmarked() {
            // given
            val activityId = 1L
            val memberId = 123L
            val mockActivity = createMockActivity(activityId)
            val bookmarkCount = 5L

            every { activityService.mustFindById(activityId) } returns mockActivity
            every { activityBookmarkService.countByActivityId(activityId) } returns bookmarkCount
            every { activityBookmarkService.existsByMemberIdAndActivityId(memberId, activityId) } returns true

            // when
            val result = activityUseCase.getActivityDetail(activityId, memberId)

            // then
            assertThat(result.isBookmarked).isTrue()
        }
    }

    @Nested
    @DisplayName("recordActivityView 유즈케이스 테스트")
    inner class RecordActivityViewTest {
        @Test
        @DisplayName("조회수 증가가 허용된 경우 정상적으로 처리된다")
        fun allow_increaseView() {
            // given
            val activityId = 1L
            val memberId = 123L
            val mockActivity = createMockActivity(activityId)
            val mockRequest = createMockHttpServletRequest("192.168.0.1", "Mozilla/5.0")
            val viewIdentifier = "activity:1:ip:192.168.0.1:userAgent:Mozilla/5.0"

            every { activityService.mustFindById(activityId) } returns mockActivity
            every { viewCountLimiterPort.isViewCountUpAllowed(activityId, viewIdentifier) } returns true
            every { mockActivity.increaseViewCount() } just Runs
            every { memberActivityViewHistoryService.recordActivityView(memberId, mockActivity) } just Runs

            // when
            activityUseCase.recordActivityView(activityId, mockRequest, memberId)

            // then
            verify(exactly = 1) { activityService.mustFindById(activityId) }
            verify(exactly = 1) { viewCountLimiterPort.isViewCountUpAllowed(activityId, viewIdentifier) }
            verify(exactly = 1) { mockActivity.increaseViewCount() }
            verify(exactly = 1) { memberActivityViewHistoryService.recordActivityView(memberId, mockActivity) }
        }

        @Test
        @DisplayName("조회수 증가가 제한된 경우 조회수는 증가하지 않지만 이력은 저장된다")
        fun disallow_increaseView() {
            // given
            val activityId = 1L
            val memberId = 123L
            val mockActivity = createMockActivity(activityId)
            val mockRequest = createMockHttpServletRequest("192.168.0.1", "Mozilla/5.0")
            val viewIdentifier = "activity:1:ip:192.168.0.1:userAgent:Mozilla/5.0"

            every { activityService.mustFindById(activityId) } returns mockActivity
            every { viewCountLimiterPort.isViewCountUpAllowed(activityId, viewIdentifier) } returns false
            every { memberActivityViewHistoryService.recordActivityView(memberId, mockActivity) } just Runs

            // when
            activityUseCase.recordActivityView(activityId, mockRequest, memberId)

            // then
            verify(exactly = 1) { viewCountLimiterPort.isViewCountUpAllowed(activityId, viewIdentifier) }
            verify(exactly = 0) { mockActivity.increaseViewCount() }
            verify(exactly = 1) { memberActivityViewHistoryService.recordActivityView(memberId, mockActivity) }
        }

        @Test
        @DisplayName("로그인하지 않은 사용자의 경우 이력은 저장되지 않는다")
        fun noHistory_notLogInUser() {
            // given
            val activityId = 1L
            val memberId: Long? = null
            val mockActivity = createMockActivity(activityId)
            val mockRequest = createMockHttpServletRequest("192.168.0.1", "Mozilla/5.0")
            val viewIdentifier = "activity:1:ip:192.168.0.1:userAgent:Mozilla/5.0"

            every { activityService.mustFindById(activityId) } returns mockActivity
            every { viewCountLimiterPort.isViewCountUpAllowed(activityId, viewIdentifier) } returns true
            every { mockActivity.increaseViewCount() } just Runs

            // when
            activityUseCase.recordActivityView(activityId, mockRequest, memberId)

            // then
            verify(exactly = 1) { mockActivity.increaseViewCount() }
            verify(exactly = 0) { memberActivityViewHistoryService.recordActivityView(any(), any()) }
        }
    }

    @Nested
    @DisplayName("getRecommendationActivities 유즈케이스 테스트")
    inner class GetRecommendationActivitiesTest {
        @Test
        @DisplayName("사용자의 관심 직무 기반 추천 활동을 조회한다")
        fun getPopularActivity_relationUserJobDetail() {
            // given
            val memberId = 123L
            val condition = RecommendActivitiesCondition(memberId = memberId, size = 10, page = 1)
            val mockMember = createMockMember(memberId)
            val jobIds = listOf(1L, 2L, 3L)
            val mockActivityView1 = createMockActivityView(1L)
            val mockActivityView2 = createMockActivityView(2L)
            val activityPage =
                PageImpl(
                    listOf(mockActivityView1, mockActivityView2),
                    PageRequest.of(0, 10),
                    2,
                )
            val bookmarkedActivityIds = setOf(1L)

            every { memberService.findActiveMember(memberId) } returns mockMember
            every { memberService.findMyInterestedJobCategoryIds(mockMember) } returns jobIds
            every { activityQueryService.getRecommendationActivities(jobIds, any()) } returns activityPage
            every {
                activityBookmarkService.getMyBookmarkedActivityIds(
                    memberId,
                    listOf(1L, 2L),
                )
            } returns bookmarkedActivityIds

            // when
            val result = activityUseCase.getRecommendationActivities(condition)

            // then
            assertThat(result.items).hasSize(2)
            assertThat(result.items[0].isBookmarked).isTrue()
            assertThat(result.items[1].isBookmarked).isFalse()
            assertThat(result.page).isEqualTo(1)
            assertThat(result.size).isEqualTo(10)
            assertThat(result.totalElements).isEqualTo(2)

            verify(exactly = 1) { memberService.findActiveMember(memberId) }
            verify(exactly = 1) { memberService.findMyInterestedJobCategoryIds(mockMember) }
            verify(exactly = 1) { activityQueryService.getRecommendationActivities(jobIds, any()) }
            verify(exactly = 1) { activityBookmarkService.getMyBookmarkedActivityIds(memberId, listOf(1L, 2L)) }
        }
    }

    @Nested
    @DisplayName("getPopularActivities 유즈케이스 테스트")
    inner class GetPopularActivitiesTest {
        @Test
        @DisplayName("로그인한 사용자가 인기 활동을 조회한다 - 북마크 정보 포함")
        fun getPopularActivities_logInUser_WithBookmark() {
            // given
            val memberId = 123L
            val condition = PopularActivitiesCondition(memberId = memberId, size = 10, page = 1)
            val mockActivityView1 = createMockActivityView(1L)
            val mockActivityView2 = createMockActivityView(2L)
            val activityPage =
                PageImpl(
                    listOf(mockActivityView1, mockActivityView2),
                    PageRequest.of(0, 10),
                    2,
                )
            val bookmarkedActivityIds = setOf(1L)

            every { activityService.getPopularActivities(any()) } returns activityPage
            every {
                activityBookmarkService.getMyBookmarkedActivityIds(
                    memberId,
                    listOf(1L, 2L),
                )
            } returns bookmarkedActivityIds

            // when
            val result = activityUseCase.getPopularActivities(condition)

            // then
            assertThat(result.items).hasSize(2)
            assertThat(result.items[0].isBookmarked).isTrue()
            assertThat(result.items[1].isBookmarked).isFalse()

            verify(exactly = 1) { activityService.getPopularActivities(any()) }
            verify(exactly = 1) { activityBookmarkService.getMyBookmarkedActivityIds(memberId, listOf(1L, 2L)) }
        }

        @Test
        @DisplayName("로그인하지 않은 사용자가 인기 활동을 조회한다 - 북마크 정보 없음")
        fun getPopularActivities_notLogInUser_WithBookmark() {
            // given
            val memberId: Long? = null
            val condition = PopularActivitiesCondition(memberId = memberId, size = 10, page = 1)
            val mockActivityView = createMockActivityView(1L)
            val activityPage = PageImpl(listOf(mockActivityView), PageRequest.of(0, 10), 1)

            every { activityService.getPopularActivities(any()) } returns activityPage

            // when
            val result = activityUseCase.getPopularActivities(condition)

            // then
            assertThat(result.items).hasSize(1)
            assertThat(result.items[0].isBookmarked).isFalse()

            verify(exactly = 1) { activityService.getPopularActivities(any()) }
            verify(exactly = 0) { activityBookmarkService.getMyBookmarkedActivityIds(any(), any()) }
        }

        @Test
        @DisplayName("빈 인기 활동 목록을 조회한다")
        fun getEmptyPopularActivities() {
            // given
            val memberId = 123L
            val condition = PopularActivitiesCondition(memberId = memberId, size = 10, page = 1)
            val emptyActivityPage = PageImpl<ActivityView>(emptyList(), PageRequest.of(0, 10), 0)

            every { activityService.getPopularActivities(any()) } returns emptyActivityPage
            every { activityBookmarkService.getMyBookmarkedActivityIds(memberId, emptyList()) } returns emptySet()

            // when
            val result = activityUseCase.getPopularActivities(condition)

            // then
            assertThat(result.items).isEmpty()
            assertThat(result.totalElements).isEqualTo(0)

            verify(exactly = 1) { activityService.getPopularActivities(any()) }
            verify(exactly = 1) { activityBookmarkService.getMyBookmarkedActivityIds(memberId, emptyList()) }
        }
    }

    @Nested
    @DisplayName("getRecentlyViewedActivities 유즈케이스 테스트")
    inner class GetRecentlyViewedActivitiesTest {
        @Test
        @DisplayName("사용자의 최근 조회 활동을 북마크 정보와 함께 조회한다")
        fun getUserRecentlyActivity_withBookmark() {
            // given
            val memberId = 123L
            val condition = RecentlyViewedActivitiesCondition(memberId = memberId, size = 10, page = 1)
            val mockActivityView1 = createMockActivityView(1L)
            val mockActivityView2 = createMockActivityView(2L)
            val activityPage =
                PageImpl(
                    listOf(mockActivityView1, mockActivityView2),
                    PageRequest.of(0, 10),
                    2,
                )
            val bookmarkedActivityIds = setOf(1L, 2L)

            every { activityQueryService.getRecentlyViewedActivities(memberId, any()) } returns activityPage
            every {
                activityBookmarkService.getMyBookmarkedActivityIds(
                    memberId,
                    listOf(1L, 2L),
                )
            } returns bookmarkedActivityIds

            // when
            val result = activityUseCase.getRecentlyViewedActivities(condition)

            // then
            assertThat(result.items).hasSize(2)
            assertThat(result.items[0].isBookmarked).isTrue()
            assertThat(result.items[1].isBookmarked).isTrue()

            verify(exactly = 1) { activityQueryService.getRecentlyViewedActivities(memberId, any()) }
            verify(exactly = 1) { activityBookmarkService.getMyBookmarkedActivityIds(memberId, listOf(1L, 2L)) }
        }

        @Test
        @DisplayName("최근 조회 활동이 없는 경우 빈 리스트를 반환한다")
        fun noRecentActivities_shouldReturnEmptyList() {
            // given
            val memberId = 123L
            val condition = RecentlyViewedActivitiesCondition(memberId = memberId, size = 10, page = 1)
            val emptyActivityPage = PageImpl<ActivityView>(emptyList(), PageRequest.of(0, 10), 0)

            every { activityQueryService.getRecentlyViewedActivities(memberId, any()) } returns emptyActivityPage
            every { activityBookmarkService.getMyBookmarkedActivityIds(memberId, emptyList()) } returns emptySet()

            // when
            val result = activityUseCase.getRecentlyViewedActivities(condition)

            // then
            assertThat(result.items).isEmpty()
            assertThat(result.totalElements).isEqualTo(0)

            verify(exactly = 1) { activityQueryService.getRecentlyViewedActivities(memberId, any()) }
            verify(exactly = 1) { activityBookmarkService.getMyBookmarkedActivityIds(memberId, emptyList()) }
        }
    }

    private fun createMockActivitySearchCondition(): ActivitySearchCondition =
        mockk<ActivitySearchCondition>(relaxed = true) {
            every { category } returns ActivityType.EXTRACURRICULAR
            every {
                copy(
                    format = null,
                    costType = null,
                    award = null,
                    duration = null,
                    domain = null,
                )
            } returns this@mockk
        }

    private fun createMockActivityView(id: Long): ActivityView =
        mockk<ActivityView>(relaxed = true) {
            every { this@mockk.id } returns id
        }

    private fun createMockActivity(id: Long): ExternalActivity =
        mockk<ExternalActivity>(relaxed = true) {
            every { this@mockk.id } returns id
            every { increaseViewCount() } just Runs
        }

    private fun createMockMember(id: Long): Member =
        mockk<Member>(relaxed = true) {
            every { this@mockk.id } returns id
        }

    private fun createMockHttpServletRequest(
        ip: String,
        userAgent: String,
    ): HttpServletRequest =
        mockk<HttpServletRequest>(relaxed = true) {
            every { remoteAddr } returns ip
            every { getHeader("User-Agent") } returns userAgent
        }
}
