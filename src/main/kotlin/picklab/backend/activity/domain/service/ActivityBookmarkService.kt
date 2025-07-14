package picklab.backend.activity.domain.service

import org.springframework.stereotype.Service
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.entity.ActivityBookmark
import picklab.backend.activity.domain.repository.ActivityBookmarkRepository
import picklab.backend.common.model.BusinessException
import picklab.backend.common.model.ErrorCode
import picklab.backend.member.domain.entity.Member

@Service
class ActivityBookmarkService(
    private val activityBookmarkRepository: ActivityBookmarkRepository,
) {
    fun getMyBookmarkedActivityIds(
        memberId: Long?,
        activityIds: List<Long>,
    ): Set<Long> =
        activityBookmarkRepository
            .findAllByMemberIdAndActivityIdIn(
                memberId = memberId,
                activityIds = activityIds,
            ).map { it.activity.id }
            .toSet()

    fun countByActivityId(activityId: Long) = activityBookmarkRepository.countByActivityId(activityId)

    fun existsByMemberIdAndActivityId(
        memberId: Long,
        activityId: Long,
    ) = activityBookmarkRepository.existsByMemberIdAndActivityId(
        memberId = memberId,
        activityId = activityId,
    )

    fun createActivityBookmark(
        member: Member,
        activity: Activity,
    ): ActivityBookmark {
        if (activityBookmarkRepository.existsByMemberAndActivity(member, activity)) {
            throw BusinessException(ErrorCode.ALREADY_EXISTS_ACTIVITY_BOOKMARK)
        }

        return activityBookmarkRepository.save(
            ActivityBookmark(
                member = member,
                activity = activity,
            ),
        )
    }

    fun removeActivityBookmark(
        member: Member,
        activity: Activity,
    ) {
        if (!activityBookmarkRepository.existsByMemberAndActivity(member, activity)) {
            throw BusinessException(ErrorCode.NOT_FOUND_ACTIVITY_BOOKMARK)
        }

        activityBookmarkRepository.deleteByMemberAndActivity(member, activity)
    }
}
