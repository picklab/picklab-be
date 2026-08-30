package picklab.backend.highschool.domain.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import picklab.backend.highschool.domain.entity.HighSchool

interface HighSchoolRepository : JpaRepository<HighSchool, Long> {
    @Query(
        """
        SELECT h
        FROM HighSchool h
        WHERE h.isActive = true
          AND h.name LIKE CONCAT('%', :query, '%')
        ORDER BY
          CASE
            WHEN h.name = :query THEN 0
            WHEN h.name LIKE CONCAT(:query, '%') THEN 1
            ELSE 2
          END,
          LENGTH(h.name),
          h.name ASC
        """,
    )
    fun searchByName(
        @Param("query")
        query: String,
        pageable: Pageable,
    ): List<HighSchool>
}
