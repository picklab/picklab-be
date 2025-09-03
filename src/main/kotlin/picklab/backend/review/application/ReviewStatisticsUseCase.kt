package picklab.backend.review.application

import org.springframework.stereotype.Component
import picklab.backend.activity.domain.service.ActivityService
import picklab.backend.member.domain.MemberService
import picklab.backend.review.application.query.model.JobRelevanceStatisticsView
import picklab.backend.review.application.query.model.SatisfactionStatisticsView
import picklab.backend.review.application.service.ReviewStatisticsQueryService

@Component
class ReviewStatisticsUseCase(
    private val activityService: ActivityService,
    private val reviewStatisticsQueryService: ReviewStatisticsQueryService,
    private val memberService: MemberService,
) {
    fun getJobRelevanceStatistics(activityId: Long): JobRelevanceStatisticsView {
        val activity = activityService.mustFindById(activityId)
        return reviewStatisticsQueryService.getJobRelevanceStatistics(activity.id)
    }

    fun getSatisfactionStatistics(
        memberId: Long?,
        activityId: Long,
    ): List<SatisfactionStatisticsView> {
        val activity = activityService.mustFindById(activityId)
        val jobCategoryIds =
            memberId?.let {
                memberService.findMyInterestedJobCategoryIds(memberService.findActiveMember(it))
            } ?: emptyList()
        return reviewStatisticsQueryService.getSatisfactionStatistics(activity.id, jobCategoryIds)
    }
}
