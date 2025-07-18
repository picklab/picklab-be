package picklab.backend.convention

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.test.fail

class SchemaAnnotationTest {
    @Test
    fun allDtoClassesMustHaveSchemaAnnotation() {
        val requestResponseClasses = findRequestResponseClasses()
        val violations = mutableListOf<String>()

        requestResponseClasses.forEach { kClass ->
            println("Checking class: ${kClass.simpleName}")

            kClass.memberProperties.forEach { property ->
                val hasSchemaAnnotation = checkSchemaAnnotation(kClass, property)

                if (!hasSchemaAnnotation) {
                    val violation =
                        "Property '${property.name}' in class ${kClass.simpleName} does not have @Schema annotation"
                    violations.add(violation)
                    println("  ❌ ${property.name}")
                } else {
                    println("  ✅ ${property.name}")
                }
            }
        }

        if (violations.isNotEmpty()) {
            fail("Found ${violations.size} violations:\n${violations.joinToString("\n")}")
        }
    }

    @Test
    fun allEntityClassesMustHaveTableAnnotationAndTableName() {
        val provider = ClassPathScanningCandidateComponentProvider(false)
        provider.addIncludeFilter(AnnotationTypeFilter(Entity::class.java))

        val entityClasses =
            provider.findCandidateComponents("picklab.backend").mapNotNull { beanDefinition ->
                try {
                    Class.forName(beanDefinition.beanClassName).kotlin
                } catch (e: Exception) {
                    null
                }
            }

        val parentEntities = entityClasses.filter { it.findAnnotation<DiscriminatorValue>() == null }

        // @Table 어노테이션이 빠져 있는 엔티티 검증
        val violations =
            parentEntities
                .filter { entity -> entity.annotations.none { it is Table } }
                .mapNotNull { it.simpleName }

        if (violations.isNotEmpty()) {
            fail("The following entity classes are missing the @Table annotation:\n${violations.joinToString("\n")}")
        }

        // @Table 어노테이션은 존재하지만, 테이블명을 누락한 엔티티 검증
        val emptyNameViolations =
            parentEntities
                .mapNotNull { entity ->
                    val table = entity.findAnnotation<Table>()
                    if (table != null && table.name.isBlank()) entity.simpleName else null
                }

        if (emptyNameViolations.isNotEmpty()) {
            fail("The following entity classes are missing the @Table.name value:\n${emptyNameViolations.joinToString("\n")}")
        }
    }

    private fun checkSchemaAnnotation(
        kClass: KClass<*>,
        property: kotlin.reflect.KProperty1<out Any, *>,
    ): Boolean {
        // 1. Property 자체의 annotation 확인 (접두사 없이 사용한 경우)
        val hasPropertySchema = property.annotations.any { it is Schema }

        // 2. Getter method annotation 확인 (@get: 사용한 경우)
        val hasGetterSchema = property.getter.annotations.any { it is Schema }

        // 3. Backing field annotation 확인 (@field: 사용한 경우)
        val hasFieldSchema = property.javaField?.annotations?.any { it is Schema } ?: false

        // 4. Constructor parameter annotation 확인 (접두사 없이 사용한 경우)
        val constructorParam =
            kClass.constructors
                .firstOrNull()
                ?.parameters
                ?.find { it.name == property.name }
        val hasConstructorParamSchema = constructorParam?.annotations?.any { it is Schema } ?: false

        return hasPropertySchema || hasGetterSchema || hasFieldSchema || hasConstructorParamSchema
    }

    private fun findRequestResponseClasses(): List<KClass<*>> {
        val provider = ClassPathScanningCandidateComponentProvider(false)
        provider.addIncludeFilter { _, _ -> true }

        val classes = mutableListOf<KClass<*>>()

        // 전체 picklab.backend 패키지를 스캔하여 request/response 클래스 찾기
        provider.findCandidateComponents("picklab.backend").forEach { beanDefinition ->
            val className = beanDefinition.beanClassName
            if (className != null &&
                (className.contains(".request.") || className.contains(".response.")) &&
                (className.contains("Request") || className.contains("Response"))
            ) {
                try {
                    classes.add(Class.forName(className).kotlin)
                } catch (e: Exception) {
                    // 클래스 로드 실패 시 무시
                }
            }
        }

        return classes
    }
}
