cmake_minimum_required(VERSION 3.22.1)

project("edgeviewer1")

# Native lib
add_library(
        edgeviewer-lib
        SHARED
        native-lib.cpp
        opencv_processor.cpp
)

# Android system libs
find_library(android-lib android)
find_library(log-lib log)
find_library(glesv2-lib GLESv2)

# ✅ C++ shared runtime (this is the one we need in APK)
find_library(cpp_shared-lib c++_shared)

# ✅ OpenCV SDK path (your path)
set(OPENCV_ANDROID_SDK_PATH
        "C:/Users/Anurag/Downloads/opencv-4.12.0-android-sdk/OpenCV-android-sdk/sdk"
)

include_directories(
        ${OPENCV_ANDROID_SDK_PATH}/native/jni/include
)

add_library(opencv_java4 SHARED IMPORTED)

set_target_properties(
        opencv_java4
        PROPERTIES
        IMPORTED_LOCATION
        ${OPENCV_ANDROID_SDK_PATH}/native/libs/${ANDROID_ABI}/libopencv_java4.so
)

# ✅ Link everything together
target_link_libraries(
        edgeviewer-lib
        ${android-lib}
        ${log-lib}
        ${glesv2-lib}
        ${cpp_shared-lib}   # <--- IMPORTANT
        opencv_java4
)
