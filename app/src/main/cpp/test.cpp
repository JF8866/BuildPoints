#include "build_points.h"
#include <cmath>
#include <cstdio>

int mainxxx() {


    float center_point[2] = {75, 200};
    float radius = 1000;
    float start_deg = -90;
    float end_deg = 90;
    float delta_deg = 5;

    float start_point[2] = {150, 0};
    float end_point[2] = {75, 75};
    float distance = 1000;
    float interval = 50;
    float direction_deg = 45;
    // int track_mode=NOT_REPEAT_ROUTE;


    // int track_mode=REPEAT_ROUTE;
    int track_mode = NOT_REPEAT_ROUTE;
    float result_points[500] = {0};
    int num;

    float left1_point[2]/*={sqrt(2)/2.f,sqrt(2)/2.f}*/;
    left1_point[0] = sqrt(2) / 2.f;
    left1_point[1] = sqrt(2) / 2.f;
    float right1_point[2]/*={3.f*sqrt(2)/2.f,sqrt(2)/-2.f}*/;
    right1_point[0] = 3.f * sqrt(2) / 2.f;
    right1_point[1] = sqrt(2) / -2.f;

    num = build_center_radiate_points(center_point,
                                      radius,
                                      start_deg,
                                      end_deg,
                                      delta_deg,
                                      track_mode,
                                      result_points);

    printf("center_radiate_points:\n");
    printf("num: %d\n", num);
    for (int i = 0; i < num; ++i) {
        printf("point%d: ", i);
        printf("%.2f  ", result_points[i * 2]);
        printf("%.2f\n", result_points[i * 2 + 1]);
    }
    printf("============================================================\n");


    num = build_parallel_points(start_point,
                                end_point,
                                distance,
                                interval,
                                direction_deg,
                                track_mode,
                                result_points);

    printf("parallel_points:\n");
    printf("num: %d\n", num);
    for (int i = 0; i < num; ++i) {
        printf("point%d: ", i);
        printf("%.2f  ", result_points[i * 2]);
        printf("%.2f\n", result_points[i * 2 + 1]);
    }
    printf("============================================================\n");

    float test_point[2] = {1, 1};
    float trans_result[2] = {0};
    trans_coordinate(left1_point, right1_point, test_point, trans_result, 1);

    printf("trans_result:%.2f, %.2f\n", trans_result[0], trans_result[1]);
    printf("============================================================\n");

    float rotated_points[500] = {0};
    float rotate_center_point[2] = {75, 75};
    float theta_deg = 45;
    rotate(result_points, rotated_points, rotate_center_point, theta_deg, num);
    printf("rotated_points:\n");
    printf("num: %d\n", num);
    for (int i = 0; i < num; ++i) {
        printf("point%d: ", i);
        printf("%.2f  ", rotated_points[i * 2]);
        printf("%.2f\n", rotated_points[i * 2 + 1]);
    }
    printf("============================================================\n");


    float shifted_points[500] = {0};
    float shift_vector[2] = {100, 100};
    shift(rotated_points, shifted_points, shift_vector, num);
    printf("shifted_points:\n");
    printf("num: %d\n", num);
    for (int i = 0; i < num; ++i) {
        printf("point%d: ", i);
        printf("%.2f  ", shifted_points[i * 2]);
        printf("%.2f\n", shifted_points[i * 2 + 1]);
    }
    printf("============================================================\n");


    return 0;
}