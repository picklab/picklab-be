package picklab.backend.bookmark.application

import org.springframework.stereotype.Component
import picklab.backend.bookmark.domain.BookmarkService

@Component
class BookmarkUseCase(
    private val bookmarkService: BookmarkService,
) {
    fun getMyBookmarkedActivityIds(
        memberId: Long?,
        activityIds: List<Long>,
    ): Set<Long> =
        bookmarkService.getActivityIdsBookmarkedByMember(
            memberId = memberId,
            activityIds = activityIds,
        )
}
