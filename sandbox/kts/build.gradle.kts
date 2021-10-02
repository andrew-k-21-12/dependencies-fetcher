plugins {
    id("com.android.application")
    kotlin("android")

    // 1. Include the plugin.
    id("io.github.andrew-k-21-12.dependencies-fetcher")

}
repositories {
    google()
    mavenCentral()

    // 2. Declare the repo to grab a native dependency from.
    gitHub()

}
android {
    compileSdk = SandboxConfigs.compileSdk
    defaultConfig {
        applicationId = SandboxConfigs.applicationId
        minSdk        = SandboxConfigs.minSdk
        targetSdk     = SandboxConfigs.targetSdk
        versionCode   = SandboxConfigs.versionCode
        versionName   = SandboxConfigs.versionName
        testInstrumentationRunner = SandboxConfigs.testInstrumentationRunner

        // 3. Provide the path to the unpacked dependency for CMake.
        externalNativeBuild {
            cmake {
                arguments("-DOpenCV_DIR=${layout.buildDirectory.dir("unpacked").get()}" +
                          "/OpenCV-android-sdk/sdk/native/jni")
            }
        }

    }
    sourceSets {
        named("main") {
            java.srcDirs(SandboxConfigs.javaSrcDirs)
            res.srcDirs(SandboxConfigs.resSrcDir)
            manifest.srcFile(SandboxConfigs.manifestSrcFile)
        }
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    externalNativeBuild {
        cmake {
            path(file(SandboxConfigs.pathToCmakeFile))
            version = SandboxConfigs.cmakeVersion
        }
    }
    buildFeatures {
        viewBinding = true
    }
}
dependencies {

    // 4. Declare a dependency to be fetched and unpacked.
    unpackOnly("opencv:opencv:4.5.3:android-sdk@zip")

    implementation(libs.bundles.coreCompat)
    implementation(libs.bundles.ui)
}
