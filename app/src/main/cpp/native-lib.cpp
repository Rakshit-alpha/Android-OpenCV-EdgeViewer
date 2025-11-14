#include <jni.h>
#include <vector>
#include "opencv_processor.hpp"

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_edgeviewer1_jni_NativeBridge_processFrameToGray(
        JNIEnv* env,
        jobject /* this */,
        jbyteArray inputRgba,
        jint width,
        jint height) {

    jsize length = env->GetArrayLength(inputRgba);
    if (length != width * height * 4) {
        return inputRgba;
    }

    std::vector<unsigned char> inputBuf(length);
    env->GetByteArrayRegion(inputRgba, 0, length,
                            reinterpret_cast<jbyte*>(inputBuf.data()));

    std::vector<unsigned char> outputBuf;
    process_frame_to_gray(inputBuf.data(), width, height, outputBuf);

    jbyteArray result = env->NewByteArray(outputBuf.size());
    env->SetByteArrayRegion(result, 0, outputBuf.size(),
                            reinterpret_cast<jbyte*>(outputBuf.data()));
    return result;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_edgeviewer1_jni_NativeBridge_processFrameToEdges(
        JNIEnv* env,
        jobject /* this */,
        jbyteArray inputRgba,
        jint width,
        jint height) {

    jsize length = env->GetArrayLength(inputRgba);
    if (length != width * height * 4) {
        return inputRgba;
    }

    std::vector<unsigned char> inputBuf(length);
    env->GetByteArrayRegion(inputRgba, 0, length,
                            reinterpret_cast<jbyte*>(inputBuf.data()));

    std::vector<unsigned char> outputBuf;
    process_frame_to_edges(inputBuf.data(), width, height, outputBuf);

    jbyteArray result = env->NewByteArray(outputBuf.size());
    env->SetByteArrayRegion(result, 0, outputBuf.size(),
                            reinterpret_cast<jbyte*>(outputBuf.data()));
    return result;
}
