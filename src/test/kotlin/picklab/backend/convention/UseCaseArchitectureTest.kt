package picklab.backend.convention

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class UseCaseArchitectureTest {

    private val importedClasses: JavaClasses = ClassFileImporter()
        .importPackages("picklab.backend")


    @Test
    fun useCasesShouldNotDependOnOtherUseCases() {
        val rule = noClasses()
            .that().haveSimpleNameEndingWith("UseCase")
            .should().dependOnClassesThat()
            .haveSimpleNameEndingWith("UseCase")
            .because("UseCase should not depend on other UseCase classes")
            .allowEmptyShould(true)

        rule.check(importedClasses)
    }

    @Test
    fun useCasesShouldNotDependOnRepositories() {
        val rule = noClasses()
            .that().haveSimpleNameEndingWith("UseCase")
            .should().dependOnClassesThat()
            .haveSimpleNameEndingWith("Repository")
            .because("UseCase should not depend on Repository directly, use Service instead")

        rule.check(importedClasses)
    }

    @Test
    fun useCasesShouldNotDependOnControllers() {
        val rule = noClasses()
            .that().haveSimpleNameEndingWith("UseCase")
            .should().dependOnClassesThat()
            .haveSimpleNameEndingWith("Controller")
            .because("UseCase should not depend on Controller")

        rule.check(importedClasses)
    }
} 