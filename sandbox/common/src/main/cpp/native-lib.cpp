#include <jni.h>
#include <string>

#include <opencv2/opencv.hpp>

extern "C" JNIEXPORT jstring JNICALL
Java_io_github_andrewk2112_dependenciesfetcher_sandbox_MainActivity_stringFromJNI
(
    JNIEnv* env,
    jobject
)
{
    std::string hello = "Hello from C++, OpenCV = " + cv::getVersionString();
    return env->NewStringUTF(hello.c_str());
}
