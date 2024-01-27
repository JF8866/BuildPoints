#include <cfloat>
#define _USE_MATH_DEFINES
#include <cmath>
#include <cstdbool>
#include <cstdio>
#include "build_points.h"

#define MAX_DISTANCE_NUM 50

static float trans_matrix[3][3]={0};


static float theta_sin=0;
static float theta_cos=0;
static float theta_red=0;
static float anchor_points[4][2]={0};



int build_curve_points(float * start_point,
                       float* end_point,
                       float line_radius,
                       int num,
                       float* result_points)
{
    float k_theta=atan2(end_point[1]-start_point[1],end_point[0]-start_point[0]);
    float d_2=sqrt(pow(end_point[1]-start_point[1],2)+pow(end_point[0]-start_point[0],2))/2;

    float theta_start=k_theta+acos(d_2/line_radius)-M_PI;
    float theta_total=2*asin(d_2/line_radius);

    float theta_delta=theta_total/(num-1);

    // printf("k_theta %.2f\n",k_theta);
    // printf("d_2 %.2f\n",d_2);
    // printf("theta_start %.2f\n",theta_start);
    // printf("theta_total %.2f\n",theta_total);
    // printf("theta_delta %.2f\n",theta_delta);

    result_points[0]=start_point[0];
    result_points[1]=start_point[1];
    for(int i=1;i<num-1;++i){

        result_points[2*i]=start_point[0] + line_radius*(cos(theta_start+theta_delta*i) -cos(theta_start) );
        result_points[2*i+1]=start_point[1] + line_radius*(sin(theta_start+theta_delta*i) -sin(theta_start) );

    }

    result_points[2*(num-1)]=end_point[0];
    result_points[2*(num-1)+1]=end_point[1];

    return num;

}



// anchors index 0:right tail, 1:left tail, 2:left front, 3:right front

int init_anchor_points(float l0,
                       float l1,
                       float l2)
{
    float height=sqrt(pow(l1,2)-pow((l0-l2)/2,2));

    theta_sin=(l0-l2)/(2*l1);
    theta_cos=height/l1;
    theta_red=asin(theta_sin);

    anchor_points[0][0]=l0/2;
    anchor_points[0][1]=0;
    anchor_points[1][0]=-l0/2;
    anchor_points[1][1]=0;
    anchor_points[2][0]=-l2/2;
    anchor_points[2][1]=height;
    anchor_points[3][0]=l2/2;
    anchor_points[3][1]=height;

    // for(int i=0;i<4;++i){
    //     printf("anchor%d: ",i);
    //     printf("%.2f  ",anchor_points[i][0]);
    //     printf("%.2f\n",anchor_points[i][1]);
    // }


    return 0;
}


