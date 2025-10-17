import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    java
    id("jacoco-conventions")
    id("spotless-conventions")
    id("errorprone-conventions")
    id("spring-conventions")
}

group = "me.heyner"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
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

    implementation(libs.jspecify)

    developmentOnly(libs.spring.boot.devtools)

    annotationProcessor(libs.lombok)

    runtimeOnly(libs.driver.postgresql)

    testRuntimeOnly(libs.driver.h2)
}

