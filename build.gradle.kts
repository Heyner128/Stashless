import java.util.Properties

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.liquibase.core)
    }
}

plugins {
    java
    jacoco
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.liquibase)
    alias(libs.plugins.spotless)
}

group = "me.heyner"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

spotless {
  java {
      googleJavaFormat()
  }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
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
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.session)
    implementation(libs.springdoc)
    implementation(libs.modelmapper)
    implementation(libs.bundles.jjwt)
    implementation(libs.liquibase.core)

    add("liquibaseRuntime", libs.liquibase.core)
    add("liquibaseRuntime", libs.picocli)
    add("liquibaseRuntime", libs.snakeyaml)
    add("liquibaseRuntime", libs.driver.postgresql)

    developmentOnly(libs.spring.boot.devtools)

    annotationProcessor(libs.lombok)

    runtimeOnly(libs.driver.postgresql)

    testRuntimeOnly(libs.driver.h2)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}



val envProperties = Properties()
val defaultEnvPropertiesFile = file("default.env")
val envPropertiesFile = file(".env")

if (defaultEnvPropertiesFile.exists()) {
    defaultEnvPropertiesFile.reader(Charsets.UTF_8).use { reader ->
        envProperties.load(reader)
    }
}

if (envPropertiesFile.exists()) {
    envPropertiesFile.reader(Charsets.UTF_8).use { reader ->
        envProperties.load(reader)
    }
}


liquibase {
    jvmArgs = arrayOf(
        "-Dliquibase.command.changeLogFile=src/main/resources/db/changelog/db.changelog-master.yaml",
        "-Dliquibase.command.url=${System.getenv("DB_URL") ?: envProperties.getProperty("DB_URL")}",
        "-Dliquibase.command.username=${System.getenv("DB_USER") ?: envProperties.getProperty("DB_USER")}",
        "-Dliquibase.command.password=${System.getenv("DB_PASSWORD") ?: envProperties.getProperty("DB_PASSWORD")}",
        "-Dliquibase.command.referenceUrl=${System.getenv("DB_URL") ?: envProperties.getProperty("DB_URL")}"
    )
    activities.register("main")
}
tasks.withType<Test> {
    environment("SPRING_PROFILES_ACTIVE", "test")
    useJUnitPlatform()
}

tasks.register("devBootRun") {
    doFirst {
        tasks.bootRun.get().environment("SPRING_PROFILES_ACTIVE", "dev")
    }
    finalizedBy(tasks.bootRun)
}

tasks.test {
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.check)
    violationRules {
        rule {
            limit {
                minimum = 0.5.toBigDecimal()
            }
        }
    }
}
