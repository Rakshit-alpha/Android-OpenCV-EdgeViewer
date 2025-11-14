# 📌 EdgeViewer — Android + OpenCV (C++) + OpenGL ES + Web Viewer

This project is a real-time camera processing pipeline built for the **Android + OpenCV (C++) + OpenGL ES + Web (TypeScript)** technical assessment.

The Android app captures camera frames → sends them to C++ via JNI → processes them using OpenCV → renders the result using OpenGL ES.  
A separate web viewer displays a processed sample frame (Base64) with FPS and resolution details.

---

## 🚀 Features

### 📱 Android (Camera + Processing)
- Real-time camera capture using **Camera2 + TextureView**
- JNI communication between Java ↔ C++
- Native C++ OpenCV processing:
  - ✔ Grayscale conversion
  - ✔ Canny Edge Detection
- Rendering using **OpenGL ES 2.0**
- 3-mode processing:
  - Normal
  - Gray
  - Edge

### 🧠 Native C++ (OpenCV)
- High-performance frame operations
- Optimized RGBA → Gray / Edge → RGBA conversions
- Well-organized modular processor

### 🎨 OpenGL Renderer
- Uploads processed frames into texture
- Renders full-screen quad
- Smooth 10–20 FPS

### 🌐 TypeScript Web Viewer
- Minimal viewer using:
  - `index.html`
  - `src/main.ts`
  - `tsconfig.json`
- Displays:
  - A Base64 sample processed frame
  - FPS & resolution stats

---

## 🛠 Architecture

