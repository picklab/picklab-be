package picklab.backend.bookmark.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.bookmark.domain.BookmarkService
import picklab.backend.member.domain.MemberService

@Component
class BookmarkUseCase(
    private val bookmarkService: BookmarkService,
    private val memberService: MemberService,
    private val activityService: ActivityService,
) {
    fun getMyBookmarkedActivityIds(
        memberId: Long?,
        activityIds: List<Long>,
    ): Set<Long> =
        bookmarkService.getActivityIdsBookmarkedByMember(
            memberId = memberId,
            activityIds = activityIds,
        )

    fun getActivityBookmarkCount(activityId: Long) = bookmarkService.countByActivityId(activityId)

    fun getMyBookmarkedActivityId(
        memberId: Long,
        activityId: Long,
    ): Boolean? =
        bookmarkService.existsByMemberIdAndActivityId(
            memberId = memberId,
            activityId = activityId,
        )

    @Transactional
    fun createActivityBookmark(
        memberId: Long,
        activityId: Long,
    ) {
        val member = memberService.findActiveMember(memberId)
        val activity = activityService.mustFindActiveActivity(activityId)

        bookmarkService.createActivityBookmark(member, activity)
    }

    @Transactional
    fun removeActivityBookmark(
        memberId: Long,
        activityId: Long,
    ) {
        val member = memberService.findActiveMember(memberId)
        val activity = activityService.mustFindActiveActivity(activityId)

        bookmarkService.removeActivityBookmark(member, activity)
    }
}
