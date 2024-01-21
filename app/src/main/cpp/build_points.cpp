#include <cfloat>
#define _USE_MATH_DEFINES
#include <cmath>
#include <cstdbool>
#include "build_points.h"

static float trans_matrix[3][3]={0};

int build_center_radiate_points( float* center_point, 
                                float radius, 
                                float start_deg, 
                                float end_deg, 
                                float delta_deg, 
                                int track_mode, 
                                float* result_points)
{
    int current_idx=0;

    if(track_mode==REPEAT_ROUTE){
        while (true)
        {
            result_points[current_idx++]=center_point[0];
            result_points[current_idx++]=center_point[1];
            result_points[current_idx++]=center_point[0]+radius*cos(M_PI*start_deg/180.f);
            result_points[current_idx++]=center_point[1]+radius*sin(M_PI*start_deg/180.f);

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

            result_points[current_idx++]=center_point[0];
            result_points[current_idx++]=center_point[1];
            result_points[current_idx++]=center_point[0]+radius*cos(M_PI*start_deg/180.f);
            result_points[current_idx++]=center_point[1]+radius*sin(M_PI*start_deg/180.f);

            if((end_deg-start_deg)>FLT_EPSILON){
                start_deg+=delta_deg;
                if((start_deg-end_deg)>FLT_EPSILON){
                    start_deg=end_deg;
                }
            }
            else{
                break;
            }

            result_points[current_idx++]=center_point[0]+radius*cos(M_PI*start_deg/180.f);
            result_points[current_idx++]=center_point[1]+radius*sin(M_PI*start_deg/180.f);


            if((end_deg-start_deg)>FLT_EPSILON){
                start_deg+=delta_deg;
                if((start_deg-end_deg)>FLT_EPSILON){
                    start_deg=end_deg;
                }
            }
            else{
                result_points[current_idx++]=center_point[0];
                result_points[current_idx++]=center_point[1];
                break;
            }

        }
        
    }


    return current_idx/2;
}


int build_parallel_points(float* start_point, 
                        float* end_point, 
                        float distance, 
                        float interval, 
                        float direction_deg, 
                        int track_mode, 
                        float* result_points)
{

    float delta_x=end_point[0]-start_point[0];
    float delta_y=end_point[1]-start_point[1];
    float mag_start_end=sqrt(pow(delta_x,2)+pow(delta_y,2));

    float unit_start_end[2]={delta_x/mag_start_end,delta_y/mag_start_end};
    
    float current_point[2]={start_point[0],start_point[1]};
    float current_vector[2]={0,0};
    float current_mag=0;


    int current_idx=0;

    if(track_mode==REPEAT_ROUTE){
        while (true)
        {
            result_points[current_idx++]=current_point[0];
            result_points[current_idx++]=current_point[1];
            result_points[current_idx++]=current_point[0]+distance*cos(M_PI*direction_deg/180.f);
            result_points[current_idx++]=current_point[1]+distance*sin(M_PI*direction_deg/180.f);
            result_points[current_idx++]=current_point[0];
            result_points[current_idx++]=current_point[1];


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
            result_points[current_idx++]=current_point[0];
            result_points[current_idx++]=current_point[1];

            result_points[current_idx++]=current_point[0]+distance*cos(M_PI*direction_deg/180.f);
            result_points[current_idx++]=current_point[1]+distance*sin(M_PI*direction_deg/180.f);


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

            result_points[current_idx++]=current_point[0]+distance*cos(M_PI*direction_deg/180.f);
            result_points[current_idx++]=current_point[1]+distance*sin(M_PI*direction_deg/180.f);

            result_points[current_idx++]=current_point[0];
            result_points[current_idx++]=current_point[1];


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
    
    return current_idx/2;
    
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