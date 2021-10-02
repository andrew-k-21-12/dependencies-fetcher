cmake_minimum_required(VERSION 3.18.1)
project(sandbox)
add_library(native-lib SHARED native-lib.cpp)

# 6. Find and link the unpacked dependency.
find_package(OpenCV 4 COMPONENTS imgproc ml)
target_link_libraries(native-lib ${OpenCV_LIBS})
