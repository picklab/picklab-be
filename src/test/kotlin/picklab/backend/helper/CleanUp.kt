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
        val tables = mutableListOf<String>()

        entityManager.metamodel.entities
            .stream()
            .filter { entity -> entity.javaType.getAnnotation(Entity::class.java) != null }
            .filter { entity -> entity.javaType.getAnnotation(DiscriminatorValue::class.java) == null }
            .map { entity -> entity.javaType.getAnnotation(Table::class.java).name }
            .forEach { tables.add(it) }

        tables
            .forEach { table ->
                jdbcTemplate.execute("TRUNCATE TABLE $table")
            }
    }
}
