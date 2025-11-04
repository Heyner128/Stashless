import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

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

tasks.bootRun {
    doFirst {
        val prefix = projectProperties.prefix
        val coreProfile  = "core"
        val specificProfile = prefix.ifEmpty { "default" }
        environment("SPRING_PROFILES_ACTIVE", "${coreProfile},${specificProfile}")
    }
}

tasks.register("run") {
    finalizedBy(tasks.bootRun)
}

tasks.register("devRun") {
    doFirst {
        val prefix = projectProperties.prefix
        val coreProfile  = "core,core-dev"
        val specificProfile = if(prefix.isNotEmpty()) "${prefix}-dev" else "default"
        tasks.bootRun.get().environment("SPRING_PROFILES_ACTIVE", "${coreProfile},${specificProfile}")
    }
    finalizedBy(tasks.bootRun)
}