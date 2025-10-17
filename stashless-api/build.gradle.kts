plugins {
    id("java-conventions")
    id("liquibase-conventions")
}

springBoot {
    mainClass = "me.heyner.stashless.StashlessApplication"
}

dependencies {
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.oauth2.authorization.server)
    implementation(libs.spring.session)
    implementation(libs.springdoc)
    implementation(libs.modelmapper)
    implementation(libs.bundles.jjwt)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}