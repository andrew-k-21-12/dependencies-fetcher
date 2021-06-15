This is a simple dependencies fetcher 
to be used inside of Gradle build scripts.

The primary objective of this library was 
to seamlessly fetch the [OpenCV](https://opencv.org) dependency
to be included into the native code of Android projects:

```groovy
externalNativeBuild {
    cmake {
        arguments "-DOpenCV_DIR=${dependenciesFetcher.fetchDependency(urlToDownloadOpenCV, ArchiveType.ZIP, "OpenCV").absolutePath}/OpenCV-android-sdk/sdk/native/jni"
    }
}
```


## Compilation

From the root directory of this repo execute `./gradlew jar`

The compiled JAR should appear in the `build/libs` directory.


## Usage

The library was tested only in **Android Studio 4.1.3** 
on macOS Catalina 10.15.6.

To use this dependencies fetcher:

1. Create a new Android project or use an existing one.
2. Create a `buildSrc` folder inside of the root directory of the project.
3. Inside of this `buildSrc` folder create a `build.gradle.kts` file
   with the following contents:
   ```kotlin
   plugins {
       `kotlin-dsl`
   }
   repositories {
       mavenCentral()
   }
   dependencies {
       implementation(fileTree("libs") { include("*.jar") })
   }
   ```
4. Inside of the created `buildSrc` folder
   at the same level with this `build.gradle.kts` file
   make a `libs` directory 
   and copy the compiled JAR-library inside.
5. Open a `build.gradle` file of the module you want to use the fetcher in
   (e.g. `app/build.gradle`) and add required configurations into it:
   ```groovy
   import io.github.andrew_k_21_12.dependenciesfetcher.*
   
   plugins {
       // ...
   }
   
   def urlToDownloadOpenCV = "https://sourceforge.net/projects/opencvlibrary/files/4.5.2/opencv-4.5.2-android-sdk.zip/download"
   def dependenciesFetcher = new DependenciesFetcher(
       new File("dependencies"),  // all fetched dependencies will be stored inside of this directory: add it to .gitignore
       new SimplePrintingLogger() // to print the progress of fetching into the console: this line can be removed
   )
   
   android {
       // ...
       defaultConfig {
           // ...
           externalNativeBuild {
               cmake {
                   // Providing an argument to CMake to locate the fetched OpenCV.
                   arguments "-DOpenCV_DIR=" +
                             dependenciesFetcher.fetchDependency(urlToDownloadOpenCV, ArchiveType.ZIP, "OpenCV").absolutePath +
                             "/OpenCV-android-sdk/sdk/native/jni"
                   cppFlags "-std=c++11"
               }
           }
       }
   }

   // Cleaning up all fetched dependencies with the corresponding task.
   clean.doFirst {
       dependenciesFetcher.cleanUpAllFetchedDependencies()
   }
   ```
6. Open your project's `CMakeLists.txt`, 
   make sure it can find and link the fetched package:
   ```
   // ...
   find_package(OpenCV 4 REQUIRED imgproc ml)
   target_link_libraries(native-lib ${OpenCV_LIBS})
   ```
7. Sync Gradle, make sure the fetched library is available in the C++ code.


## Why this fetcher?

It won't take a lot of time to find other libraries doing something similar.
However under a closer look they have some drawbacks
(at least when you want to include 
only native [OpenCV](https://opencv.org) libraries):

1. Google's [Prefab](https://google.github.io/prefab). 
   This is something I started from.
   Had to drop it because of its build system agnostic package-describing metadata:
   in the current version we can use only simple JSON descriptions,
   no additional CMake scripts are allowed.
   At the same time if you take a look inside of 
   some unpacked [OpenCV Android release](https://opencv.org/releases),
   you'll see that it has lots of CMake scripts required for a proper integration.
2. [JVM-only Gradle OpenCV dependencies](https://github.com/quickbirdstudios/opencv-android). 
   In my particular case there is no interest to use [OpenCV](https://opencv.org)
   in any JVM-based environment, so had to skip it too.
3. Some utility to prepare and integrate native libraries
   from their headers and binaries into Gradle projects.
   It looks reasonable, but I didn't want to prepare each library release manually,
   plus some difficulties with CMake scripts could appear.
