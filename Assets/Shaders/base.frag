#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 fragColor;

layout(location = 0) out vec4 outColor;

void main() {
    outColor = vec4(fragColor[0] - mod(fragColor[0],0.0625f), fragColor[1] - mod(fragColor[1],0.0625f), fragColor[2] - mod(fragColor[2],0.0625f), 1);
}