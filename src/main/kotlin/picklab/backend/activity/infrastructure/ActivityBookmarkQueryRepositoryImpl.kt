package picklab.backend.activity.infrastructure

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import picklab.backend.activity.application.ActivityBookmarkQueryRepository
import picklab.backend.activity.application.model.GetMyBookmarkListCommand
import picklab.backend.activity.domain.entity.QActivity
import picklab.backend.activity.domain.entity.QActivityBookmark
import picklab.backend.activity.domain.entity.QActivityJobCategory
import picklab.backend.activity.domain.enums.ActivityBookmarkSortType
import picklab.backend.job.domain.entity.QJobCategory

@Repository
class ActivityBookmarkQueryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ActivityBookmarkQueryRepository {
    override fun findBookmarkedActivityIds(
        memberId: Long,
        queryData: GetMyBookmarkListCommand,
        pageable: PageRequest,
    ): List<Long> {
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

        return jpaQueryFactory
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
            .fetch()
    }

    override fun countBookmarkedActivities(
        memberId: Long,
        queryData: GetMyBookmarkListCommand,
    ): Long {
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

        return jpaQueryFactory
            .select(QActivityBookmark.activityBookmark.id.count())
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
    }
}
