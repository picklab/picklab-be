package picklab.backend.bookmark.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.activity.domain.entity.Activity
import picklab.backend.bookmark.domain.entity.Bookmark
import picklab.backend.member.domain.entity.Member

interface BookmarkRepository : JpaRepository<Bookmark, Long> {
    fun findAllByMemberIdAndActivityIdIn(
        memberId: Long?,
        activityIds: List<Long>,
    ): List<Bookmark>

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
}
