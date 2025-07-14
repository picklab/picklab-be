package picklab.backend.helper

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.Table
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CleanUp(
    private val jdbcTemplate: JdbcTemplate,
    private val entityManager: EntityManager,
) {
    @Transactional
    fun all() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0")

        try {
            val tables =
                entityManager.metamodel.entities
                    .filter { entity -> entity.javaType.getAnnotation(Entity::class.java) != null }
                    .filter { entity -> entity.javaType.getAnnotation(DiscriminatorValue::class.java) == null }
                    .mapNotNull { entity -> entity.javaType.getAnnotation(Table::class.java).name }
                    .toSet()

            tables
                .forEach { table ->
                    try {
                        jdbcTemplate.execute("TRUNCATE TABLE $table")
                    } catch (e: Exception) {
                        println("Failed to clean table: $table, error: ${e.message}")
                    }
                }
        } finally {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1")
        }
    }
}
