package picklab.backend.activity.domain.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import picklab.backend.activity.application.model.ActivityItem
import picklab.backend.activity.application.model.ActivitySearchCommand
import picklab.backend.activity.domain.entity.QActivity
import picklab.backend.activity.domain.entity.QActivityGroup
import picklab.backend.activity.domain.entity.QActivityJobCategory
import picklab.backend.activity.domain.enums.ActivityFieldType
import picklab.backend.activity.domain.enums.ActivitySortType
import picklab.backend.activity.domain.enums.DomainType
import picklab.backend.activity.domain.enums.EducationCostType
import picklab.backend.activity.domain.enums.EducationFormatType
import picklab.backend.activity.domain.enums.LocationType
import picklab.backend.job.domain.entity.QJobCategory
import java.time.LocalDate
import kotlin.collections.map

@Repository
class ActivityRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ActivityRepositoryCustom {
    override fun getActivities(
        queryData: ActivitySearchCommand,
        pageable: PageRequest,
    ): Page<ActivityItem> {
        val a = QActivity.activity
        val ag = QActivityGroup.activityGroup
        val ajc = QActivityJobCategory.activityJobCategory
        val jc = QJobCategory.jobCategory

        val condition = createGetActivitiesCondition(queryData, a, jc)

        val orderBy =
            when (queryData.sort) {
                ActivitySortType.LATEST -> listOf(a.createdAt.desc())
                ActivitySortType.DEADLINE_ASC -> listOf(a.recruitmentEndDate.asc(), a.createdAt.desc())
                ActivitySortType.DEADLINE_DESC ->
                    listOf(
                        Expressions
                            .numberTemplate(
                                Long::class.java,
                                "DATEDIFF({0}, {1})",
                                a.recruitmentEndDate,
                                LocalDate.now(),
                            ).desc(),
                        a.createdAt.desc(),
                    )
            }

        val result =
            jpaQueryFactory
                .select(
                    a.id,
                    a.title,
                    a.organizer,
                    a.startDate,
                    a.activityType,
                    jc.jobDetail,
                    a.activityThumbnailUrl,
                ).from(a)
                .leftJoin(ajc)
                .on(ajc.activity.id.eq(a.id))
                .leftJoin(jc)
                .on(ajc.jobCategory.id.eq(jc.id))
                .where(condition)
                .orderBy(*orderBy.toTypedArray())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        val items =
            result
                .groupBy { it.get(a.id) }
                .map { (id, rows) ->
                    val first = rows.first()
                    ActivityItem(
                        id = id!!,
                        title = first.get(a.title)!!,
                        organization = first.get(a.organizer)?.toString() ?: "",
                        startDate = first.get(a.startDate)!!,
                        category = first.get(a.activityType) ?: "",
                        jobTags = rows.mapNotNull { it.get(jc.jobDetail)?.toString() }.distinct(),
                        thumbnailUrl = first.get(a.activityThumbnailUrl),
                    )
                }

        val count =
            jpaQueryFactory
                .select(a.id.countDistinct())
                .from(a)
                .leftJoin(a.activityGroup, ag)
                .leftJoin(ajc)
                .on(ajc.activity.id.eq(a.id))
                .leftJoin(jc)
                .on(ajc.jobCategory.id.eq(jc.id))
                .where(condition)
                .fetchOne() ?: 0L

        return PageImpl(items, pageable, count)
    }

    fun createGetActivitiesCondition(
        queryData: ActivitySearchCommand,
        a: QActivity,
        jc: QJobCategory,
    ): BooleanBuilder {
        val condition = BooleanBuilder()

        condition.and(a.activityType.eq(queryData.category.name))
        condition.and(a.deletedAt.isNull)
        condition.and(a.recruitmentEndDate.goe(LocalDate.now()))

        if (!queryData.jobTag.isNullOrEmpty()) {
            condition.and(jc.jobDetail.`in`(queryData.jobTag))
        }

        if (!queryData.organizer.isNullOrEmpty()) {
            condition.and(a.organizer.`in`(queryData.organizer))
        }

        if (!queryData.target.isNullOrEmpty()) {
            condition.and(a.targetAudience.`in`(queryData.target))
        }

        if (!queryData.field.isNullOrEmpty()) {
            condition.and(
                Expressions.enumPath(ActivityFieldType::class.javaObjectType, "activityField").`in`(queryData.field),
            )
        }

        if (!queryData.location.isNullOrEmpty()) {
            condition.and(Expressions.enumPath(LocationType::class.javaObjectType, "location").`in`(queryData.location))
        }

        if (queryData.format != null) {
            condition.and(Expressions.enumPath(EducationFormatType::class.javaObjectType, "format").`in`(queryData.format))
        }

        if (!queryData.costType.isNullOrEmpty()) {
            condition.and(
                Expressions.enumPath(EducationCostType::class.javaObjectType, "costType").`in`(queryData.costType),
            )
        }

        if (queryData.award != null) {
            if (queryData.award.size == 1) {
                condition.and(Expressions.numberPath(Long::class.javaObjectType, "cost").lt(queryData.award[0]))
            } else {
                condition.and(Expressions.numberPath(Long::class.javaObjectType, "cost").goe(queryData.award[0]))
                condition.and(Expressions.numberPath(Long::class.javaObjectType, "cost").lt(queryData.award[1]))
            }
        }

        if (queryData.duration != null) {
            if (queryData.duration.size == 1) {
                condition.and(a.duration.lt(queryData.duration[0] * 30))
            } else {
                condition.and(a.duration.between(queryData.duration[0] * 30, queryData.duration[1] * 30))
            }
        }

        if (!queryData.domain.isNullOrEmpty()) {
            condition.and(Expressions.enumPath(DomainType::class.javaObjectType, "domain").`in`(queryData.domain))
        }

        return condition
    }
}
