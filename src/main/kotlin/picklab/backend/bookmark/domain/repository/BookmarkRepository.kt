package picklab.backend.bookmark.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import picklab.backend.bookmark.domain.entity.Bookmark

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
}
