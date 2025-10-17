plugins {
    id("java-conventions")
}

springBoot {
    mainClass = "me.heyner.stashless.AuthorizationServerApplication"
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.authorization.server)
}