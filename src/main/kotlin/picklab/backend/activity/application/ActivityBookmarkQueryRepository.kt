package picklab.backend.activity.application

import org.springframework.data.domain.PageRequest
import picklab.backend.activity.application.model.GetMyBookmarkListCommand

interface ActivityBookmarkQueryRepository {
    fun findBookmarkedActivityIds(
        memberId: Long,
        queryData: GetMyBookmarkListCommand,
        pageable: PageRequest,
    ): List<Long>
    
    fun countBookmarkedActivities(
        memberId: Long,
        queryData: GetMyBookmarkListCommand,
    ): Long
}