int build_imitation_circle_points( float l0,
                                   float l1,
                                   float l2,
                                   int distance_num,
                                   float* x_distances,
                                   float* y_distances,
                                   float* result_points,
                                   float* lines_radius
)
{


    init_anchor_points(l0,l1,l2);

    // start end distance and theta [corner_idx][distance_idx][start_end_distance_theta]
    float corner_start_end_distance_theta[4][MAX_DISTANCE_NUM][4]={0};

    // start end point [corner_idx][distance_idx][start_end_point]
    float corner_start_end_points[4][MAX_DISTANCE_NUM][4]={0};

    // corner radius [corner_idx][distance_idx]
    float corner_radius[4][MAX_DISTANCE_NUM]={0};


    for(int i=0;i<distance_num;++i){

        corner_start_end_distance_theta[0][i][0]=y_distances[i];
        corner_start_end_distance_theta[0][i][1]=-M_PI_2;
        corner_start_end_distance_theta[0][i][2]=x_distances[i];
        corner_start_end_distance_theta[0][i][3]=theta_red;

        corner_start_end_distance_theta[3][i][0]=x_distances[i];
        corner_start_end_distance_theta[3][i][1]=theta_red;
        corner_start_end_distance_theta[3][i][2]=y_distances[i];
        corner_start_end_distance_theta[3][i][3]=M_PI_2;

        corner_start_end_distance_theta[2][i][0]=y_distances[i];
        corner_start_end_distance_theta[2][i][1]=M_PI_2;
        corner_start_end_distance_theta[2][i][2]=x_distances[i];
        corner_start_end_distance_theta[2][i][3]=M_PI-theta_red;

        corner_start_end_distance_theta[1][i][0]=x_distances[i];
        corner_start_end_distance_theta[1][i][1]=M_PI-theta_red;
        corner_start_end_distance_theta[1][i][2]=y_distances[i];
        corner_start_end_distance_theta[1][i][3]=3*M_PI_2;

    }



    for(int i=0;i<distance_num;++i){

        for(int j=0;j<4;++j){

            corner_start_end_points[j][i][0]=(
                    anchor_points[j][0] +
                    corner_start_end_distance_theta[j][i][0]*cos(corner_start_end_distance_theta[j][i][1]));

            corner_start_end_points[j][i][1]=(
                    anchor_points[j][1] +
                    corner_start_end_distance_theta[j][i][0]*sin(corner_start_end_distance_theta[j][i][1]));


            corner_start_end_points[j][i][2]=(
                    anchor_points[j][0] +
                    corner_start_end_distance_theta[j][i][2]*cos(corner_start_end_distance_theta[j][i][3]));

            corner_start_end_points[j][i][3]=(
                    anchor_points[j][1] +
                    corner_start_end_distance_theta[j][i][2]*sin(corner_start_end_distance_theta[j][i][3]));

        }

    }



    for(int i=0;i<distance_num;++i)
    {
        corner_radius[0][i]=(pow(y_distances[i],2) +
                             pow(x_distances[i],2) +
                             2*y_distances[i]*x_distances[i]*theta_sin)/
                            (2*(y_distances[i]+x_distances[i]*theta_sin));

        corner_radius[1][i]=(pow(y_distances[i],2) +
                             pow(x_distances[i],2) +
                             2*y_distances[i]*x_distances[i]*theta_sin)/
                            (2*(y_distances[i]+x_distances[i]*theta_sin));

        corner_radius[2][i]=(pow(y_distances[i],2) +
                             pow(x_distances[i],2) -
                             2*y_distances[i]*x_distances[i]*theta_sin)/
                            (2*(y_distances[i]-x_distances[i]*theta_sin));

        corner_radius[3][i]=(pow(y_distances[i],2) +
                             pow(x_distances[i],2) -
                             2*y_distances[i]*x_distances[i]*theta_sin)/
                            (2*(y_distances[i]-x_distances[i]*theta_sin));

    }

    int point_idx=0;
    for(int i=0;i<distance_num;++i)
    {

        result_points[2*point_idx]=corner_start_end_points[0][i][0];
        result_points[2*point_idx+1]=corner_start_end_points[0][i][1];
        lines_radius[point_idx]=corner_radius[0][i];
        point_idx++;

        result_points[2*point_idx]=corner_start_end_points[0][i][2];
        result_points[2*point_idx+1]=corner_start_end_points[0][i][3];
        lines_radius[point_idx]=0;
        point_idx++;



        result_points[2*point_idx]=corner_start_end_points[3][i][0];
        result_points[2*point_idx+1]=corner_start_end_points[3][i][1];
        lines_radius[point_idx]=corner_radius[3][i];
        point_idx++;

        result_points[2*point_idx]=corner_start_end_points[3][i][2];
        result_points[2*point_idx+1]=corner_start_end_points[3][i][3];
        lines_radius[point_idx]=0;
        point_idx++;


        result_points[2*point_idx]=corner_start_end_points[2][i][0];
        result_points[2*point_idx+1]=corner_start_end_points[2][i][1];
        lines_radius[point_idx]=corner_radius[2][i];
        point_idx++;

        result_points[2*point_idx]=corner_start_end_points[2][i][2];
        result_points[2*point_idx+1]=corner_start_end_points[2][i][3];
        lines_radius[point_idx]=0;
        point_idx++;


        result_points[2*point_idx]=corner_start_end_points[1][i][0];
        result_points[2*point_idx+1]=corner_start_end_points[1][i][1];
        lines_radius[point_idx]=corner_radius[1][i];
        point_idx++;

        result_points[2*point_idx]=corner_start_end_points[1][i][2];
        result_points[2*point_idx+1]=corner_start_end_points[1][i][3];
        lines_radius[point_idx]=0;
        point_idx++;




        result_points[2*point_idx]=corner_start_end_points[0][i][0];
        result_points[2*point_idx+1]=corner_start_end_points[0][i][1];
        lines_radius[point_idx]=0;
        point_idx++;

    }

    return point_idx;
}


