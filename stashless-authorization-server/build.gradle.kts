plugins {
    id("java-conventions")
}

springBoot {
    mainClass = "me.heyner.stashless.AuthorizationServerApplication"
}

dependencies {
    implementation(project(":stashless-core"))
    implementation(libs.modelmapper)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.authorization.server)

    implementation(libs.springdoc)

    implementation(libs.driver.postgresql)
    testImplementation(libs.driver.h2)

    testImplementation(libs.spring.boot.starter.test)
}