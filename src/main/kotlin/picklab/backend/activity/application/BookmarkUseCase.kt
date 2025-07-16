package picklab.backend.activity.application

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import picklab.backend.activity.domain.service.ActivityBookmarkService
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.member.domain.MemberService

@Component
class BookmarkUseCase(
    private val activityBookmarkService: ActivityBookmarkService,
    private val memberService: MemberService,
    private val activityService: ActivityService,
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
}
