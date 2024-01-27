#ifndef _BUILD_POINTS_
#define _BUILD_POINTS_


enum TRACK_MODE
{
    NOT_REPEAT_ROUTE,
    REPEAT_ROUTE,
};

// TODO 增加曲率半径输出 lines_radius


/*
input:
    start_point：起始点坐标
    end_point：终止点坐标
    line_radius：曲率
    num：需要生成的点数量
ouput:
    result_points： 接收点坐标的指针，指向一个足够大的数组，参考每条线两个点
return:
    生成点的数量
*/
int build_curve_points(float * start_point,
                       float* end_point,
                       float line_radius,
                       int num,
                       float* result_points);

/*
input:
    l0：车尾宽度
    l1：车长度
    l2：车头宽度
    distance_num：仿圆的圈数
    x_distance：x轴方向离车距离
    y_distance：y轴方向离车距离
ouput:
    result_points： 接收点坐标的指针，指向一个足够大的数组，参考每条线两个点
    lines_radius： 接收每两个点间线的曲率半径，直线的曲率半径为零
return:
    生成点的数量
*/
int build_imitation_circle_points( float l0,
                                   float l1,
                                   float l2,
                                   int distance_num,
                                   float* x_distances,
                                   float* y_distances,
                                   float* result_points,
                                   float* lines_radius
);



/*
input：
    center_point: 起点圆心坐标指针，指向一个长度2的数组
    radius：圆半径
    star_deg：起始角度，x轴正半轴为0度，逆时针为正方向
    end_deg：结束角度，x轴正半轴为0度，逆时针为正方向
    delta_deg：两条直线间的夹角
    track_mode: 是否重走返回再走下一条直线 0为不重走，1为重走
output:
    result_points： 接收点坐标的指针，指向一个足够大的数组，参考每条线两个点
    lines_radius： 接收每两个点间线的曲率半径，直线的曲率半径为零
return:
    生成点的数量
*/
int build_center_radiate_points( float* center_point,
                                 float radius,
                                 float start_deg,
                                 float end_deg,
                                 float delta_deg,
                                 int track_mode,
                                 float* result_points,
                                 float* lines_radius
);





/*
input:
    start_point: 起点坐标指针，指向一个长度2的数组
    end_point: 终点坐标指针，指向一个长度2的数组
    distance：直线长度
    interval：直线间隔
    direction_deg：直线方向，x轴正半轴为0度，逆时针为正方向
    track_mode: 是否重走返回再走下一条直线 0为不重走，1为重走
output:
    result_points：： 接收点坐标的指针，指向一个足够大的数组，参考每条线两个点
    lines_radius： 接收每两个点间线的曲率半径，直线的曲率半径为零
return:
    生成点的数量
*/
int build_parallel_points(float* start_point,
                          float* end_point,
                          float distance,
                          float interval,
                          float direction_deg,
                          int track_mode,
                          float* result_points,
                          float* lines_radius);

/*
input:
    left1_point：车轮廓左后角，左侧1米点在机器小车中的坐标，指向一个长度2的数组
    right1_point：车轮廓右后角，右侧1米点在机器小车中的坐标，指向一个长度2的数组
    input_points：标准坐标系中生成的点坐标
    num：点数量
output:
    result_points：接收转换后机器小车坐标系中的点坐标
*/
int trans_coordinate(float * left1_point,
                     float * right1_point,
                     float* input_points,
                     float* result_points,
                     int num);


/*
input:
    input_points：标准坐标系中生成的点坐标
    rotate_center_point：旋转中心
    theta_deg：旋转角度，逆时针为正
    num：点数量
output:
    result_points：接收转换后机器小车坐标系中的点坐标
*/
int rotate(float* input_points,
           float*result_points,
           float* rotate_center_point,
           float theta_deg,
           int num);


/*
input:
    input_points：标准坐标系中生成的点坐标
    shift_vector：平移向量
    num：点数量
output:
    result_points：接收转换后机器小车坐标系中的点坐标
*/
int shift(float* input_points,float*result_points, float* shift_vector,int num);


#endif