import com.github.gradle.node.npm.task.NpmTask

plugins {
    java
    id("com.github.node-gradle.node")
}

val frontendDir = "$rootDir/web"

node {
    download = true
    version = "24.5.0"
    nodeProjectDir.set(file(frontendDir))
}

tasks.register("installNpm", NpmTask::class) {
    workingDir = file(frontendDir)
    inputs.dir(frontendDir)
    group = BasePlugin.BUILD_GROUP
    args.set(listOf("install"))
}

tasks.register("buildNpm", NpmTask::class) {
    dependsOn("installNpm")
    workingDir = file(frontendDir)
    inputs.dir(frontendDir)
    group = BasePlugin.BUILD_GROUP
    args.set(listOf("run","build:auth"))
}

tasks.processResources {
    dependsOn("buildNpm")
    from("$frontendDir/dist/auth") {
        into("/")
    }
}