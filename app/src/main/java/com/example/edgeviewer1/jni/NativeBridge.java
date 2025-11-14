package com.example.edgeviewer1.jni;

public class NativeBridge {

    static {
        System.loadLibrary("edgeviewer-lib");
    }

    // Gray processing (already existed)
    public static native byte[] processFrameToGray(
            byte[] inputRgba,
            int width,
            int height
    );

    // Edge processing (converted from your Kotlin external fun)
    public static native byte[] processFrameToEdges(
            byte[] inputRgba,
            int width,
            int height
    );
}
