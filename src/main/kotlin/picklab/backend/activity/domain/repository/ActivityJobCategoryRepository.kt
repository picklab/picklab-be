package picklab.backend.activity.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import picklab.backend.activity.domain.entity.ActivityJobCategory

interface ActivityJobCategoryRepository : JpaRepository<ActivityJobCategory, Long> {
    @Query("""
        SELECT ajc FROM ActivityJobCategory ajc
        JOIN FETCH ajc.activity a
        JOIN FETCH ajc.jobCategory jc
        WHERE a.id IN :activityIds
    """)
    fun findJobCategoriesByActivityIds(@Param("activityIds") activityIds: List<Long>): List<ActivityJobCategory>
}