int build_center_radiate_points( float* center_point,
                                 float radius,
                                 float start_deg,
                                 float end_deg,
                                 float delta_deg,
                                 int track_mode,
                                 float* result_points,
                                 float* lines_radius)
{
    int current_point_idx=0;

    if(track_mode==REPEAT_ROUTE){
        while (true)
        {
            result_points[current_point_idx++]=center_point[0];
            result_points[current_point_idx++]=center_point[1];
            result_points[current_point_idx++]=center_point[0]+radius*cos(M_PI*start_deg/180.f);
            result_points[current_point_idx++]=center_point[1]+radius*sin(M_PI*start_deg/180.f);


            if((end_deg-start_deg)>FLT_EPSILON){
                start_deg+=delta_deg;
                if((start_deg-end_deg)>FLT_EPSILON){
                    start_deg=end_deg;
                }
            }
            else{
                break;
            }

        }

    }

    if(track_mode==NOT_REPEAT_ROUTE){
        while (true)
        {

            result_points[current_point_idx++]=center_point[0];
            result_points[current_point_idx++]=center_point[1];
            result_points[current_point_idx++]=center_point[0]+radius*cos(M_PI*start_deg/180.f);
            result_points[current_point_idx++]=center_point[1]+radius*sin(M_PI*start_deg/180.f);

            if((end_deg-start_deg)>FLT_EPSILON){
                start_deg+=delta_deg;
                if((start_deg-end_deg)>FLT_EPSILON){
                    start_deg=end_deg;
                }
            }
            else{
                break;
            }

            result_points[current_point_idx++]=center_point[0]+radius*cos(M_PI*start_deg/180.f);
            result_points[current_point_idx++]=center_point[1]+radius*sin(M_PI*start_deg/180.f);


            if((end_deg-start_deg)>FLT_EPSILON){
                start_deg+=delta_deg;
                if((start_deg-end_deg)>FLT_EPSILON){
                    start_deg=end_deg;
                }
            }
            else{
                result_points[current_point_idx++]=center_point[0];
                result_points[current_point_idx++]=center_point[1];
                break;
            }

        }

    }

    for(int i=0;i<(current_point_idx/2)-1;++i){
        lines_radius[i]=0;
    }

    return current_point_idx/2;
}


int build_parallel_points(float* start_point,
                          float* end_point,
                          float distance,
                          float interval,
                          float direction_deg,
                          int track_mode,
                          float* result_points,
                          float* lines_radius)
{

    float delta_x=end_point[0]-start_point[0];
    float delta_y=end_point[1]-start_point[1];
    float mag_start_end=sqrt(pow(delta_x,2)+pow(delta_y,2));

    float unit_start_end[2]={delta_x/mag_start_end,delta_y/mag_start_end};

    float current_point[2]={start_point[0],start_point[1]};
    float current_vector[2]={0,0};
    float current_mag=0;


    int current_point_idx=0;

    if(track_mode==REPEAT_ROUTE){
        while (true)
        {
            result_points[current_point_idx++]=current_point[0];
            result_points[current_point_idx++]=current_point[1];
            result_points[current_point_idx++]=current_point[0]+distance*cos(M_PI*direction_deg/180.f);
            result_points[current_point_idx++]=current_point[1]+distance*sin(M_PI*direction_deg/180.f);
            result_points[current_point_idx++]=current_point[0];
            result_points[current_point_idx++]=current_point[1];


            current_vector[0]=current_point[0]-start_point[0];
            current_vector[1]=current_point[1]-start_point[1];
            current_mag=sqrt(pow(current_vector[0],2)+pow(current_vector[1],2));



            if((mag_start_end-current_mag)>FLT_EPSILON){
                current_point[0]+=interval*unit_start_end[0];
                current_point[1]+=interval*unit_start_end[1];

                current_vector[0]=current_point[0]-start_point[0];
                current_vector[1]=current_point[1]-start_point[1];
                current_mag=sqrt(pow(current_vector[0],2)+pow(current_vector[1],2));

                if((current_mag-mag_start_end)>FLT_EPSILON){
                    current_point[0]=end_point[0];
                    current_point[1]=end_point[1];
                }
            }
            else{
                break;
            }

        }

    }


    if(track_mode==NOT_REPEAT_ROUTE){
        while (true)
        {
            result_points[current_point_idx++]=current_point[0];
            result_points[current_point_idx++]=current_point[1];

            result_points[current_point_idx++]=current_point[0]+distance*cos(M_PI*direction_deg/180.f);
            result_points[current_point_idx++]=current_point[1]+distance*sin(M_PI*direction_deg/180.f);


            current_vector[0]=current_point[0]-start_point[0];
            current_vector[1]=current_point[1]-start_point[1];
            current_mag=sqrt(pow(current_vector[0],2)+pow(current_vector[1],2));

            if((mag_start_end-current_mag)>FLT_EPSILON){
                current_point[0]+=interval*unit_start_end[0];
                current_point[1]+=interval*unit_start_end[1];

                current_vector[0]=current_point[0]-start_point[0];
                current_vector[1]=current_point[1]-start_point[1];
                current_mag=sqrt(pow(current_vector[0],2)+pow(current_vector[1],2));

                if((current_mag-mag_start_end)>FLT_EPSILON){
                    current_point[0]=end_point[0];
                    current_point[1]=end_point[1];
                }
            }
            else{
                break;
            }

            result_points[current_point_idx++]=current_point[0]+distance*cos(M_PI*direction_deg/180.f);
            result_points[current_point_idx++]=current_point[1]+distance*sin(M_PI*direction_deg/180.f);

            result_points[current_point_idx++]=current_point[0];
            result_points[current_point_idx++]=current_point[1];


            current_vector[0]=current_point[0]-start_point[0];
            current_vector[1]=current_point[1]-start_point[1];
            current_mag=sqrt(pow(current_vector[0],2)+pow(current_vector[1],2));

            if((mag_start_end-current_mag)>FLT_EPSILON){
                current_point[0]+=interval*unit_start_end[0];
                current_point[1]+=interval*unit_start_end[1];

                current_vector[0]=current_point[0]-start_point[0];
                current_vector[1]=current_point[1]-start_point[1];
                current_mag=sqrt(pow(current_vector[0],2)+pow(current_vector[1],2));

                if((current_mag-mag_start_end)>FLT_EPSILON){
                    current_point[0]=end_point[0];
                    current_point[1]=end_point[1];
                }
            }
            else{
                break;
            }


        }

    }

    for(int i=0;i<(current_point_idx/2)-1;++i){
        lines_radius[i]=0;
    }

    return current_point_idx/2;

}



