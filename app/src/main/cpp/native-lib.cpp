#include <jni.h>
#include <string>
#include <android/log.h>
#include "build_points.h"

#define LOG_I(...) __android_log_print(ANDROID_LOG_INFO, "JNI BuildPoints", __VA_ARGS__)
#define LOG_W(...) __android_log_print(ANDROID_LOG_WARN, "JNI BuildPoints", __VA_ARGS__)
#define LOG_E(...) __android_log_print(ANDROID_LOG_ERROR, "JNI BuildPoints", __VA_ARGS__)

extern "C"
JNIEXPORT jint JNICALL
Java_site_feiyuliuxing_buildpoints_BuildPoints_buildCenterRadiatePoints(
        JNIEnv *env, jobject thiz,
        jfloatArray center_point,
        jfloat radius,
        jfloat start_deg,
        jfloat end_deg,
        jfloat delta_deg,
        jint track_mode,
        jfloatArray result_points
) {
    jboolean isCopy = JNI_FALSE;
    jfloat *cpoint = env->GetFloatArrayElements(center_point, &isCopy);
    jfloat *rpoints = env->GetFloatArrayElements(result_points, &isCopy);
    jint point_count = build_center_radiate_points(
            cpoint, radius, start_deg, end_deg,
            delta_deg, track_mode, rpoints
    );
    env->ReleaseFloatArrayElements(center_point, cpoint, 0);
    // 更新到原始的 Java 数组中
    env->ReleaseFloatArrayElements(result_points, rpoints, 0);
    return point_count;
}

extern "C"
JNIEXPORT jint JNICALL
Java_site_feiyuliuxing_buildpoints_BuildPoints_buildParallelPoints(
        JNIEnv *env, jobject thiz,
        jfloatArray start_point,
        jfloatArray end_point,
        jfloat distance,
        jfloat interval,
        jfloat direction_deg,
        jint track_mode,
        jfloatArray result_points
) {
    jfloat *start = env->GetFloatArrayElements(start_point, nullptr);
    jfloat *end = env->GetFloatArrayElements(end_point, nullptr);
    jfloat *result = env->GetFloatArrayElements(result_points, nullptr);

    jint point_count = build_parallel_points(
            start, end, distance, interval,
            direction_deg, track_mode, result
    );

    env->ReleaseFloatArrayElements(start_point, start, 0);
    env->ReleaseFloatArrayElements(end_point, end, 0);
    env->ReleaseFloatArrayElements(result_points, result, 0);
    return point_count;
}

extern "C"
JNIEXPORT jint JNICALL
Java_site_feiyuliuxing_buildpoints_BuildPoints_transCoordinate(
        JNIEnv *env, jobject thiz,
        jfloatArray left1_point,
        jfloatArray right1_point,
        jfloatArray input_points,
        jfloatArray result_points
) {
    //顶点个数
    jint num = env->GetArrayLength(input_points) / 2;
    jfloat *left1 = env->GetFloatArrayElements(left1_point, nullptr);
    jfloat *right1 = env->GetFloatArrayElements(right1_point, nullptr);
    jfloat *input = env->GetFloatArrayElements(input_points, nullptr);
    jfloat *result = env->GetFloatArrayElements(result_points, nullptr);

    jint res = trans_coordinate(left1, right1, input, result, num);

    env->ReleaseFloatArrayElements(left1_point, left1, 0);
    env->ReleaseFloatArrayElements(right1_point, right1, 0);
    env->ReleaseFloatArrayElements(input_points, input, 0);
    env->ReleaseFloatArrayElements(result_points, result, 0);
    return res;
}

extern "C"
JNIEXPORT jint JNICALL
Java_site_feiyuliuxing_buildpoints_BuildPoints_rotate(
        JNIEnv *env, jobject thiz,
        jfloatArray input_points,
        jfloatArray result_points,
        jfloat cx, jfloat cy,
        jfloat theta_deg
) {
    //顶点个数
    jint num = env->GetArrayLength(input_points) / 2;
//    LOG_I("rotate 输入顶点个数 %d", num);
    jfloat *input = env->GetFloatArrayElements(input_points, nullptr);
    jfloat center_point[2] = { cx, cy };
    jfloat *result = env->GetFloatArrayElements(result_points, nullptr);

    jint res = rotate(input, result, center_point, theta_deg, num);

    env->ReleaseFloatArrayElements(input_points, input, 0);
    env->ReleaseFloatArrayElements(result_points, result, 0);
    return res;
}

extern "C"
JNIEXPORT jint JNICALL
Java_site_feiyuliuxing_buildpoints_BuildPoints_shift(
        JNIEnv *env, jobject thiz,
        jfloatArray input_points,
        jfloatArray result_points,
        jfloat x_offset, jfloat y_offset
) {
    //顶点个数
    jint num = env->GetArrayLength(input_points) / 2;
//    LOG_I("shift 输入顶点个数 %d", num);
    jfloat *input = env->GetFloatArrayElements(input_points, nullptr);
    jfloat shift_vector[2] = { x_offset, y_offset };
    jfloat *result = env->GetFloatArrayElements(result_points, nullptr);

    jint res = shift(input, result, shift_vector, num);

    env->ReleaseFloatArrayElements(input_points, input, 0);
    env->ReleaseFloatArrayElements(result_points, result, 0);
    return res;
}