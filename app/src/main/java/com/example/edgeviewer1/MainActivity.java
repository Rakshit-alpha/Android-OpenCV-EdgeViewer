package com.example.edgeviewer1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.edgeviewer1.camera.CameraController;
import com.example.edgeviewer1.camera.FrameListener;
import com.example.edgeviewer1.gl.GLView;
import com.example.edgeviewer1.jni.NativeBridge;

import java.nio.ByteBuffer;

public class MainActivity extends ComponentActivity implements FrameListener {

    // Mode enum
    private enum ViewMode {
        NORMAL,
        GRAY,
        EDGE
    }

    private TextureView textureView;
    private GLView glView;
    private CameraController cameraController;

    private ViewMode currentMode = ViewMode.NORMAL;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView = findViewById(R.id.textureView);
        glView = findViewById(R.id.glView);
        Button toggleButton = findViewById(R.id.toggleModeBtn);

        // 3-mode cycle: NORMAL → GRAY → EDGE → NORMAL
        toggleButton.setOnClickListener(v -> {
            switch (currentMode) {
                case NORMAL:
                    currentMode = ViewMode.GRAY;
                    break;
                case GRAY:
                    currentMode = ViewMode.EDGE;
                    break;
                case EDGE:
                    currentMode = ViewMode.NORMAL;
                    break;
            }

            switch (currentMode) {
                case NORMAL:
                    toggleButton.setText("Mode: Normal");
                    break;
                case GRAY:
                    toggleButton.setText("Mode: Gray");
                    break;
                case EDGE:
                    toggleButton.setText("Mode: Edge");
                    break;
            }

            switch (currentMode) {
                case NORMAL:
                    glView.setVisibility(View.GONE);
                    break;
                case GRAY:
                case EDGE:
                    glView.setVisibility(View.VISIBLE);
                    break;
            }
        });

        if (hasCameraPermission()) {
            initCameraController();
        } else {
            requestCameraPermission();
        }
    }

    private void initCameraController() {
        cameraController = new CameraController(this, textureView, this);
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCameraController();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraController != null) {
            cameraController.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (cameraController != null) {
            cameraController.onPause();
        }
        super.onPause();
    }

    // FrameListener
    @Override
    public void onFrameAvailable(Bitmap frame) {
        // NORMAL: don't process, just show camera preview
        if (currentMode == ViewMode.NORMAL) {
            return;
        }

        int width = frame.getWidth();
        int height = frame.getHeight();

        Bitmap bmp = frame.copy(Bitmap.Config.ARGB_8888, false);

        ByteBuffer buffer = ByteBuffer.allocate(width * height * 4);
        bmp.copyPixelsToBuffer(buffer);
        byte[] rgbaBytes = buffer.array();

        byte[] processed;

        if (currentMode == ViewMode.GRAY) {
            processed = NativeBridge.processFrameToGray(rgbaBytes, width, height);
        } else { // ViewMode.EDGE
            processed = NativeBridge.processFrameToEdges(rgbaBytes, width, height);
        }

        runOnUiThread(() -> glView.updateFrame(processed, width, height));
    }
}
