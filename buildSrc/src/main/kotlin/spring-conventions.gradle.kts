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
    val prefix = projectProperties.prefix
    val profile = if(prefix.isNotEmpty()) "${prefix}-test" else "test"
    environment("SPRING_PROFILES_ACTIVE", profile)
    useJUnitPlatform()
}

tasks.bootRun {
    val prefix = projectProperties.prefix
    val profile = prefix.ifEmpty { "default" }
    environment("SPRING_PROFILES_ACTIVE", profile)
}

tasks.register("run") {
    finalizedBy(tasks.bootRun)
}

tasks.register("devRun") {
    doFirst {
        val prefix = projectProperties.prefix
        val profile = if(prefix.isNotEmpty()) "${prefix}-dev" else "dev"
        tasks.bootRun.get().environment("SPRING_PROFILES_ACTIVE", profile)
    }
    finalizedBy(tasks.bootRun)
}