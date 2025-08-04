package picklab.backend.activity.application

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import picklab.backend.activity.application.model.GetMyBookmarkListCommand

@Service
class ActivityBookmarkQueryService(
    private val activityBookmarkQueryRepository: ActivityBookmarkQueryRepository,
) {
    fun getBookmarkedActivityIds(
        memberId: Long,
        queryData: GetMyBookmarkListCommand,
        pageable: PageRequest,
    ): List<Long> = activityBookmarkQueryRepository.findBookmarkedActivityIds(memberId, queryData, pageable)
    
    fun countBookmarkedActivities(
        memberId: Long,
        queryData: GetMyBookmarkListCommand,
    ): Long = activityBookmarkQueryRepository.countBookmarkedActivities(memberId, queryData)
}
