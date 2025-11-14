

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.spring.boot.gradle)
    implementation(libs.spring.dependency.management.gradle)
    implementation(libs.spotless.gradle)
    implementation(libs.errorprone.gradle)
    implementation(libs.node.gradle)
    implementation(files((libs as Any).javaClass.superclass.protectionDomain.codeSource.location))
}