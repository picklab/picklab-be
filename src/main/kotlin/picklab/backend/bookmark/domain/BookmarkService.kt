package picklab.backend.bookmark.domain

import org.springframework.stereotype.Service
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.bookmark.domain.entity.Bookmark
import picklab.backend.bookmark.domain.repository.BookmarkRepository
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.entity.Member

@Service
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
) {
    fun getMyBookmarkedActivityIds(
        memberId: Long?,
        activityIds: List<Long>,
    ): Set<Long> =
        bookmarkRepository
            .findAllByMemberIdAndActivityIdIn(
                memberId = memberId,
                activityIds = activityIds,
            ).map { it.activity.id }
            .toSet()

    fun countByActivityId(activityId: Long) = bookmarkRepository.countByActivityId(activityId)

    fun existsByMemberIdAndActivityId(
        memberId: Long,
        activityId: Long,
    ) = bookmarkRepository.existsByMemberIdAndActivityId(
        memberId = memberId,
        activityId = activityId,
    )

    fun createActivityBookmark(
        member: Member,
        activity: Activity,
    ): Bookmark {
        if (bookmarkRepository.existsByMemberAndActivity(member, activity)) {
            throw BusinessException(ErrorCode.ALREADY_EXISTS_ACTIVITY_BOOKMARK)
        }

        return bookmarkRepository.save(
            Bookmark(
                member = member,
                activity = activity,
            ),
        )
    }

    fun removeActivityBookmark(
        member: Member,
        activity: Activity,
    ) {
        if (!bookmarkRepository.existsByMemberAndActivity(member, activity)) {
            throw BusinessException(ErrorCode.NOT_FOUND_ACTIVITY_BOOKMARK)
        }

        bookmarkRepository.deleteByMemberAndActivity(member, activity)
    }
}
