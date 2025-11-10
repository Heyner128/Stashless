import gradle.kotlin.dsl.accessors._24c08a5a081fe29647e836babef36c8b.annotationProcessor
import gradle.kotlin.dsl.accessors._24c08a5a081fe29647e836babef36c8b.developmentOnly
import gradle.kotlin.dsl.accessors._24c08a5a081fe29647e836babef36c8b.runtimeOnly
import gradle.kotlin.dsl.accessors._24c08a5a081fe29647e836babef36c8b.testRuntimeOnly
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

val libs = the<LibrariesForLibs>()

plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

open class ProfilePropertiesExtension {
    var prefix: String = ""
}

val projectProperties = extensions.create<ProfilePropertiesExtension>("projectProperties")

tasks.withType<Test> {
    doFirst {
        val prefix = projectProperties.prefix
        val coreProfile  = "core-test"
        val specificProfile = if (prefix.isNotEmpty()) "${prefix}-test" else "default"
        environment("SPRING_PROFILES_ACTIVE", "${coreProfile},${specificProfile}")
    }
    useJUnitPlatform()
}

tasks.register("run") {
    doFirst {
        val prefix = projectProperties.prefix
        val coreProfile  = "core"
        val specificProfile = prefix.ifEmpty { "default" }
        tasks.bootRun.get().environment("SPRING_PROFILES_ACTIVE", "${coreProfile},${specificProfile}")
    }
    finalizedBy(tasks.bootRun)
}

tasks.register("devRun") {
    doFirst {
        val prefix = projectProperties.prefix
        val coreProfile  = "core,core-dev"
        val specificProfile = if(prefix.isNotEmpty()) "${prefix}-dev" else "default"
        tasks.bootRun.get().environment("SPRING_PROFILES_ACTIVE", "${coreProfile},${prefix},${specificProfile}")
    }
    finalizedBy(tasks.bootRun)
}

dependencies {
    annotationProcessor(libs.lombok)

    annotationProcessor(libs.spring.boot.configuration.processor)

    developmentOnly(libs.spring.boot.devtools)

    runtimeOnly(libs.driver.postgresql)

    runtimeOnly(libs.driver.h2)

    testRuntimeOnly(libs.driver.h2)
}