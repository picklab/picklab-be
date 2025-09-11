package picklab.backend.activity.infrastructure

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import picklab.backend.activity.application.ActivityQueryRepository
import picklab.backend.activity.application.model.ActivityView
import picklab.backend.activity.application.model.GetMyBookmarkListCondition
import picklab.backend.activity.domain.entity.QActivity
import picklab.backend.activity.domain.entity.QActivityBookmark
import picklab.backend.activity.domain.entity.QActivityJobCategory
import picklab.backend.activity.domain.enums.ActivityBookmarkSortType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.job.domain.entity.QJobCategory
import picklab.backend.member.domain.entity.QMemberActivityViewHistory

@Repository
class ActivityQueryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ActivityQueryRepository {
    override fun findAllByMemberJobRecommendation(
        jobIds: List<Long>,
        pageable: PageRequest,
    ): Page<ActivityView> {
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
                ).map { it as ActivityView }

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

    override fun findPopularActivities(pageable: PageRequest): Page<ActivityView> {
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
                ).map { it as ActivityView }

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

    override fun findActivityItemByMemberBookmarked(
        memberId: Long,
        queryData: GetMyBookmarkListCondition,
        pageable: PageRequest,
    ): Page<ActivityView> {
        val condition =
            BooleanBuilder().apply {
                and(
                    QActivityBookmark.activityBookmark.member.id
                        .eq(memberId),
                )
                and(QActivity.activity.deletedAt.isNull)

                queryData.activityTypes?.let { activityTypes ->
                    and(QActivity.activity.activityType.`in`(activityTypes.map { it.discriminator }))
                }

                queryData.recruitmentStatus?.let { status ->
                    and(QActivity.activity.status.eq(status))
                }

                queryData.jobGroups?.let { jobGroups ->
                    and(QJobCategory.jobCategory.jobGroup.`in`(jobGroups))
                }
            }

        val orderBy =
            when (queryData.sortType) {
                ActivityBookmarkSortType.RECENTLY_BOOKMARKED -> listOf(QActivityBookmark.activityBookmark.createdAt.desc())
                ActivityBookmarkSortType.LATEST -> listOf(QActivity.activity.createdAt.desc())
                ActivityBookmarkSortType.DEADLINE_ASC ->
                    listOf(
                        QActivity.activity.recruitmentEndDate.asc(),
                        QActivity.activity.createdAt.desc(),
                    )

                ActivityBookmarkSortType.DEADLINE_DESC ->
                    listOf(
                        QActivity.activity.recruitmentEndDate.desc(),
                        QActivity.activity.createdAt.desc(),
                    )
            }

        val items =
            jpaQueryFactory
                .select(QActivity.activity.id)
                .from(QActivityBookmark.activityBookmark)
                .join(QActivityBookmark.activityBookmark.activity, QActivity.activity)
                .leftJoin(QActivityJobCategory.activityJobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.activity.id
                        .eq(QActivity.activity.id),
                ).leftJoin(QJobCategory.jobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.jobCategory.id
                        .eq(QJobCategory.jobCategory.id),
                ).where(condition)
                .orderBy(*orderBy.toTypedArray())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .transform(
                    GroupBy.groupBy(QActivity.activity.id).list(
                        Projections.constructor(
                            ActivityView::class.java,
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
                .select(QActivityBookmark.activityBookmark.id.countDistinct())
                .from(QActivityBookmark.activityBookmark)
                .join(QActivityBookmark.activityBookmark.activity, QActivity.activity)
                .leftJoin(QActivityJobCategory.activityJobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.activity.id
                        .eq(QActivity.activity.id),
                ).leftJoin(QJobCategory.jobCategory)
                .on(
                    QActivityJobCategory.activityJobCategory.jobCategory.id
                        .eq(QJobCategory.jobCategory.id),
                ).where(condition)
                .fetchOne() ?: 0L

        return PageImpl(items, pageable, count)
    }

    override fun findRecentlyViewedActivities(
        memberId: Long,
        pageable: PageRequest,
    ): Page<ActivityView> {
        val latestViewActivities =
            jpaQueryFactory
                .select(
                    QActivity.activity.id,
                    QMemberActivityViewHistory.memberActivityViewHistory.createdAt.max(),
                ).from(QMemberActivityViewHistory.memberActivityViewHistory)
                .join(QMemberActivityViewHistory.memberActivityViewHistory.activity, QActivity.activity)
                .where(
                    QMemberActivityViewHistory.memberActivityViewHistory.member.id
                        .eq(memberId),
                    QActivity.activity.deletedAt.isNull,
                ).groupBy(QActivity.activity.id)
                .orderBy(
                    QMemberActivityViewHistory.memberActivityViewHistory.createdAt
                        .max()
                        .desc(),
                ).offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        if (latestViewActivities.isEmpty()) {
            return PageImpl(emptyList(), pageable, 0)
        }

        val sortedActivityIds =
            latestViewActivities.mapNotNull {
                it.get(QActivity.activity.id)
            }

        val itemsMap =
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
                ).where(QActivity.activity.id.`in`(sortedActivityIds))
                .transform(
                    GroupBy.groupBy(QActivity.activity.id).`as`(
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

        val sortedItems =
            sortedActivityIds.mapNotNull { activityId ->
                itemsMap[activityId]
            }

        val count =
            jpaQueryFactory
                .select(QActivity.activity.id.countDistinct())
                .from(QMemberActivityViewHistory.memberActivityViewHistory)
                .join(QMemberActivityViewHistory.memberActivityViewHistory.activity, QActivity.activity)
                .where(
                    QMemberActivityViewHistory.memberActivityViewHistory.member.id
                        .eq(memberId),
                    QActivity.activity.deletedAt.isNull,
                ).fetchOne() ?: 0L

        return PageImpl(sortedItems, pageable, count)
    }
}
