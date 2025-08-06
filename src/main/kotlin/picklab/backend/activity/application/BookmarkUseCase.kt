package picklab.backend.activity.application

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.application.model.ActivityItemWithBookmark
import picklab.backend.activity.application.model.GetMyBookmarkListCondition
import picklab.backend.activity.domain.service.ActivityBookmarkService
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.common.model.PageResponse
import picklab.backend.member.domain.MemberService

@Component
class BookmarkUseCase(
    private val activityBookmarkService: ActivityBookmarkService,
    private val memberService: MemberService,
    private val activityService: ActivityService,
    private val activityQueryService: ActivityQueryService,
) {
    @Transactional
    fun createActivityBookmark(
        memberId: Long,
        activityId: Long,
    ) {
        val member = memberService.findActiveMember(memberId)
        val activity = activityService.mustFindById(activityId)

        activityBookmarkService.createActivityBookmark(member, activity)
    }

    @Transactional
    fun removeActivityBookmark(
        memberId: Long,
        activityId: Long,
    ) {
        val member = memberService.findActiveMember(memberId)
        val activity = activityService.mustFindById(activityId)

        activityBookmarkService.removeActivityBookmark(member, activity)
    }

    @Transactional(readOnly = true)
    fun getBookmarks(command: GetMyBookmarkListCondition): PageResponse<ActivityItemWithBookmark> {
        val member = memberService.findActiveMember(command.memberId)
        val pageable = PageRequest.of(command.page, command.size)

        val bookmarkedActivityPage = activityQueryService.getBookmarkedActivityItems(member.id, command, pageable)

        val itemPage =
            bookmarkedActivityPage.map {
                ActivityItemWithBookmark.from(
                    item = it,
                    isBookmarked = true,
                )
            }

        return PageResponse.from(itemPage)
    }
}
