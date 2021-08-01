plugins {
    kotlin("jvm") version "1.4.32"
    id("java-gradle-plugin")                                    // to use Gradle plugin APIs
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0" // simplifies publishing
    id("org.jetbrains.dokka") version "1.4.32"                  // docs generator
}

// All identifiers of the library.
group = "io.github.andrew-k-21-12"
val artifactId = rootProject.name
ext {
    set("artifactId", artifactId)
}
version = "1.0.3.1"

// Declarations to use plugin APIs in target buildscripts.
gradlePlugin {
    plugins {
        create(artifactId) {
            id = "$group.$artifactId"
            implementationClass = "io.github.andrewk2112.dependenciesfetcher.plugin.DependenciesFetcherPlugin"
        }
    }
}

// Java compatibility declaration is strictly required to avoid Java version errors in integrations.
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

// Dependencies sources and dependencies itself.
repositories {
    mavenCentral()
}
dependencies {}

// All publishing scripts.
apply(from = "${rootDir}/scripts/publish-root.gradle")
apply(from = "${rootProject.projectDir}/scripts/publish-module.gradle")