int init_trans_coordinate(float * left1_point, float * right1_point){
    float zero_point[2]= {(left1_point[0]+right1_point[0])/2, (left1_point[1]+right1_point[1])/2};
    float axis_0_vector[2]= {right1_point[0]-zero_point[0], right1_point[1]-zero_point[1]};
    float mag_vector= sqrt(pow(axis_0_vector[0],2)+pow(axis_0_vector[1],2));
    float axis_0_unit[2]= {axis_0_vector[0]/mag_vector, axis_0_vector[1]/mag_vector};


    trans_matrix[0][0]= axis_0_unit[0];
    trans_matrix[0][1]= -axis_0_unit[1];
    trans_matrix[0][2]= zero_point[0];
    trans_matrix[1][0]= axis_0_unit[1];
    trans_matrix[1][1]= axis_0_unit[0];
    trans_matrix[1][2]= zero_point[1];
    trans_matrix[2][0]= 0;
    trans_matrix[2][1]= 0;
    trans_matrix[2][2]= 1;

    return 0;
}


int trans_coordinate(float * left1_point, float * right1_point,float* input_points, float* result_points, int num){

    init_trans_coordinate(left1_point, right1_point);

    for(int i=0;i<num;++i){

        result_points[2*i]= trans_matrix[0][0]*input_points[2*i] + trans_matrix[0][1]*input_points[2*i+1]+trans_matrix[0][2];
        result_points[2*i+1]= trans_matrix[1][0]*input_points[2*i] + trans_matrix[1][1]*input_points[2*i+1]+trans_matrix[1][2];

    }

    return 0;
}

int rotate(float* input_points,float*result_points, float* rotate_center_point,float theta_deg,int num){
    float tx=rotate_center_point[0];
    float ty=rotate_center_point[1];

    float rotate_matrix[3][3]={0};
    rotate_matrix[0][0]= cos(M_PI*theta_deg/180.f);
    rotate_matrix[0][1]= -sin(M_PI*theta_deg/180.f);
    rotate_matrix[0][2]= (1-cos(M_PI*theta_deg/180.f))*tx+ty*sin(M_PI*theta_deg/180.f);
    rotate_matrix[1][0]=sin(M_PI*theta_deg/180.f);
    rotate_matrix[1][1]=cos(M_PI*theta_deg/180.f);
    rotate_matrix[1][2]=(1-cos(M_PI*theta_deg/180.f))*ty-tx*sin(M_PI*theta_deg/180.f);


    for(int i=0;i<num;++i){

        result_points[2*i]=(
                rotate_matrix[0][0]*input_points[2*i] +
                rotate_matrix[0][1]*input_points[2*i+1] +
                rotate_matrix[0][2]);
        result_points[2*i+1]= (
                rotate_matrix[1][0]*input_points[2*i] +
                rotate_matrix[1][1]*input_points[2*i+1] +
                rotate_matrix[1][2]);

    }

    return 0;
}

int shift(float* input_points,float*result_points, float* shift_vector,int num){
    for(int i=0;i<num;++i){

        result_points[2*i]= shift_vector[0] + input_points[2*i];
        result_points[2*i+1]= shift_vector[1] + input_points[2*i+1];
    }

    return 0;

}