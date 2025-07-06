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
    fun useCasesShouldOnlyDependOnServiceLayer() {
        val rule = classes()
            .that().haveSimpleNameEndingWith("UseCase")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(
                "picklab.backend..service..",
                "picklab.backend..infrastructure..", 
                "picklab.backend..model..",
                "picklab.backend..enums..",
                "picklab.backend..entity..",
                "java..",
                "kotlin..",
                "org.springframework..",
                "org.jetbrains.annotations..",
                "jakarta.."
            )
            .orShould().dependOnClassesThat()
            .haveSimpleNameEndingWith("Service")
            .orShould().dependOnClassesThat()
            .haveSimpleNameEndingWith("Provider")
            .orShould().dependOnClassesThat()
            .haveSimpleNameEndingWith("Mapper")
            .orShould().dependOnClassesThat()
            .areInterfaces()

        rule.check(importedClasses)
    }

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