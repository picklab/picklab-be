package picklab.backend.bookmark.domain

import org.springframework.stereotype.Service
import picklab.backend.bookmark.domain.repository.BookmarkRepository

@Service
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
) {
    fun getActivityIdsBookmarkedByMember(
        memberId: Long?,
        activityIds: List<Long>,
    ): Set<Long> =
        bookmarkRepository
            .findAllByMemberIdAndActivityIdIn(
                memberId = memberId,
                activityIds = activityIds,
            ).map { it.activity.id }
            .toSet()
}
