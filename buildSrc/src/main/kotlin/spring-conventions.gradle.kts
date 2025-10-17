import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

tasks.withType<Test> {
    environment("SPRING_PROFILES_ACTIVE", "test")
    useJUnitPlatform()
}

tasks.register("run") {
    finalizedBy(tasks.bootRun)
}

tasks.register("devRun") {
    doFirst {
        tasks.bootRun.get().environment("SPRING_PROFILES_ACTIVE", "dev")
    }
    finalizedBy(tasks.bootRun)
}