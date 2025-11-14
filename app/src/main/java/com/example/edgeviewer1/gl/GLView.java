package com.example.edgeviewer1.gl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLView extends GLSurfaceView {

    private final FrameRenderer renderer;

    public GLView(Context context) {
        this(context, null);
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);
        renderer = new FrameRenderer();
        setRenderer(renderer);

        // We only render when a new frame comes
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void updateFrame(byte[] rgbaBytes, int width, int height) {
        renderer.updateFrame(rgbaBytes, width, height);
        requestRender();
    }

    private static class FrameRenderer implements GLSurfaceView.Renderer {

        private int textureId = 0;
        private int programId = 0;

        private int positionHandle = 0;
        private int texCoordHandle = 0;
        private int textureUniformHandle = 0;

        private ByteBuffer frameBuffer = null;
        private int frameWidth = 0;
        private int frameHeight = 0;

        // Fullscreen quad (X,Y)
        private final float[] vertexCoords = new float[]{
                -1f, -1f,
                1f, -1f,
                -1f,  1f,
                1f,  1f
        };

        // Texture coords
        private final float[] texCoords = new float[]{
                0f, 1f,
                1f, 1f,
                0f, 0f,
                1f, 0f
        };

        private final FloatBuffer vertexBuffer;
        private final FloatBuffer texCoordBuffer;

        FrameRenderer() {
            vertexBuffer = ByteBuffer
                    .allocateDirect(vertexCoords.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            vertexBuffer.put(vertexCoords);
            vertexBuffer.position(0);

            texCoordBuffer = ByteBuffer
                    .allocateDirect(texCoords.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            texCoordBuffer.put(texCoords);
            texCoordBuffer.position(0);
        }

        void updateFrame(byte[] bytes, int width, int height) {
            if (frameBuffer == null || frameWidth != width || frameHeight != height) {
                frameWidth = width;
                frameHeight = height;
                frameBuffer = ByteBuffer.allocateDirect(width * height * 4);
            }
            frameBuffer.clear();
            frameBuffer.put(bytes);
            frameBuffer.position(0);
        }

        @Override
        public void onSurfaceCreated(GL10 unused, EGLConfig config) {
            GLES20.glClearColor(0f, 0f, 0f, 1f);
            programId = createProgram(VERT_SHADER, FRAG_SHADER);
            GLES20.glUseProgram(programId);

            positionHandle = GLES20.glGetAttribLocation(programId, "aPosition");
            texCoordHandle = GLES20.glGetAttribLocation(programId, "aTexCoord");
            textureUniformHandle = GLES20.glGetUniformLocation(programId, "uTexture");

            textureId = createTexture();
        }

        @Override
        public void onSurfaceChanged(GL10 unused, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 unused) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            if (frameBuffer == null) {
                return;
            }

            GLES20.glUseProgram(programId);

            // Update texture pixels
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            if (frameWidth > 0 && frameHeight > 0) {
                frameBuffer.position(0);
                GLES20.glTexImage2D(
                        GLES20.GL_TEXTURE_2D,
                        0,
                        GLES20.GL_RGBA,
                        frameWidth,
                        frameHeight,
                        0,
                        GLES20.GL_RGBA,
                        GLES20.GL_UNSIGNED_BYTE,
                        frameBuffer
                );
            }

            // Set vertex data
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(
                    positionHandle,
                    2,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    vertexBuffer
            );

            // Set texture coords
            GLES20.glEnableVertexAttribArray(texCoordHandle);
            GLES20.glVertexAttribPointer(
                    texCoordHandle,
                    2,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    texCoordBuffer
            );

            GLES20.glUniform1i(textureUniformHandle, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(texCoordHandle);
        }

        private int createTexture() {
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            int texId = textures[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

            GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR
            );
            GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR
            );
            GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE
            );
            GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE
            );

            return texId;
        }

        private int loadShader(int type, String source) {
            int shader = GLES20.glCreateShader(type);
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            return shader;
        }

        private int createProgram(String vertexSource, String fragmentSource) {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
            int program = GLES20.glCreateProgram();
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            GLES20.glLinkProgram(program);
            return program;
        }

        private static final String VERT_SHADER =
                "attribute vec4 aPosition;\n" +
                        "attribute vec2 aTexCoord;\n" +
                        "varying vec2 vTexCoord;\n" +
                        "void main() {\n" +
                        "    gl_Position = aPosition;\n" +
                        "    vTexCoord = aTexCoord;\n" +
                        "}\n";

        private static final String FRAG_SHADER =
                "precision mediump float;\n" +
                        "varying vec2 vTexCoord;\n" +
                        "uniform sampler2D uTexture;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = texture2D(uTexture, vTexCoord);\n" +
                        "}\n";
    }
}
