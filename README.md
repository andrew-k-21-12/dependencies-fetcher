## Where am I?

This is a simple dependencies fetcher to be used inside of Gradle build scripts.

The primary objective of this library is to seamlessly fetch the [OpenCV](https://opencv.org) dependency 
to be included into the native code of Android projects:

```groovy
// ...
def pathToOpenCV = dependenciesFetcher.fetchDependency(urlToOpenCV, ArchiveType.ZIP, 'OpenCV').absolutePath

android {
    // ...
    defaultConfig {
        // ...
        externalNativeBuild {
            cmake {
                arguments "-DOpenCV_DIR=$pathToOpenCV/OpenCV-android-sdk/sdk/native/jni"
            }
        }
    }
    // ...
}
```


## Usage

The library was tested only with **Android Studio 4.1.3**, **4.2.1**, **4.2.2**
on macOS Catalina 10.15.6 and Big Sur 11.2.3.

To use this dependencies fetcher:

1. Create a new Android project or use an existing one.

2. Inside the project-level `build.gradle` file add the following buildscript dependency:
   ```groovy
   buildscript {
       // ...
       dependencies {
           // ...
           classpath 'io.github.andrew-k-21-12:dependencies-fetcher:1.0.2'
       } 
   }
   ```
   The latest available version of the fetcher can be checked at
   [Maven Central](https://repo1.maven.org/maven2/io/github/andrew-k-21-12/dependencies-fetcher).
   Otherwise, a local distribution of the fetcher can be used. Replace
   ```groovy
   classpath 'io.github.andrew-k-21-12:dependencies-fetcher:1.0.2'
   ```
   with
   ```groovy
   classpath files('path/dependencies-fetcher-1.0.2.jar')
   ```
   where `path` should be replaced with some actual path to the compiled fetcher library 
   (see the compilation guide in the `library` module's `README.md`).

3. Open a `build.gradle` file of the module you want to use the fetcher in (e.g. `app/build.gradle`)
   and add required configurations into it:
   ```groovy
   plugins {
       // ...
   }
   
   import io.github.andrewk2112.dependenciesfetcher.*
   def dependenciesFetcher = new DependenciesFetcher(
       // All fetched dependencies will be stored inside of this directory: add it to .gitignore.
       new File(projectDir, 'dependencies'),
       // To print the progress of fetching into the console: this line can be removed.
       new SimplePrintingLogger()
   )
   def urlToOpenCV = 'https://sourceforge.net/projects/opencvlibrary/files/4.5.2/opencv-4.5.2-android-sdk.zip/download'
   // Fetching the dependency, getting its local path.
   def pathToOpenCV = dependenciesFetcher.fetchDependency(urlToOpenCV, ArchiveType.ZIP, 'OpenCV').absolutePath
   
   android {
       // ...
       defaultConfig {
           // ...
           externalNativeBuild {
               cmake {
                   // Providing an argument to CMake to locate the fetched OpenCV.
                   arguments "-DOpenCV_DIR=$pathToOpenCV/OpenCV-android-sdk/sdk/native/jni"
               }
           }
       }
       // ...
   }

   // Cleaning up all fetched dependencies with the corresponding task.
   clean.doFirst {
       dependenciesFetcher.cleanUpAllFetchedDependencies()
   }
   ```

4. Open your project's `CMakeLists.txt`, make sure it can find and link the fetched package:
   ```
   // ...
   find_package(OpenCV 4 QUIET COMPONENTS imgproc ml)
   if(OpenCV_FOUND)
       target_link_libraries(native-lib ${OpenCV_LIBS})
   else()
       message(WARNING "OpenCV is not found!")
   endif()
   ```
   *It's preferred to use a stricter version of `find_package` with `REQUIRED`
   but starting from Android Studio 4.2.2 it fails to perform project's clean in this case.*

5. Sync Gradle, make sure the fetched library is available in the C++ code.


## Why this fetcher?

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


## To be done

1. Correct and automatic local directories to place dependencies into.

2. Automatic clean-up.

3. Refactor all scripts into KTS.

4. CI?
