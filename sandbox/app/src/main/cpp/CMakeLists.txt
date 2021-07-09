cmake_minimum_required(VERSION 3.10.2)

project(sandbox)

add_library(native-lib SHARED native-lib.cpp)

find_package(OpenCV 4 QUIET COMPONENTS imgproc ml)
if(OpenCV_FOUND)
    target_link_libraries(native-lib ${OpenCV_LIBS})
else()
    message(WARNING "OpenCV is not found!")
endif()
