plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    id("com.google.devtools.ksp") version "1.9.25-1.0.20"
    jacoco
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
}

group = "picklab"
version = "0.0.1-SNAPSHOT"
val swaggerVersion = "2.8.8"
val jjwtVersion = "0.12.6"
val queryDSLVersion = "7.0"
val archUnitVersion = "1.4.1"
val caffeineCacheVersion = "3.2.2"
val awsJavaSdkVersion = "2.32.19"
val localStackContainerVersion = "1.21.3"
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$swaggerVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:mysql")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.mockk:mockk:1.13.8")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    testImplementation("org.testcontainers:localstack:$localStackContainerVersion")

    // jjwt
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    // QueryDSL
    implementation("io.github.openfeign.querydsl:querydsl-jpa:$queryDSLVersion")
    ksp("io.github.openfeign.querydsl:querydsl-ksp-codegen:$queryDSLVersion")

    // archunit
    testImplementation("com.tngtech.archunit:archunit-junit5:$archUnitVersion")

    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineCacheVersion")

    // ncloud object storage
    implementation(platform("software.amazon.awssdk:bom:$awsJavaSdkVersion"))
    implementation("software.amazon.awssdk:s3")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }

    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/entrypoint/request/**",
                        "**/entrypoint/response/**",
                        "**/domain/entity/**",
                    )
                }
            },
        ),
    )
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)

    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/entrypoint/request/**",
                        "**/entrypoint/response/**",
                        "**/domain/entity/**",
                    )
                }
            },
        ),
    )

    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.named("check").configure { dependsOn("ktlintCheck") }

jacoco {
    toolVersion = "0.8.11"
}

ktlint {
    ignoreFailures.set(false)
    filter {
        exclude { element ->
            val p = element.file.path
            p.contains("${File.separator}build${File.separator}generated${File.separator}") ||
                p.replace('\\', '/').contains("/build/generated/")
        }
    }
}
