#pragma once

#include <vector>

void process_frame_to_gray(
        const unsigned char* inputRgba,
        int width,
        int height,
        std::vector<unsigned char>& outputRgba);

void process_frame_to_edges(
        const unsigned char* inputRgba,
        int width,
        int height,
        std::vector<unsigned char>& outputRgba);
