#version 300 es
precision mediump float;
uniform bool isTexture;
uniform vec4 vColor;
uniform sampler2D uTextureUnit;
in vec2 vTextureCoord;
out vec4 vFragColor;

void main(void) {
    if (isTexture) {
        vFragColor = texture(uTextureUnit, vTextureCoord);
    } else {
        vFragColor = vColor;
    }
}