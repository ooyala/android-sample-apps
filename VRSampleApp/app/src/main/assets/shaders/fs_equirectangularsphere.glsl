// Immersive 360 degree image shader
// Maps a equirectangular/spherical texture to a sphere
// Supports 3D VR modes: side by side, top and bottom

precision highp float;

uniform sampler2D s_Texture;
uniform vec2 u_TextureSize;
uniform mat4 rotation;
uniform int mode;
varying vec2 v_TextureCoord;

#define PIP2    1.5707963 // PI/2
#define PI      3.1415629
#define TWOPI   6.2831853 // 2PI

#define MODE_MONO 0
#define MODE_STEREO 1 // side by side

vec4 trace(in vec2 p, in vec4 uv_rect)
{
    vec3 D = normalize(vec3(p * 0.5, 1.0)); // ray direction D

    // calculate hit point of ray on sphere
    vec3 sp = (rotation * vec4(-D, 1.0)).xyz;

    // calculate texture mapping for hit point
    float phi = atan(sp.z, sp.x);
    float theta = acos(sp.y);

    // Spherical mapping from sphere to texture
    float u = 0.5 - (phi + PI) / TWOPI + 0.25;
    float v = (theta + PIP2) / PI - 0.5;

    // transform and clamp to requested uv (sub)section
    // (mod simulates texture wrap mode GL_REPEAT)
    u = uv_rect.x + mod(u * uv_rect.z, uv_rect.z);
    v = uv_rect.y + mod(v * uv_rect.w, uv_rect.w);

    return texture2D(s_Texture, vec2(u, v));
}

void main (void)
{
    // Scale texture space to (-1,1) in both axes
    vec2 p = -1.0 + 2.0 * v_TextureCoord;

    // Setup uv rect, i.e. a rect (x, y, width, height) that defines a (sub)section of the texture
    vec4 uv_rect;

    if(mode == MODE_MONO) {
        uv_rect = vec4(0.0, 0.0, 1.0, 1.0); // mono: complete texture
        p.x *= u_TextureSize.x / u_TextureSize.y;
    }
    else if(mode == MODE_STEREO) {
        uv_rect = vec4(0.0, 0.0, 1.0, 1.0);
        p.x = mod(p.x + 1.0, 1.0) * 2.0 - 1.0;
    }

    // Scale to aspect ratio
    gl_FragColor = trace(p, uv_rect);
}
