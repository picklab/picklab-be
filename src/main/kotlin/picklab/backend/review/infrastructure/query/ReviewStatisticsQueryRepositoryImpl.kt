package picklab.backend.review.infrastructure.query

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import picklab.backend.job.domain.entity.QJobCategory.Companion.jobCategory
import picklab.backend.job.domain.enums.JobDetail
import picklab.backend.job.domain.enums.JobGroup
import picklab.backend.review.application.query.ReviewStatisticsQueryRepository
import picklab.backend.review.application.query.model.JobRelevanceStatisticsView
import picklab.backend.review.application.query.model.SatisfactionStatisticsView
import picklab.backend.review.domain.entity.QReview.Companion.review
import picklab.backend.review.domain.enums.ReviewApprovalStatus
import picklab.backend.review.infrastructure.query.projection.JobRelevanceStatisticsItem
import picklab.backend.review.infrastructure.query.projection.QSatisfactionStatisticsItem
import picklab.backend.review.infrastructure.query.projection.SatisfactionStatisticsItem

@Repository
class ReviewStatisticsQueryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ReviewStatisticsQueryRepository {
    override fun findJobRelevanceStatistics(activityId: Long): JobRelevanceStatisticsView {
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

    override fun findSatisfactionStatistics(
        activityId: Long,
        jobCategoryIds: List<Long>,
    ): List<SatisfactionStatisticsView> {
        val results = mutableListOf<SatisfactionStatisticsItem>()
        val overall =
            jpaQueryFactory
                .select(
                    QSatisfactionStatisticsItem(
                        Expressions.nullExpression(JobGroup::class.java),
                        Expressions.nullExpression(JobDetail::class.java),
                        review.overallScore.avg(),
                        review.infoScore.avg(),
                        review.difficultyScore.avg(),
                        review.benefitScore.avg(),
                    ),
                ).from(review)
                .where(
                    review.activity.id
                        .eq(activityId)
                        .and(review.reviewApprovalStatus.eq(ReviewApprovalStatus.APPROVED)),
                ).fetchOne()

        // 빈 데이터 Early Return
        if (overall == null) {
            return listOf(
                SatisfactionStatisticsItem(
                    jobGroup = null,
                    jobDetail = null,
                    avgTotalScore = 0.0,
                    avgJobExperienceScore = 0.0,
                    avgActivityIntensityScore = 0.0,
                    avgBenefitScore = 0.0,
                ),
            )
        }
        results.add(overall)

        if (jobCategoryIds.isNotEmpty()) {
            val detailScores =
                jpaQueryFactory
                    .select(
                        QSatisfactionStatisticsItem(
                            jobCategory.jobGroup,
                            jobCategory.jobDetail,
                            review.overallScore.avg(),
                            review.infoScore.avg(),
                            review.difficultyScore.avg(),
                            review.benefitScore.avg(),
                        ),
                    ).from(review)
                    .join(review.jobCategory, jobCategory)
                    .where(
                        review.activity.id.eq(activityId),
                        review.reviewApprovalStatus.eq(ReviewApprovalStatus.APPROVED),
                        jobCategory.id.`in`(jobCategoryIds),
                    ).groupBy(jobCategory.jobGroup, jobCategory.jobDetail)
                    .fetch()
            results.addAll(detailScores)
        }
        return results
    }
}
