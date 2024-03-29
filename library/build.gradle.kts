repositories {
    mavenCentral() // to grab the Kotlin JVM plugin from
}

plugins {
    kotlin("jvm") version "1.7.20" // the plugin uses both Kotlin
    id("groovy")                   // and Groovy sources
    id("java-gradle-plugin")       // and is delivered via Gradle Plugins
    id("com.gradle.plugin-publish") version "1.1.0"
    id("maven-publish") // to generate the published metadata correctly without the unnecessary prefix in the group
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8 // all the sources are treated
    targetCompatibility = JavaVersion.VERSION_1_8 // and compiled into byte code of this version
}

// To use Kotlin classes in the Groovy API, their compiled versions should be included into the corresponding classpath.
tasks.named<GroovyCompile>("compileGroovy") {
    val compileKotlin = tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin")
    dependsOn(compileKotlin)
    classpath += files(compileKotlin.get().destinationDirectory)
}

// All metadata and configurations for the output plugin.
group   = "io.github.andrew-k-21-12"
version = "2.1.0"
gradlePlugin {
    plugins {
        create("dependenciesFetcher") {
            id                  = "$group.${rootProject.name}"
            displayName         = "Dependencies Fetcher"
            description         = "A simple dependencies fetcher helpful to grab non-JVM dependencies (e.g. OpenCV)"
            implementationClass = "io.github.andrewk2112.dependenciesfetcher.DependenciesFetcherPlugin"
        }
    }
}

// Gradle Plugin Portal publishing metadata.
pluginBundle {
    website = "https://github.com/andrew-k-21-12/dependencies-fetcher"
    vcsUrl  = "https://github.com/andrew-k-21-12/dependencies-fetcher.git"
    tags    = listOf("dependencies", "fetcher", "native", "android")
}
