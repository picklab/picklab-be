package picklab.backend.convention

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent.satisfied
import com.tngtech.archunit.lang.SimpleConditionEvent.violated
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import io.swagger.v3.oas.annotations.media.Schema
import org.junit.jupiter.api.Test

class SchemaAnnotationTest {

    @Test
    fun allDtoClassesMustHaveSchemaAnnotation() {
        val importedClasses: JavaClasses = ClassFileImporter().importPackages("picklab.backend")
        importedClasses.forEach { println("Imported class: ${it.name}") }

        val schemaAnnotationCondition = object : ArchCondition<JavaClass>("have all fields annotated with @Schema") {
            override fun check(item: JavaClass, events: ConditionEvents) {
                val className = item.name.lowercase()
                if (className.isNotResponseOrRequest()) return

                item.fields.forEach { field ->
                    println("Checking field: ${field.name} in class ${item.name}")
                    println("Annotations: ${field.annotations.map { it.rawType.name }}") // 필드 어노테이션 확인

                    val result = if (field.name.isCompanion() || field.isAnnotatedWith(Schema::class.java)) {
                        satisfied(
                            field,
                            "Field '${field.name}' in class ${item.name} is satisfied (having @Schema annotation)"
                        )
                    } else {
                        violated(field, "Field '${field.name}' in class ${item.name} does not have @Schema annotation")
                    }
                    events.add(result)
                }
            }
        }

        val rule: ArchRule = ArchRuleDefinition.classes()
            .that()
            .resideInAnyPackage("..request..", "..response..")
            .should(schemaAnnotationCondition)

        rule.check(importedClasses)
    }

    private fun String.isResponseOrRequest() = this.contains("response") || this.contains("request")
    private fun String.isCompanion() = this == "Companion"
    private fun String.isNotResponseOrRequest() = !this.isResponseOrRequest()
}