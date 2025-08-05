package picklab.backend.activity.application

import org.springframework.data.domain.PageRequest
import picklab.backend.activity.application.model.GetMyBookmarkListCondition

interface ActivityBookmarkQueryRepository {
    fun findBookmarkedActivityIds(
        memberId: Long,
        queryData: GetMyBookmarkListCondition,
        pageable: PageRequest,
    ): List<Long>

    fun countBookmarkedActivities(
        memberId: Long,
        queryData: GetMyBookmarkListCondition,
    ): Long
}
