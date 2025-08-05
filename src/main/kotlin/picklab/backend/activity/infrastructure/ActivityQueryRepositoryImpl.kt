package picklab.backend.activity.infrastructure

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import picklab.backend.activity.application.ActivityQueryRepository
import picklab.backend.activity.application.model.ActivityItem
import picklab.backend.activity.application.model.QActivityItem
import picklab.backend.activity.domain.entity.QActivity
import picklab.backend.activity.domain.entity.QActivityBookmark
import picklab.backend.activity.domain.entity.QActivityJobCategory
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.job.domain.entity.QJobCategory

@Repository
class ActivityQueryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ActivityQueryRepository {
    override fun findAllByMemberJobRecommendation(
        jobIds: List<Long>,
        pageable: PageRequest,
    ): Page<ActivityItem> {
        // 1. 조건에 맞는 activityId 들을 조회
        val activityIds =
            jpaQueryFactory
                .select(QActivity.activity.id)
                .from(QActivity.activity)
                .innerJoin(QActivityJobCategory.activityJobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.activity.id
                        .eq(QActivity.activity.id),
                ).innerJoin(QJobCategory.jobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.jobCategory.id
                        .eq(QJobCategory.jobCategory.id),
                ).where(
                    QActivity.activity.deletedAt.isNull
                        .and(QJobCategory.jobCategory.id.`in`(jobIds)),
                ).groupBy(QActivity.activity.id)
                .orderBy(
                    QActivity.activity.viewCount
                        .add(
                            JPAExpressions
                                .select(QActivityBookmark.activityBookmark.count().coalesce(0L))
                                .from(QActivityBookmark.activityBookmark)
                                .where(QActivityBookmark.activityBookmark.activity.eq(QActivity.activity)),
                        ).desc(),
                    QActivity.activity.createdAt.desc(),
                ).offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        if (activityIds.isEmpty()) {
            return PageImpl(emptyList(), pageable, 0)
        }

        // 2. 1번에서 구한 activityId 들을 이용하여 ActivityItem을 조회
        val items =
            jpaQueryFactory
                .selectFrom(QActivity.activity)
                .leftJoin(QActivityJobCategory.activityJobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.activity.id
                        .eq(QActivity.activity.id),
                ).leftJoin(QJobCategory.jobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.jobCategory.id
                        .eq(QJobCategory.jobCategory.id),
                ).where(QActivity.activity.id.`in`(activityIds))
                .orderBy(
                    QActivity.activity.viewCount
                        .add(
                            JPAExpressions
                                .select(QActivityBookmark.activityBookmark.count().coalesce(0L))
                                .from(QActivityBookmark.activityBookmark)
                                .where(QActivityBookmark.activityBookmark.activity.eq(QActivity.activity)),
                        ).desc(),
                    QActivity.activity.createdAt.desc(),
                ).transform(
                    GroupBy.groupBy(QActivity.activity.id).list(
                        QActivityItem(
                            QActivity.activity.id,
                            QActivity.activity.title,
                            QActivity.activity.organizer.stringValue(),
                            QActivity.activity.startDate,
                            QActivity.activity.activityType,
                            GroupBy.list(QJobCategory.jobCategory.jobDetail.stringValue()),
                            QActivity.activity.activityThumbnailUrl,
                        ),
                    ),
                )

        // 3. 페이징 기능을 위해 토탈 count 를 구하는 쿼리
        val count =
            jpaQueryFactory
                .select(QActivity.activity.id.countDistinct())
                .from(QActivity.activity)
                .leftJoin(QActivityJobCategory.activityJobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.activity.id
                        .eq(QActivity.activity.id),
                ).leftJoin(QJobCategory.jobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.jobCategory.id
                        .eq(QJobCategory.jobCategory.id),
                ).where(
                    QActivity.activity.deletedAt.isNull
                        .and(QJobCategory.jobCategory.id.`in`(jobIds)),
                ).fetchOne() ?: 0L

        return PageImpl(items, pageable, count)
    }

    override fun findPopularActivities(pageable: PageRequest): Page<ActivityItem> {
        val condition =
            BooleanBuilder().apply {
                and(QActivity.activity.status.eq(RecruitmentStatus.OPEN))
                and(QActivity.activity.deletedAt.isNull)
            }

        val orderBy =
            listOf(
                QActivity.activity.viewCount
                    .add(QActivityBookmark.activityBookmark.count().coalesce(0L))
                    .desc(),
                QActivity.activity.createdAt.desc(),
            )

        val popularActivityIds =
            jpaQueryFactory
                .select(QActivity.activity.id)
                .from(QActivity.activity)
                .leftJoin(QActivityBookmark.activityBookmark)
                .on(QActivityBookmark.activityBookmark.activity.eq(QActivity.activity))
                .where(condition)
                .groupBy(QActivity.activity.id)
                .orderBy(*orderBy.toTypedArray())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        if (popularActivityIds.isEmpty()) {
            return PageImpl(emptyList(), pageable, 0)
        }

        val items =
            jpaQueryFactory
                .from(QActivity.activity)
                .leftJoin(QActivityJobCategory.activityJobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.activity.id
                        .eq(QActivity.activity.id),
                ).leftJoin(QJobCategory.jobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.jobCategory.id
                        .eq(QJobCategory.jobCategory.id),
                ).where(QActivity.activity.id.`in`(popularActivityIds))
                .transform(
                    GroupBy.groupBy(QActivity.activity.id).list(
                        QActivityItem(
                            QActivity.activity.id,
                            QActivity.activity.title,
                            QActivity.activity.organizer.stringValue(),
                            QActivity.activity.startDate,
                            QActivity.activity.activityType,
                            GroupBy.list(QJobCategory.jobCategory.jobDetail.stringValue()),
                            QActivity.activity.activityThumbnailUrl,
                        ),
                    ),
                )

        val count =
            jpaQueryFactory
                .select(QActivity.activity.id.countDistinct())
                .from(QActivity.activity)
                .leftJoin(QActivityBookmark.activityBookmark)
                .on(QActivityBookmark.activityBookmark.activity.eq(QActivity.activity))
                .where(condition)
                .fetchOne() ?: 0L

        return PageImpl(items, pageable, count)
    }
}
