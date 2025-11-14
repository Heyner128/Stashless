import java.io.FileInputStream
import java.util.Properties
import kotlin.apply
import org.gradle.api.tasks.JavaExec
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the

plugins {
    java
}

val libs = the<LibrariesForLibs>()

val liquibaseRuntime by configurations.creating


fun loadEnvironmentProperties() : Properties {
    val environmentFile = file("${project.rootDir}/.env")
    return Properties().apply { load(FileInputStream(environmentFile)) }
}

fun liquibaseArgs(vararg command: String): List<String> {
    val properties = loadEnvironmentProperties()

    val url  = System.getenv("DB_URL") ?: properties["DB_URL"]
    val user = System.getenv("DB_USER") ?: properties["DB_USER"]
    val password = System.getenv("DB_PASSWORD") ?: properties["DB_PASSWORD"]

    val searchPath = "${projectDir.path}/src/main/resources"
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

dependencies {
    implementation(libs.liquibase.core)
    liquibaseRuntime(libs.liquibase.core)
    liquibaseRuntime(libs.driver.postgresql)
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