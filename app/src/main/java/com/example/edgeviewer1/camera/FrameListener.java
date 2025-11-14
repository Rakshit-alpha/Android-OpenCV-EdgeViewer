package com.example.edgeviewer1.camera;

import android.graphics.Bitmap;

public interface FrameListener {
    void onFrameAvailable(Bitmap frame);
}
