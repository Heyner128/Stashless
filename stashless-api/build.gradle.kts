plugins {
    id("java-conventions")
    id("spring-conventions")
}

springBoot {
    mainClass = "me.heyner.stashless.StashlessApplication"
}

projectProperties.prefix = "api"

dependencies {
    implementation(project(":stashless-core"))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.springdoc)
    implementation(libs.modelmapper)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}