This is a dependencies fetcher plugin for Gradle-based JVM projects to grab non-JVM dependencies.

In other words: it allows you for example to fetch the [OpenCV](https://opencv.org) dependency
to be included into the native code of your Android project in the same way as it's done for regular dependencies.


## Quick start (Groovy-based build scripts)

The plugin was tested only with **Android Studio 2021.1.1** and **Gradle 7.2**.

0. Create a new Android project or use an existing one.

1. Make sure the Gradle Plugin Portal is included as repository for build script plugins 
   and the fetcher plugin is declared 
   (the latest available version can be checked
   [at the Gradle Plugin Portal](https://plugins.gradle.org/plugin/io.github.andrew-k-21-12.dependencies-fetcher)):
    ```groovy
    // settings.gradle
    pluginManagement {
        repositories {
            gradlePluginPortal() // <- add this line
            // ...
        }
        // ...
        plugins {
            // ...
            id 'io.github.andrew-k-21-12.dependencies-fetcher' version '2.0.0' // <- add this line
        }
    }
    ```

2. Include the plugin into the target project's module:
    ```groovy
    // For Android projects it's usually app/build.gradle
    plugins {
        // ...  
        id 'io.github.andrew-k-21-12.dependencies-fetcher' // <- add this line
    }
    ```
   
3. Declare the repo to grab a native dependency from:
    ```groovy
    // For Android projects it's usually app/build.gradle
    // Make sure the repositoriesMode is set to RepositoriesMode.PREFER_PROJECT for dependencyResolutionManagement
    plugins {
        // ...    
    }
    repositories {
        // ...
        new io.github.andrewk2112.dependenciesfetcher.GitHubRepositoryFactory().create(it) // <- add this line
    }
    ```
   
4. Provide the path to the unpacked dependency for CMake. 
   The plugin unpacks fetched dependencies into the `unpacked` folder inside of the module's `build` directory:
    ```groovy
    // For Android projects it's usually app/build.gradle
    android {
        // ...
        defaultConfig {
            // ...
            externalNativeBuild { // <- add this block
                cmake {
                    arguments "-DOpenCV_DIR=${layout.buildDirectory.dir('unpacked').get()}" +
                              '/OpenCV-android-sdk/sdk/native/jni'
                }
            }                     // <-
        }
        // ...
    }
    ```
   
5. Declare a dependency to be fetched and unpacked.
   For GitHub repositories it has the following format:
   `<username>:<reponame>:<version>[:classifier]@<extension>`.
   Supported extension types are `tar.gz` and `zip`.
    ```groovy
    // For Android projects it's usually app/build.gradle
    dependencies {
        unpackOnly 'opencv:opencv:4.5.3:android-sdk@zip' // <- add this line
        // ...
    }
    ```

6. Find and link the unpacked dependency in the native CMake-based configuration:
    ```cmake
    # For Android projects it's usually app/src/main/cpp/CMakeLists.txt
    # ...
    find_package(OpenCV 4 COMPONENTS imgproc ml)     # <- add these lines
    target_link_libraries(native-lib ${OpenCV_LIBS}) # <- "native-lib" is a target to link the native dependency to
    ```

Sync Gradle, run the project (as dependencies are fetched only on a requested build) 
and make sure the fetched library is available in the C++ code.

To get insights about other integration modes and check how to include the plugin for KTS-based build scripts,
review the **sandbox** project.


## Why this fetcher plugin?

It won't take lots of time to find other libraries doing something similar. 
However under a closer look they have some drawbacks
(at least when you want to include only native [OpenCV](https://opencv.org) libraries):

1. Google's [Prefab](https://google.github.io/prefab). This is something I started from. 
   Had to drop it because of its build system agnostic package-describing metadata:
   in the current version we can use only simple JSON descriptions, no additional CMake scripts are allowed.
   At the same time if you take a look inside of some unpacked [OpenCV Android release](https://opencv.org/releases),
   you'll see that it has lots of CMake scripts required for a proper integration.

2. [JVM-only Gradle OpenCV dependencies](https://github.com/quickbirdstudios/opencv-android). 
   In my particular case there is no interest to use [OpenCV](https://opencv.org) in any JVM-based environment,
   so had to skip it too.

3. Some utility to prepare and integrate native libraries from their headers and binaries into Gradle projects.
   It looks reasonable, but I didn't want to prepare each library release manually,
   plus some difficulties with CMake scripts could appear.
