package picklab.backend.activity.domain.repository

import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import picklab.backend.activity.application.model.ActivityItem
import picklab.backend.activity.application.model.ActivitySearchCommand
import picklab.backend.activity.domain.enums.ActivitySortType
import java.sql.Date

@Repository
class ActivityRepositoryImpl(
    private val entityManager: EntityManager,
) : ActivityRepositoryCustom {
    override fun getActivities(
        queryData: ActivitySearchCommand,
        pageable: PageRequest,
    ): Page<ActivityItem> {
        val (condition, params) = createCondition(queryData)

        val orderBy =
            when (queryData.sort) {
                ActivitySortType.LATEST -> "ORDER BY a.created_at DESC"
                ActivitySortType.DEADLINE_ASC -> "ORDER BY a.recruitment_end_date ASC, a.created_at DESC"
                ActivitySortType.DEADLINE_DESC -> "ORDER BY DATEDIFF(a.recruitment_end_date, CURRENT_DATE) DESC, a.created_at DESC"
            }

        val itemQuery =
            """
            SELECT a.id, a.title, a.organizer, a.start_date, a.activity_type,
            GROUP_CONCAT(jc.job_detail) AS job_tags,
            a.activity_thumbnail_url
            FROM activity a
            LEFT JOIN activity_group ag ON a.group_id = ag.id
            LEFT JOIN activity_job_category ajc ON a.id = ajc.activity_id
            LEFT JOIN job_category jc ON ajc.job_category_id = jc.id
            $condition
            GROUP BY a.id, a.title, a.organizer, a.start_date, a.activity_type, a.activity_thumbnail_url, a.created_at
            $orderBy
            LIMIT :offset, :size
            """.trimIndent()

        val countQuery =
            """
            SELECT COUNT(DISTINCT a.id)
            FROM activity a
            LEFT JOIN activity_group ag ON a.group_id = ag.id
            LEFT JOIN activity_job_category ajc ON a.id = ajc.activity_id
            LEFT JOIN job_category jc ON ajc.job_category_id = jc.id
            $condition
            """.trimIndent()

        val nativeQuery =
            entityManager
                .createNativeQuery(itemQuery)
                .setParameter("category", queryData.category.name)
                .setParameter("offset", pageable.offset)
                .setParameter("size", pageable.pageSize)

        val countNativeQuery =
            entityManager
                .createNativeQuery(countQuery)
                .setParameter("category", queryData.category.name)

        params.forEach { (key, value) ->
            nativeQuery.setParameter(key, value)
            countNativeQuery.setParameter(key, value)
        }

        val result = nativeQuery.resultList as List<Array<Any?>>
        val total = (countNativeQuery.singleResult as Number).toLong()

        val items =
            result.map { row ->
                ActivityItem(
                    id = (row[0] as Number).toLong(),
                    title = row[1] as String,
                    organization = row[2] as String,
                    startDate = (row[3] as Date).toLocalDate(),
                    category = row[4] as String,
                    jobTags = (row[5] as? String)?.split(",") ?: emptyList(),
                    thumbnailUrl = row[6] as? String,
                )
            }

        return PageImpl(items, pageable, total)
    }

    private fun createCondition(queryData: ActivitySearchCommand): Pair<String, Map<String, Any>> {
        var condition =
            """
            WHERE a.activity_type = :category
            AND a.deleted_at IS NULL
            AND a.recruitment_end_date >= CURRENT_DATE
            """.trimIndent()
        val params = mutableMapOf<String, Any>()

        if (!queryData.jobTag.isNullOrEmpty()) {
            condition += " AND jc.job_detail IN (:jobTags)"
            params["jobTags"] = queryData.jobTag.map { it.name }
        }

        if (!queryData.organizer.isNullOrEmpty()) {
            condition += " AND a.organizer IN (:organizers)"
            params["organizers"] = queryData.organizer.map { it.name }
        }

        if (!queryData.target.isNullOrEmpty()) {
            condition += " AND a.target_audience IN (:targets)"
            params["targets"] = queryData.target.map { it.name }
        }

        if (!queryData.field.isNullOrEmpty()) {
            condition += " AND a.activity_field IN (:fields)"
            params["fields"] = queryData.field.map { it.name }
        }

        if (!queryData.location.isNullOrEmpty()) {
            condition += " AND a.location IN (:locations)"
            params["locations"] = queryData.location.map { it.name }
        }

        if (queryData.format != null) {
            condition += " AND a.education_format IN (:format)"
            params["format"] = queryData.format.map { it.name }
        }

        if (!queryData.costType.isNullOrEmpty()) {
            condition += " AND a.education_cost_type IN (:costTypes)"
            params["costTypes"] = queryData.costType.map { it.name }
        }

        if (queryData.award != null) {
            if (queryData.award.size == 1) {
                condition += " AND a.cost < :maxAward"
                params["maxAward"] = queryData.award[0]
            } else {
                condition += " AND a.cost >= :minAward AND a.cost < :maxAward"
                params["minAward"] = queryData.award[0]
                params["maxAward"] = queryData.award[1]
            }
        }

        if (queryData.duration != null) {
            if (queryData.duration.size == 1) {
                condition += " AND a.duration < :maxDuration"
                params["maxDuration"] = queryData.duration[0] * 30
            } else {
                condition += " AND a.duration BETWEEN :minDuration AND :maxDuration"
                params["minDuration"] = queryData.duration[0] * 30
                params["maxDuration"] = queryData.duration[1] * 30
            }
        }

        if (!queryData.domain.isNullOrEmpty()) {
            condition += " AND a.domain IN (:domains)"
            params["domains"] = queryData.domain.map { it.name }
        }

        return condition to params
    }
}
