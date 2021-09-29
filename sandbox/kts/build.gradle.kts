plugins {
	id("com.android.application")
	kotlin("android")

	id("io.github.andrew-k-21-12.dependencies-fetcher")

}

repositories {
	google()
	mavenCentral()

	gitHub()

}

android {
	compileSdk = 31
	defaultConfig {
		applicationId = "io.github.andrewk2112.dependenciesfetcher.sandbox"
		minSdk    = 21
		targetSdk = 31
		versionCode = 1
		versionName = "1.0.0"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

		externalNativeBuild {
			cmake {
				arguments("-DOpenCV_DIR=${layout.buildDirectory.dir("unpacked").get()}" +
						  "/OpenCV-android-sdk/sdk/native/jni")
			}
		}

	}
	sourceSets {
		named("main") {
			java.srcDirs("../common/src/main/java")
			res.srcDirs("../common/src/main/res")
			manifest.srcFile("../common/src/main/AndroidManifest.xml")
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
			path(file("../common/src/main/cpp/CMakeLists.txt"))
			version = "3.18.1"
		}
	}

	buildFeatures {
		viewBinding = true
	}
}

dependencies {

	unpackOnly("opencv:opencv:4.5.3:android-sdk@zip")

	implementation("androidx.core:core-ktx:1.6.0")
	implementation("androidx.appcompat:appcompat:1.3.1")
	implementation("com.google.android.material:material:1.4.0")
	implementation("androidx.constraintlayout:constraintlayout:2.1.0")
}