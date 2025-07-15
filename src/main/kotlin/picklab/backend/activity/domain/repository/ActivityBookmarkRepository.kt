package picklab.backend.activity.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.activity.domain.entity.ActivityBookmark
import picklab.backend.member.domain.entity.Member

interface ActivityBookmarkRepository : JpaRepository<ActivityBookmark, Long> {
    fun findAllByMemberIdAndActivityIdIn(
        memberId: Long?,
        activityIds: List<Long>,
    ): List<ActivityBookmark>

    fun countByActivityId(activityId: Long): Long

    fun existsByMemberIdAndActivityId(
        memberId: Long,
        activityId: Long,
    ): Boolean

    fun existsByMemberAndActivity(
        member: Member,
        activity: Activity,
    ): Boolean

    fun deleteByMemberAndActivity(
        member: Member,
        activity: Activity,
    )

    fun findAllByActivityId(id: Long) : List<ActivityBookmark>
}
