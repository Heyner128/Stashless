plugins {
    id("java-conventions")
    id("liquibase-conventions")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.devtools)

    implementation(libs.modelmapper)

    implementation(libs.springdoc)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}