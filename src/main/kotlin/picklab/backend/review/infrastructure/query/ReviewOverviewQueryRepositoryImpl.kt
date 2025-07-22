package picklab.backend.review.infrastructure.query

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import picklab.backend.activity.domain.entity.QActivity.Companion.activity
import picklab.backend.review.application.query.ReviewOverviewQueryRepository
import picklab.backend.review.application.query.model.MyReviewListItem
import picklab.backend.review.application.query.model.QMyReviewListItem
import picklab.backend.review.domain.entity.QReview.Companion.review

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
