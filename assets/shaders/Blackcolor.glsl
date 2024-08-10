#type vertex
#version 460
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;
layout (location=3) in float aTexId;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;
out float fTexId;

void main()
{
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexId = aTexId;

    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 460

in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;

uniform sampler2D uTextures[8];

out vec4 color;
vec4 ones=vec4(1,1,1,1);
void main()
{
    if (fTexId > 0) {

        int id = int(fTexId);
        switch (id) {
            case 0:
            color = texture(uTextures[0], fTexCoords);
            break;
            case 1:
            color = texture(uTextures[1], fTexCoords);
            break;
            case 2:
            color = texture(uTextures[2], fTexCoords);
            break;
            case 3:
            color = texture(uTextures[3], fTexCoords);
            break;
            case 4:
            color = texture(uTextures[4], fTexCoords);
            break;
            case 5:
            color = texture(uTextures[5], fTexCoords);
            break;
            case 6:
            color = texture(uTextures[6], fTexCoords);
            break;
            case 7:
            color = texture(uTextures[7], fTexCoords);
            break;
        }
    } else {
        color = fColor;
    }
    vec4 colorg=fColor *color[3]* (1-color);
    color[0]=colorg[0];
    color[1]=colorg[1];
    color[2]=colorg[2];

}