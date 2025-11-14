#include "opencv_processor.hpp"
#include <opencv2/opencv.hpp>

using namespace cv;

void process_frame_to_gray(
        const unsigned char* inputRgba,
        int width,
        int height,
        std::vector<unsigned char>& outputRgba) {

    Mat rgba(height, width, CV_8UC4, (void*)inputRgba);

    Mat gray;
    cvtColor(rgba, gray, COLOR_RGBA2GRAY);

    Mat grayRgba;
    cvtColor(gray, grayRgba, COLOR_GRAY2RGBA);

    outputRgba.resize(width * height * 4);
    memcpy(outputRgba.data(), grayRgba.data, width * height * 4);
}

void process_frame_to_edges(
        const unsigned char* inputRgba,
        int width,
        int height,
        std::vector<unsigned char>& outputRgba) {

    Mat rgba(height, width, CV_8UC4, (void*)inputRgba);

    Mat gray;
    cvtColor(rgba, gray, COLOR_RGBA2GRAY);

    Mat edges;
    // Canny thresholds can be tuned
    Canny(gray, edges, 80, 150);

    Mat edgesRgba;
    cvtColor(edges, edgesRgba, COLOR_GRAY2RGBA);

    outputRgba.resize(width * height * 4);
    memcpy(outputRgba.data(), edgesRgba.data, width * height * 4);
}
