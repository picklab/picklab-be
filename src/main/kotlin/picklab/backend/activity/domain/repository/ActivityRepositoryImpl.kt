package picklab.backend.activity.domain.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import picklab.backend.activity.application.model.ActivityItem
import picklab.backend.activity.application.model.ActivitySearchCommand
import picklab.backend.activity.domain.entity.QActivity
import picklab.backend.activity.domain.entity.QActivityJobCategory
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.ActivitySortType
import picklab.backend.activity.domain.enums.DomainType
import picklab.backend.activity.domain.enums.EducationCostType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.job.domain.entity.QJobCategory
import java.time.LocalDate

@Repository
class ActivityRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ActivityRepositoryCustom {
    override fun getActivities(
        queryData: ActivitySearchCommand,
        pageable: PageRequest,
    ): Page<ActivityItem> {
        val condition =
            BooleanBuilder().apply {
                and(QActivity.activity.activityType.eq(queryData.category.name))
                and(QActivity.activity.deletedAt.isNull)
                and(QActivity.activity.recruitmentEndDate.goe(LocalDate.now()))
                andIfNotNullOrEmpty(queryData.jobTag) { QJobCategory.jobCategory.jobDetail.`in`(queryData.jobTag) }
                andIfNotNullOrEmpty(queryData.organizer) { QActivity.activity.organizer.`in`(queryData.organizer) }
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
                ActivitySortType.LATEST -> listOf(QActivity.activity.createdAt.desc())
                ActivitySortType.DEADLINE_ASC -> listOf(QActivity.activity.recruitmentEndDate.asc(), QActivity.activity.createdAt.desc())
                ActivitySortType.DEADLINE_DESC ->
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
                        Projections.constructor(
                            ActivityItem::class.java,
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
}

inline fun <T> BooleanBuilder.andIfNotNullOrEmpty(
    collection: Collection<T>?,
    predicate: (Collection<T>) -> BooleanExpression,
) = apply { if (!collection.isNullOrEmpty()) and(predicate(collection)) }
