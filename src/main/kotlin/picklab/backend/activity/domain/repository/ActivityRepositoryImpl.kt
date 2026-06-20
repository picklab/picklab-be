package picklab.backend.activity.domain.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import picklab.backend.activity.application.model.ActivitySearchCondition
import picklab.backend.activity.application.model.ActivityView
import picklab.backend.activity.domain.entity.QActivity
import picklab.backend.activity.domain.entity.QActivityJobCategory
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.ActivitySortType
import picklab.backend.activity.domain.enums.DomainType
import picklab.backend.activity.domain.enums.EducationCostType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.activity.domain.enums.RecruitmentEndType
import picklab.backend.activity.domain.enums.RecruitmentStatus
import picklab.backend.activity.infrastructure.QActivityItem
import picklab.backend.job.domain.entity.QJobCategory
import picklab.backend.job.domain.enums.JobGroup
import java.time.LocalDate

@Repository
class ActivityRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ActivityRepositoryCustom {
    override fun getActivities(
        queryData: ActivitySearchCondition,
        pageable: PageRequest,
    ): Page<ActivityView> {
        val condition =
            BooleanBuilder().apply {
                and(QActivity.activity.activityType.eq(queryData.category.name))
                and(QActivity.activity.deletedAt.isNull)
                and(
                    Expressions
                        .enumPath(RecruitmentEndType::class.java, "recruitmentEndType")
                        .ne(RecruitmentEndType.FIXED)
                        .or(QActivity.activity.recruitmentEndDate.goe(LocalDate.now())),
                )
                andIfNotNullOrEmpty(queryData.jobTag) { QJobCategory.jobCategory.jobDetail.`in`(queryData.jobTag) }
                andIfNotNullOrEmpty(queryData.organizerType) { QActivity.activity.organizerType.`in`(queryData.organizerType) }
                andIfNotNullOrEmpty(queryData.target) { QActivity.activity.targetAudience.`in`(queryData.target) }
                andIfNotNullOrEmpty(queryData.field) {
                    Expressions.enumPath(ActivityFieldType::class.javaObjectType, "activityField").`in`(queryData.field)
                }
                andIfNotNullOrEmpty(queryData.location) {
                    Expressions.enumPath(LocationType::class.javaObjectType, "location").`in`(queryData.location)
                }
                andIfNotNullOrEmpty(queryData.format) {
                    Expressions.enumPath(EducationFormatType::class.javaObjectType, "format").`in`(queryData.format)
                }
                andIfNotNullOrEmpty(queryData.costType) {
                    Expressions.enumPath(EducationCostType::class.javaObjectType, "costType").`in`(queryData.costType)
                }
                andIfNotNullOrEmpty(queryData.domain) {
                    Expressions.enumPath(DomainType::class.javaObjectType, "domain").`in`(queryData.domain)
                }

                if (queryData.award != null) {
                    if (queryData.award.size == 1) {
                        and(Expressions.numberPath(Long::class.javaObjectType, "cost").lt(queryData.award[0]))
                    } else {
                        and(Expressions.numberPath(Long::class.javaObjectType, "cost").goe(queryData.award[0]))
                        and(Expressions.numberPath(Long::class.javaObjectType, "cost").lt(queryData.award[1]))
                    }
                }
                if (queryData.duration != null) {
                    if (queryData.duration.size == 1) {
                        and(QActivity.activity.duration.lt(queryData.duration[0] * 30))
                    } else {
                        and(QActivity.activity.duration.between(queryData.duration[0] * 30, queryData.duration[1] * 30))
                    }
                }
            }

        val orderBy =
            when (queryData.sort) {
                ActivitySortType.LATEST -> {
                    listOf(QActivity.activity.createdAt.desc())
                }

                ActivitySortType.DEADLINE_ASC -> {
                    listOf(
                        QActivity.activity.recruitmentEndDate.asc(),
                        QActivity.activity.createdAt.desc(),
                    )
                }

                ActivitySortType.DEADLINE_DESC -> {
                    listOf(
                        Expressions
                            .numberTemplate(
                                Long::class.java,
                                "DATEDIFF({0}, {1})",
                                QActivity.activity.recruitmentEndDate,
                                LocalDate.now(),
                            ).desc(),
                        QActivity.activity.createdAt.desc(),
                    )
                }
            }

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
                ).where(condition)
                .orderBy(*orderBy.toTypedArray())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .transform(
                    GroupBy.groupBy(QActivity.activity.id).list(
                        QActivityItem(
                            QActivity.activity.id,
                            QActivity.activity.title,
                            QActivity.activity.organizer,
                            QActivity.activity.organizerType.stringValue(),
                            QActivity.activity.startDate,
                            QActivity.activity.activityType,
                            GroupBy.list(QJobCategory.jobCategory.jobDetail.stringValue()),
                            QActivity.activity.activityThumbnailUrl,
                            QActivity.activity.viewCount,
                            QActivity.activity.recruitmentEndDate,
                            QActivity.activity.recruitmentEndType,
                        ),
                    ),
                ).map { it as ActivityView }

