import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import java.io.FileInputStream
import java.util.Locale
import java.util.Properties
import kotlin.apply
import org.gradle.api.tasks.JavaExec


plugins {
    java
    jacoco
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spotless)
    alias(libs.plugins.errorprone)
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
    options.errorprone.check("NullAway", CheckSeverity.ERROR)
    options.errorprone.option("NullAway:AnnotatedPackages", "me.heyner")
    options.errorprone.disable("JavaUtilDate")
    options.errorprone.disable("EqualsGetClass")
    options.errorprone.check("WildcardImport", CheckSeverity.ERROR)
    if (name.lowercase(Locale.getDefault()).contains("test") ) {
        options.errorprone.disable("NullAway")
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

val liquibaseRuntime by configurations.creating


fun loadEnvironmentProperties() : Properties {
    val environmentFile = file(".env")
    return Properties().apply { load(FileInputStream(environmentFile)) }
}

fun liquibaseArgs(vararg command: String): List<String> {
    val properties = loadEnvironmentProperties()

    val url  = System.getenv("DB_URL") ?: properties["DB_URL"]
    val user = System.getenv("DB_USER") ?: properties["DB_USER"]
    val password = System.getenv("DB_PASSWORD") ?: properties["DB_PASSWORD"]

    val searchPath = "${project.layout.buildDirectory.get().asFile}/resources/main"
    val changelogFile = "db/changelog/db.changelog-master.yaml"

    val args = mutableListOf(
        "--changeLogFile=$changelogFile",
        "--url=$url",
        "--username=$user",
        "--password=$password",
        "--classpath=$searchPath",
        "--logLevel=warn",
        "--defaultSchemaName=public"
    )

    args.addAll(command)
    return args
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

    errorprone(libs.errorprone)
    errorprone(libs.nullaway)
    implementation(libs.jspecify)

    liquibaseRuntime(libs.liquibase.core)
    liquibaseRuntime(libs.driver.postgresql)

    developmentOnly(libs.spring.boot.devtools)

    annotationProcessor(libs.lombok)

    runtimeOnly(libs.driver.postgresql)

    testRuntimeOnly(libs.driver.h2)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
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

tasks.register<JavaExec>("liquibaseUpdate") {
    group = "liquibase"
    description = "Run Liquibase update"
    classpath = liquibaseRuntime
    mainClass.set("liquibase.integration.commandline.Main")
    args = liquibaseArgs("update")
}

tasks.register<JavaExec>("liquibaseStatus") {
    group = "liquibase"
    description = "Show Liquibase status"
    classpath = liquibaseRuntime
    mainClass.set("liquibase.integration.commandline.Main")
    args = liquibaseArgs("status")
}

tasks.register<JavaExec>("liquibaseRollback") {
    group = "liquibase"
    description = "Rollback last Liquibase changeset"
    classpath = liquibaseRuntime
    mainClass.set("liquibase.integration.commandline.Main")
    args = liquibaseArgs("rollbackCount", "1")
}

tasks.register<JavaExec>("liquibaseDropAll") {
    group = "liquibase"
    description = "Drop all objects in the database"
    classpath = liquibaseRuntime
    mainClass.set("liquibase.integration.commandline.Main")
    args = liquibaseArgs("dropAll")
}
