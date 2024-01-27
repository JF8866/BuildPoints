#version 300 es
uniform mat4 uMVPMatrix;
uniform mat4 translateMatrix;
uniform bool isTranslate;
layout(location = 0) in vec4 vPosition;
layout(location = 1) in vec2 aTextureCoord;
out vec2 vTextureCoord;

void main() {
    if(isTranslate){
        gl_Position = uMVPMatrix * translateMatrix * vPosition;
    } else {
        gl_Position = uMVPMatrix * vPosition;
    }
    vTextureCoord = aTextureCoord;
}