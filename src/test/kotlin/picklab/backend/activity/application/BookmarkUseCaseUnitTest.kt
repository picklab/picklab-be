package picklab.backend.activity.application

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import picklab.backend.activity.application.model.ActivityView
import picklab.backend.activity.application.model.GetMyBookmarkListCondition
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.entity.ActivityBookmark
import picklab.backend.activity.domain.enums.ActivityBookmarkSortType
import picklab.backend.activity.domain.service.ActivityBookmarkService
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.activity.infrastructure.ActivityItem
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.MemberService
import picklab.backend.member.domain.entity.Member
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class BookmarkUseCaseUnitTest {
    @MockK
    lateinit var activityBookmarkService: ActivityBookmarkService

    @MockK
    lateinit var memberService: MemberService

    @MockK
    lateinit var activityService: ActivityService

    @MockK
    lateinit var activityQueryService: ActivityQueryService

    @InjectMockKs
    lateinit var bookmarkUseCase: BookmarkUseCase

    @Nested
    @DisplayName("createActivityBookmark 메소드 테스트")
    inner class CreateActivityBookmarkTest {
        @Test
        @DisplayName("활동 북마크를 성공적으로 생성한다")
        fun createActivityBookmark_success() {
            // given
            val memberId = 1L
            val activityId = 1L
            val mockMember = createMockMember(memberId)
            val mockActivity = createMockActivity(activityId)
            val mockActivityBookmark = createMockActivityBookmark(1L)

            every { memberService.findActiveMember(memberId) } returns mockMember
            every { activityService.mustFindById(activityId) } returns mockActivity
            every {
                activityBookmarkService.createActivityBookmark(
                    mockMember,
                    mockActivity,
                )
            } returns mockActivityBookmark

            // when
            bookmarkUseCase.createActivityBookmark(memberId, activityId)

            // then
            verifyOrder {
                memberService.findActiveMember(memberId)
                activityService.mustFindById(activityId)
                activityBookmarkService.createActivityBookmark(mockMember, mockActivity)
            }
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 북마크 생성 시 BusinessException(ErrorCode.INVALID_MEMBER) 예외가 발생한다")
        fun createActivityBookmark_memberNotFound() {
            // given
            val memberId = 999L
            val activityId = 1L

            every { memberService.findActiveMember(memberId) } throws BusinessException(ErrorCode.INVALID_MEMBER)

            // when
            val exception =
                assertThrows(BusinessException::class.java) {
                    bookmarkUseCase.createActivityBookmark(memberId, activityId)
                }

            // then
            assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_MEMBER)

            verify(exactly = 1) { memberService.findActiveMember(memberId) }
            verify(exactly = 0) { activityService.mustFindById(any()) }
            verify(exactly = 0) { activityBookmarkService.createActivityBookmark(any(), any()) }
        }

        @Test
        @DisplayName("존재하지 않는 활동 ID로 북마크 생성 시 BusinessException(ErrorCode.NOT_FOUND_ACTIVITY) 예외가 발생한다")
        fun createActivityBookmark_activityNotFound() {
            // given
            val memberId = 1L
            val activityId = 999L
            val mockMember = createMockMember(memberId)

            every { memberService.findActiveMember(memberId) } returns mockMember
            every { activityService.mustFindById(activityId) } throws BusinessException(ErrorCode.NOT_FOUND_ACTIVITY)

            // when
            val exception =
                assertThrows(BusinessException::class.java) {
                    bookmarkUseCase.createActivityBookmark(memberId, activityId)
                }

            // then
            assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND_ACTIVITY)

            verify(exactly = 1) { memberService.findActiveMember(memberId) }
            verify(exactly = 1) { activityService.mustFindById(activityId) }
            verify(exactly = 0) { activityBookmarkService.createActivityBookmark(any(), any()) }
        }

        @Test
        @DisplayName("이미 존재하는 북마크 생성 시 BusinessException(ErrorCode.ALREADY_EXISTS_ACTIVITY_BOOKMARK) 예외가 발생한다")
        fun createActivityBookmark_alreadyExists() {
            // given
            val memberId = 1L
            val activityId = 1L
            val mockMember = createMockMember(memberId)
            val mockActivity = createMockActivity(activityId)

            every { memberService.findActiveMember(memberId) } returns mockMember
            every { activityService.mustFindById(activityId) } returns mockActivity
            every {
                activityBookmarkService.createActivityBookmark(mockMember, mockActivity)
            } throws BusinessException(ErrorCode.ALREADY_EXISTS_ACTIVITY_BOOKMARK)

            // when
            val exception =
                assertThrows(BusinessException::class.java) {
                    bookmarkUseCase.createActivityBookmark(memberId, activityId)
                }

            // then
            assertThat(exception.errorCode).isEqualTo(ErrorCode.ALREADY_EXISTS_ACTIVITY_BOOKMARK)

            verifyOrder {
                memberService.findActiveMember(memberId)
                activityService.mustFindById(activityId)
                activityBookmarkService.createActivityBookmark(mockMember, mockActivity)
            }
        }
    }

    @Nested
    @DisplayName("removeActivityBookmark 메소드 테스트")
    inner class RemoveActivityBookmarkTest {
        @Test
        @DisplayName("활동 북마크를 성공적으로 삭제한다")
        fun removeActivityBookmark_success() {
            // given
            val memberId = 1L
            val activityId = 1L
            val mockMember = createMockMember(memberId)
            val mockActivity = createMockActivity(activityId)

            every { memberService.findActiveMember(memberId) } returns mockMember
            every { activityService.mustFindById(activityId) } returns mockActivity
            every { activityBookmarkService.removeActivityBookmark(mockMember, mockActivity) } just Runs

            // when
            bookmarkUseCase.removeActivityBookmark(memberId, activityId)

            // then
            verifyOrder {
                memberService.findActiveMember(memberId)
                activityService.mustFindById(activityId)
                activityBookmarkService.removeActivityBookmark(mockMember, mockActivity)
            }
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 북마크 삭제 시 BusinessException(ErrorCode.INVALID_MEMBER) 예외가 발생한다")
        fun removeActivityBookmark_memberNotFound() {
            // given
            val memberId = 999L
            val activityId = 1L

            every { memberService.findActiveMember(memberId) } throws BusinessException(ErrorCode.INVALID_MEMBER)

            // when
            val exception =
                assertThrows(BusinessException::class.java) {
                    bookmarkUseCase.removeActivityBookmark(memberId, activityId)
                }

            // then
            assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_MEMBER)

            verify(exactly = 1) { memberService.findActiveMember(memberId) }
            verify(exactly = 0) { activityService.mustFindById(any()) }
            verify(exactly = 0) { activityBookmarkService.removeActivityBookmark(any(), any()) }
        }

        @Test
        @DisplayName("존재하지 않는 북마크 삭제 시 BusinessException(ErrorCode.NOT_FOUND_ACTIVITY_BOOKMARK) 예외가 발생한다")
        fun removeActivityBookmark_bookmarkNotFound() {
            // given
            val memberId = 1L
            val activityId = 1L
            val mockMember = createMockMember(memberId)
            val mockActivity = createMockActivity(activityId)

            every { memberService.findActiveMember(memberId) } returns mockMember
            every { activityService.mustFindById(activityId) } returns mockActivity
            every {
                activityBookmarkService.removeActivityBookmark(mockMember, mockActivity)
            } throws BusinessException(ErrorCode.NOT_FOUND_ACTIVITY_BOOKMARK)

            // when
            val exception =
                assertThrows(BusinessException::class.java) {
                    bookmarkUseCase.removeActivityBookmark(memberId, activityId)
                }

            // then
            assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND_ACTIVITY_BOOKMARK)

            verifyOrder {
                memberService.findActiveMember(memberId)
                activityService.mustFindById(activityId)
                activityBookmarkService.removeActivityBookmark(mockMember, mockActivity)
            }
        }
    }

    @Nested
    @DisplayName("getBookmarks 메소드 테스트")
    inner class GetBookmarksTest {
        @Test
        @DisplayName("북마크 목록을 성공적으로 조회한다")
        fun getBookmarks_success() {
            // given
            val memberId = 1L
            val page = 0
            val size = 10
            val condition =
                GetMyBookmarkListCondition(
                    memberId = memberId,
                    activityTypes = null,
                    jobGroups = null,
                    recruitmentStatus = null,
                    sortType = ActivityBookmarkSortType.LATEST,
                    page = page,
                    size = size,
                )
            val mockMember = createMockMember(memberId)
            val mockActivityItems =
                listOf(
                    createMockActivityItem(1L),
                    createMockActivityItem(2L),
                )
            val mockPage: Page<ActivityView> = PageImpl(mockActivityItems, PageRequest.of(page, size), 2)

            every { memberService.findActiveMember(memberId) } returns mockMember
            every {
                activityQueryService.getBookmarkedActivityItems(memberId, condition, any())
            } returns mockPage

            // when
            val result = bookmarkUseCase.getBookmarks(condition)

            // then
            assertThat(result.items).hasSize(2)
            assertThat(result.items[0].isBookmarked).isTrue()
            assertThat(result.items[1].isBookmarked).isTrue()
            assertThat(result.page).isEqualTo(page + 1) // PageResponse는 1-based
            assertThat(result.size).isEqualTo(size)
            assertThat(result.totalElements).isEqualTo(2)

            verify(exactly = 1) { memberService.findActiveMember(memberId) }
            verify(exactly = 1) {
                activityQueryService.getBookmarkedActivityItems(memberId, condition, any())
            }
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 북마크 목록 조회 시 BusinessException(ErrorCode.INVALID_MEMBER) 예외가 발생한다")
        fun getBookmarks_memberNotFound() {
            // given
            val memberId = 999L
            val page = 0
            val size = 10
            val condition =
                GetMyBookmarkListCondition(
                    memberId = memberId,
                    activityTypes = null,
                    jobGroups = null,
                    recruitmentStatus = null,
                    sortType = ActivityBookmarkSortType.LATEST,
                    page = page,
                    size = size,
                )

            every { memberService.findActiveMember(memberId) } throws BusinessException(ErrorCode.INVALID_MEMBER)

            // when & then
            assertThatThrownBy {
                bookmarkUseCase.getBookmarks(condition)
            }.isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_MEMBER)

            verify(exactly = 1) { memberService.findActiveMember(memberId) }
            verify(exactly = 0) { activityQueryService.getBookmarkedActivityItems(any(), any(), any()) }
        }

        @Test
        @DisplayName("빈 북마크 목록을 조회한다")
        fun getBookmarks_emptyList() {
            // given
            val memberId = 1L
            val page = 0
            val size = 10
            val condition =
                GetMyBookmarkListCondition(
                    memberId = memberId,
                    activityTypes = null,
                    jobGroups = null,
                    recruitmentStatus = null,
                    sortType = ActivityBookmarkSortType.LATEST,
                    page = page,
                    size = size,
                )
            val mockMember = createMockMember(memberId)
            val emptyPage: Page<ActivityView> = PageImpl(emptyList(), PageRequest.of(0, 10), 0)

            every { memberService.findActiveMember(memberId) } returns mockMember
            every {
                activityQueryService.getBookmarkedActivityItems(memberId, condition, any())
            } returns emptyPage

            // when
            val result = bookmarkUseCase.getBookmarks(condition)

            // then
            assertThat(result.items).isEmpty()
            assertThat(result.totalElements).isEqualTo(0)
        }
    }

    private fun createMockMember(id: Long): Member =
        mockk<Member>(relaxed = true) {
            every { this@mockk.id } returns id
        }

    private fun createMockActivity(id: Long): Activity =
        mockk<Activity>(relaxed = true) {
            every { this@mockk.id } returns id
        }

    private fun createMockActivityItem(id: Long): ActivityItem =
        mockk<ActivityItem>(relaxed = true) {
            every { this@mockk.id } returns id
        }

    private fun createMockActivityBookmark(id: Long) =
        mockk<ActivityBookmark>(relaxed = true) {
            every { this@mockk.id } returns id
        }
}
