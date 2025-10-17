import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import java.util.Locale

plugins {
    id("net.ltgt.errorprone")
}

val libs = the<LibrariesForLibs>()

tasks.withType<JavaCompile> {
    options.errorprone.check("NullAway", CheckSeverity.ERROR)
    options.errorprone.option("NullAway:AnnotatedPackages", "me.heyner")
    options.errorprone.disable("JavaUtilDate")
    options.errorprone.disable("EqualsGetClass")
    options.errorprone.check("WildcardImport", CheckSeverity.ERROR)
    if (name.lowercase(Locale.getDefault()).contains("test") ) {
        options.errorprone.disable("NullAway")
    }
}

dependencies {

    errorprone(libs.errorprone)
    errorprone(libs.nullaway)
}