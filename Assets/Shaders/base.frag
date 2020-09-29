#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 fragColor;

layout(location = 0) out vec4 outColor;

void main() {
    outColor = vec4((fragColor[0]-fragColor[0]%0.065), (fragColor[1]-fragColor[1]%0.065), (fragColor[2]-fragColor[2]%0.065), 1.0);
}