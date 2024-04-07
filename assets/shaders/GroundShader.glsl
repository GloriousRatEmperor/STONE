#type vertex
#version 460
layout (location=0) in vec2 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexcord;
void main()
{
    fColor = aColor;
    fTexcord=aTexCoords;
    gl_Position = uProjection * uView * vec4(aPos,0.0, 1.0);

}


#type fragment
#version 460

in vec4 fColor;
in vec2 fTexcord;

out vec4 color;

uniform sampler2D sampler;

void main()
{
    vec4 tek=texture(sampler,fTexcord);
    color =  vec4(fColor[0]*(1-tek[3]) + tek[0]*tek[3],
    fColor[1]*(1-tek[3])  + tek[1]*tek[3],fColor[2]*(1-tek[3])+ tek[2]*tek[3],1);

}