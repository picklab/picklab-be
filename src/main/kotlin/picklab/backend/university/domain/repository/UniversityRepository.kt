package picklab.backend.university.domain.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import picklab.backend.university.domain.entity.University

interface UniversityRepository : JpaRepository<University, Long> {
    @Query(
        """
        SELECT u
        FROM University u
        WHERE u.isActive = true
          AND u.name LIKE CONCAT('%', :query, '%')
        ORDER BY
          CASE
            WHEN u.name = :query THEN 0
            WHEN u.name LIKE CONCAT(:query, '%') THEN 1
            ELSE 2
          END,
          LENGTH(u.name),
          u.name ASC
        """,
    )
    fun searchByName(
        @Param("query")
        query: String,
        pageable: Pageable,
    ): List<University>
}
