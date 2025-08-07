package picklab.backend.review.infrastructure.query

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.review.application.query.ReviewStatisticsQueryRepository
import picklab.backend.review.application.query.model.JobRelevanceStatisticsItem
import picklab.backend.review.domain.entity.QReview.Companion.review
import picklab.backend.review.domain.enums.ReviewApprovalStatus

@Repository
class ReviewStatisticsQueryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ReviewStatisticsQueryRepository {
    override fun findJobRelevanceStatistics(activityId: Long): JobRelevanceStatisticsItem {
        val results =
            jpaQueryFactory
                .select(review.jobCategory.jobGroup, review.jobRelevanceScore.avg())
                .from(review)
                .where(
                    review.activity.id
                        .eq(activityId)
                        .and(review.reviewApprovalStatus.eq(ReviewApprovalStatus.APPROVED)),
                ).groupBy(review.jobCategory.jobGroup)
                .fetch()

        val scoreMap = mutableMapOf<JobGroup, Double>()

        results.forEach { tuple ->
            tuple.get(review.jobCategory.jobGroup)?.let { jobGroup ->
                val avgScore = tuple.get(review.jobRelevanceScore.avg()) ?: 0.0
                scoreMap[jobGroup] = avgScore
            }
        }

        return JobRelevanceStatisticsItem(
            planningAvgScore = scoreMap[JobGroup.PLANNING] ?: 0.0,
            developmentAvgScore = scoreMap[JobGroup.DEVELOPMENT] ?: 0.0,
            marketingAvgScore = scoreMap[JobGroup.MARKETING] ?: 0.0,
            aiAvgScore = scoreMap[JobGroup.AI] ?: 0.0,
            designAvgScore = scoreMap[JobGroup.DESIGN] ?: 0.0,
        )
    }
}