        val count =
            jpaQueryFactory
                .select(QActivity.activity.id.countDistinct())
                .from(QActivity.activity)
                .leftJoin(QActivity.activity.activityGroup)
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

    override fun findActivityTitlesForAutocomplete(
        keyword: String,
        limit: Int,
    ): List<String> =
        jpaQueryFactory
            .select(QActivity.activity.title)
            .from(QActivity.activity)
            .where(
                QActivity.activity.deletedAt.isNull
                    .and(QActivity.activity.title.startsWith(keyword)),
            ).orderBy(QActivity.activity.title.asc())
            .limit(limit.toLong())
            .fetch()

    override fun searchActivitiesByKeyword(
        keyword: String,
        activityType: String?,
        status: RecruitmentStatus?,
        jobGroups: List<JobGroup>?,
        sort: ActivitySortType,
        pageable: PageRequest,
    ): Page<ActivityView> {
        val condition =
            BooleanBuilder().apply {
                and(QActivity.activity.deletedAt.isNull)
                and(
                    QActivity.activity.title
                        .containsIgnoreCase(keyword)
                        .or(QActivity.activity.organizer.containsIgnoreCase(keyword)),
                )
                activityType?.let { and(QActivity.activity.activityType.eq(it)) }
                status?.let { and(QActivity.activity.status.eq(it)) }
                jobGroups?.let { and(QJobCategory.jobCategory.jobGroup.`in`(it)) }
            }

        val daysUntilDeadline =
            Expressions.numberTemplate(
                Long::class.java,
                "DATEDIFF({0}, {1})",
                QActivity.activity.recruitmentEndDate,
                LocalDate.now(),
            )
        val orderBy =
            when (sort) {
                ActivitySortType.LATEST -> {
                    listOf(QActivity.activity.createdAt.desc())
                }

                ActivitySortType.DEADLINE_ASC -> {
                    listOf(
                        QActivity.activity.recruitmentEndDate.asc(),
                        QActivity.activity.createdAt.desc(),
                    )
                }

                ActivitySortType.DEADLINE_DESC -> {
                    listOf(
                        daysUntilDeadline.desc(),
                        QActivity.activity.createdAt.desc(),
                    )
                }
            }

        val activityIds =
            when (sort) {
                ActivitySortType.DEADLINE_DESC ->
                    jpaQueryFactory
                        .select(QActivity.activity, daysUntilDeadline)
                        .distinct()
                        .from(QActivity.activity)
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
                        .mapNotNull { it.get(QActivity.activity)?.id }

                else ->
                    jpaQueryFactory
                        .select(QActivity.activity)
                        .distinct()
                        .from(QActivity.activity)
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
                        .map { it.id }
            }

        if (activityIds.isEmpty()) {
            return PageImpl(emptyList(), pageable, 0)
        }

        val itemsMap =
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
                .transform(
                    GroupBy.groupBy(QActivity.activity.id).`as`(
                        QActivityItem(
                            QActivity.activity.id,
                            QActivity.activity.title,
                            QActivity.activity.organizer,
                            QActivity.activity.organizerType.stringValue(),
                            QActivity.activity.startDate,
                            QActivity.activity.activityType,
                            GroupBy.list(QJobCategory.jobCategory.jobDetail.stringValue()),
                            QActivity.activity.activityThumbnailUrl,
                            QActivity.activity.viewCount,
                            QActivity.activity.recruitmentEndDate,
                            QActivity.activity.recruitmentEndType,
                        ),
                    ),
                )

        val items = activityIds.mapNotNull { itemsMap[it] }

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
                ).where(condition)
                .fetchOne() ?: 0L

        return PageImpl(items, pageable, count)
    }

    override fun countActivitiesByKeywordPerType(keyword: String): Map<String, Long> {
        val condition =
            BooleanBuilder().apply {
                and(QActivity.activity.deletedAt.isNull)
                and(
                    QActivity.activity.title
                        .containsIgnoreCase(keyword)
                        .or(QActivity.activity.organizer.containsIgnoreCase(keyword)),
                )
            }

        return jpaQueryFactory
            .select(QActivity.activity.activityType, QActivity.activity.id.count())
            .from(QActivity.activity)
            .where(condition)
            .groupBy(QActivity.activity.activityType)
            .fetch()
            .associate { tuple ->
                (tuple.get(QActivity.activity.activityType) ?: "") to
                    (tuple.get(QActivity.activity.id.count()) ?: 0L)
            }
    }
}

inline fun <T> BooleanBuilder.andIfNotNullOrEmpty(
    collection: Collection<T>?,
    predicate: (Collection<T>) -> BooleanExpression,
) = apply { if (!collection.isNullOrEmpty()) and(predicate(collection)) }
