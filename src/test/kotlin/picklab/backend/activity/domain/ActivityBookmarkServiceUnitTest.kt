package picklab.backend.activity.domain

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.entity.ActivityBookmark
import picklab.backend.activity.domain.repository.ActivityBookmarkRepository
import picklab.backend.activity.domain.service.ActivityBookmarkService
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.entity.Member
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class ActivityBookmarkServiceUnitTest {
    @MockK
    private lateinit var activityBookmarkRepository: ActivityBookmarkRepository

    @InjectMockKs
    private lateinit var activityBookmarkService: ActivityBookmarkService

    @Nested
    @DisplayName("getMyBookmarkedActivityIds 메소드 테스트")
    inner class GetMyBookmarkedActivityIdsTest {
        @Test
        @DisplayName("로그인한 사용자의 북마크된 활동 ID 목록을 반환한다")
        fun getMyBookmarkedActivityIds_success() {
            // given
            val memberId = 1L
            val activityIds = listOf(1L, 2L, 3L)
            val mockBookmarks =
                listOf(
                    createMockActivityBookmark(1L),
                    createMockActivityBookmark(3L),
                )

            every {
                activityBookmarkRepository.findAllByMemberIdAndActivityIdIn(memberId, activityIds)
            } returns mockBookmarks

            // when
            val result = activityBookmarkService.getMyBookmarkedActivityIds(memberId, activityIds)

            // then
            assertThat(result).containsExactlyInAnyOrder(1L, 3L)
            verify(exactly = 1) {
                activityBookmarkRepository.findAllByMemberIdAndActivityIdIn(memberId, activityIds)
            }
        }

        @Test
        @DisplayName("로그인하지 않은 사용자인 경우 빈 데이터를 반환한다")
        fun getMyBookmarkedActivityIds_nullMemberId() {
            // given
            val memberId = null
            val activityIds = listOf(1L, 2L, 3L)

            every {
                activityBookmarkRepository.findAllByMemberIdAndActivityIdIn(null, activityIds)
            } returns emptyList()

            // when
            val result = activityBookmarkService.getMyBookmarkedActivityIds(memberId, activityIds)

            // then
            assertThat(result).isEmpty()
            verify(exactly = 1) {
                activityBookmarkRepository.findAllByMemberIdAndActivityIdIn(null, activityIds)
            }
        }

        @Test
        @DisplayName("빈 활동 ID 목록이 주어진 경우 빈 데이터를 반환한다")
        fun getMyBookmarkedActivityIds_emptyActivityIds() {
            // given
            val memberId = 1L
            val activityIds = emptyList<Long>()

            every {
                activityBookmarkRepository.findAllByMemberIdAndActivityIdIn(memberId, activityIds)
            } returns emptyList()

            // when
            val result = activityBookmarkService.getMyBookmarkedActivityIds(memberId, activityIds)

            // then
            assertThat(result).isEmpty()
            verify(exactly = 1) {
                activityBookmarkRepository.findAllByMemberIdAndActivityIdIn(memberId, activityIds)
            }
        }
    }

    @Nested
    @DisplayName("countByActivityId 메소드 테스트")
    inner class CountByActivityIdTest {
        @Test
        @DisplayName("특정 활동의 북마크 수를 반환한다")
        fun countByActivityId_success() {
            // given
            val activityId = 1L
            val expectedCount = 5L

            every { activityBookmarkRepository.countByActivityId(activityId) } returns expectedCount

            // when
            val result = activityBookmarkService.countByActivityId(activityId)

            // then
            assertThat(result).isEqualTo(expectedCount)
            verify(exactly = 1) { activityBookmarkRepository.countByActivityId(activityId) }
        }
    }

    @Nested
    @DisplayName("existsByMemberIdAndActivityId 메소드 테스트")
    inner class ExistsByMemberIdAndActivityIdTest {
        @Test
        @DisplayName("북마크가 존재하는 경우 true를 반환한다")
        fun existsByMemberIdAndActivityId_exists() {
            // given
            val memberId = 1L
            val activityId = 1L

            every {
                activityBookmarkRepository.existsByMemberIdAndActivityId(memberId, activityId)
            } returns true

            // when
            val result = activityBookmarkService.existsByMemberIdAndActivityId(memberId, activityId)

            // then
            assertThat(result).isTrue()
            verify(exactly = 1) {
                activityBookmarkRepository.existsByMemberIdAndActivityId(memberId, activityId)
            }
        }

        @Test
        @DisplayName("북마크가 존재하지 않는 경우 false를 반환한다")
        fun existsByMemberIdAndActivityId_notExists() {
            // given
            val memberId = 1L
            val activityId = 1L

            every {
                activityBookmarkRepository.existsByMemberIdAndActivityId(memberId, activityId)
            } returns false

            // when
            val result = activityBookmarkService.existsByMemberIdAndActivityId(memberId, activityId)

            // then
            assertThat(result).isFalse()
            verify(exactly = 1) {
                activityBookmarkRepository.existsByMemberIdAndActivityId(memberId, activityId)
            }
        }
    }

    @Nested
    @DisplayName("createActivityBookmark 메소드 테스트")
    inner class CreateActivityBookmarkTest {
        @Test
        @DisplayName("새로운 활동 북마크를 성공적으로 생성한다")
        fun createActivityBookmark_success() {
            // given
            val member = createMockMember(1L)
            val activity = createMockActivity(1L)
            val expectedBookmark = createMockActivityBookmark(1L)

            every { activityBookmarkRepository.existsByMemberAndActivity(member, activity) } returns false
            every { activityBookmarkRepository.save(any<ActivityBookmark>()) } returns expectedBookmark

            // when
            val result = activityBookmarkService.createActivityBookmark(member, activity)

            // then
            assertThat(result).isEqualTo(expectedBookmark)
            verify(exactly = 1) { activityBookmarkRepository.existsByMemberAndActivity(member, activity) }
            verify(exactly = 1) { activityBookmarkRepository.save(any<ActivityBookmark>()) }
        }

        @Test
        @DisplayName("이미 존재하는 북마크를 생성하려고 하면 예외가 발생한다")
        fun createActivityBookmark_alreadyExists() {
            // given
            val member = createMockMember(1L)
            val activity = createMockActivity(1L)

            every { activityBookmarkRepository.existsByMemberAndActivity(member, activity) } returns true

            // when & then
            assertThatThrownBy {
                activityBookmarkService.createActivityBookmark(member, activity)
            }.isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_EXISTS_ACTIVITY_BOOKMARK)

            verify(exactly = 1) { activityBookmarkRepository.existsByMemberAndActivity(member, activity) }
            verify(exactly = 0) { activityBookmarkRepository.save(any<ActivityBookmark>()) }
        }
    }

    @Nested
    @DisplayName("removeActivityBookmark 메소드 테스트")
    inner class RemoveActivityBookmarkTest {
        @Test
        @DisplayName("기존 활동 북마크를 성공적으로 삭제한다")
        fun removeActivityBookmark_success() {
            // given
            val member = createMockMember(1L)
            val activity = createMockActivity(1L)

            every { activityBookmarkRepository.existsByMemberAndActivity(member, activity) } returns true
            every { activityBookmarkRepository.deleteByMemberAndActivity(member, activity) } just Runs

            // when
            activityBookmarkService.removeActivityBookmark(member, activity)

            // then
            verify(exactly = 1) { activityBookmarkRepository.existsByMemberAndActivity(member, activity) }
            verify(exactly = 1) { activityBookmarkRepository.deleteByMemberAndActivity(member, activity) }
        }

        @Test
        @DisplayName("존재하지 않는 북마크를 삭제하려고 하면 예외가 발생한다")
        fun removeActivityBookmark_notExists() {
            // given
            val member = createMockMember(1L)
            val activity = createMockActivity(1L)

            every { activityBookmarkRepository.existsByMemberAndActivity(member, activity) } returns false

            // when & then
            assertThatThrownBy {
                activityBookmarkService.removeActivityBookmark(member, activity)
            }.isInstanceOf(BusinessException::class.java)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_ACTIVITY_BOOKMARK)

            verify(exactly = 1) { activityBookmarkRepository.existsByMemberAndActivity(member, activity) }
            verify(exactly = 0) { activityBookmarkRepository.deleteByMemberAndActivity(member, activity) }
        }
    }

    // Mock 객체 생성 헬퍼 메소드들
    private fun createMockMember(id: Long): Member =
        mockk<Member>(relaxed = true) {
            every { this@mockk.id } returns id
        }

    private fun createMockActivity(id: Long): Activity =
        mockk<Activity>(relaxed = true) {
            every { this@mockk.id } returns id
        }

    private fun createMockActivityBookmark(activityId: Long): ActivityBookmark =
        mockk<ActivityBookmark>(relaxed = true) {
            every { activity.id } returns activityId
        }
}
