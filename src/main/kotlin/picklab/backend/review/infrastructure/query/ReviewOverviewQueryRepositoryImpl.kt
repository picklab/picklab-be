package picklab.backend.review.infrastructure.query

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import picklab.backend.activity.domain.entity.QActivity.Companion.activity
import picklab.backend.job.domain.entity.QJobCategory.Companion.jobCategory
import picklab.backend.member.domain.entity.QMember.Companion.member
import picklab.backend.participation.domain.entity.QActivityParticipation.Companion.activityParticipation
import picklab.backend.review.application.model.ActivityReviewListQueryRequest
import picklab.backend.review.application.query.ReviewOverviewQueryRepository
import picklab.backend.review.application.query.model.ActivityReviewListItem
import picklab.backend.review.application.query.model.MyReviewListItem
import picklab.backend.review.application.query.model.QActivityReviewListItem
import picklab.backend.review.application.query.model.QMyReviewListItem
import picklab.backend.review.domain.entity.QReview.Companion.review
import picklab.backend.review.domain.enums.ReviewApprovalStatus

@Repository
class ReviewOverviewQueryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ReviewOverviewQueryRepository {
    override fun findMyReviews(
        memberId: Long,
        pageable: Pageable,
    ): Page<MyReviewListItem> {
        val content =
            jpaQueryFactory
                .select(
                    QMyReviewListItem(
                        review.id,
                        activity.title,
                        activity.organizer,
                        activity.activityType,
                        review.createdAt,
                        review.reviewApprovalStatus,
                    ),
                ).from(review)
                .join(review.activity, activity)
                .where(review.member.id.eq(memberId))
                .orderBy(
                    * pageable.sort
                        .map { toOrderSpecifier(it) }
                        .toList()
                        .toTypedArray(),
                ).offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()
        val totalCount =
            jpaQueryFactory
                .select(review.count())
                .from(review)
                .where(review.member.id.eq(memberId))
                .fetchOne() ?: 0L

        return PageImpl(content, pageable, totalCount)
    }

    override fun findActivityReviewsWithFilter(
        request: ActivityReviewListQueryRequest,
        activityId: Long,
        pageable: Pageable,
    ): Page<ActivityReviewListItem> {
        val builder = BooleanBuilder()
        builder.and(activity.id.eq(activityId))
        request.rating?.let {
            builder.and(review.overallScore.eq(it))
        }
        request.jobGroup?.takeIf { it.isNotEmpty() }?.let {
            builder.and(jobCategory.jobGroup.`in`(it))
        }
        request.jobDetail?.takeIf { it.isNotEmpty() }?.let {
            builder.and(jobCategory.jobDetail.`in`(it))
        }
        request.status?.let {
            builder.and(activityParticipation.progressStatus.eq(it))
        }
        // 승인 상태의 리뷰만 노출
        builder.and(review.reviewApprovalStatus.eq(ReviewApprovalStatus.APPROVED))

        val content =
            jpaQueryFactory
                .select(
                    QActivityReviewListItem(
                        review.id,
                        review.overallScore,
                        review.infoScore,
                        review.difficultyScore,
                        review.benefitScore,
                        jobCategory.jobGroup,
                        jobCategory.jobDetail,
                        activityParticipation.createdAt,
                        activityParticipation.progressStatus,
                        review.summary,
                        review.strength,
                        review.weakness,
                        review.tips,
                    ),
                ).from(review)
                .join(review.activity, activity)
                .join(review.member, member)
                .join(review.jobCategory, jobCategory)
                .join(activityParticipation)
                .on(
                    activityParticipation.member.id
                        .eq(member.id)
                        .and(activityParticipation.activity.id.eq(activity.id)),
                ).where(builder)
                .orderBy(
                    * pageable.sort
                        .map { toOrderSpecifier(it) }
                        .toList()
                        .toTypedArray(),
                ).offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        val totalCount =
            jpaQueryFactory
                .select(review.count())
                .from(review)
                .join(review.activity, activity)
                .join(review.member, member)
                .join(review.jobCategory, jobCategory)
                .join(activityParticipation)
                .on(
                    activityParticipation.member.id
                        .eq(member.id)
                        .and(activityParticipation.activity.id.eq(activity.id)),
                ).where(builder)
                .fetchOne() ?: 0L

        return PageImpl(content, pageable, totalCount)
    }

    /**
     * Spring Pageable의 Sort.Order → QueryDSL OrderSpecifier 로 변환
     */
    private fun toOrderSpecifier(sort: Sort.Order): OrderSpecifier<*> =
        when (sort.property) {
            "createdAt" -> if (sort.isAscending) review.createdAt.asc() else review.createdAt.desc()
            "title" -> if (sort.isAscending) activity.title.asc() else activity.title.desc()
            else -> review.id.desc()
        }
}
