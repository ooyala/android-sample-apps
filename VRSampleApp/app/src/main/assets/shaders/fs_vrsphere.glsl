#extension GL_OES_EGL_image_external : require
precision highp float;

uniform samplerExternalOES s_Texture;
uniform mat4 mvpInv;

varying vec2 v_TextureCoord;

#define PIP2    1.5707963 // PI/2
#define PI      3.1415629
#define TWOPI   6.2831853 // 2PI

vec3 raySphereIntersect(vec3 r0, vec3 rd, float sr) {
    // - r0: ray origin
    // - rd: normalized ray direction
    // - sr: sphere radius
    float a = dot(rd, rd);
    vec3 s0_r0 = r0;
    float b = 2.0 * dot(rd, s0_r0);
    float c = dot(s0_r0, s0_r0) - (sr * sr);

    float d = b * b - 4.0 * a * c;

    return r0 + rd * (-b + sqrt(d)) / ( 2.0 * a);
}

vec4 trace(in vec2 p, in vec4 uv_rect)
{
    vec4 r0 = vec4(p, -1.0, 1.0);
    vec4 r1 = vec4(p,  1.0, 1.0);

    r0 = mvpInv * r0;
    r0 /= r0.w;
    r1 = mvpInv * r1;
    r1 /= r1.w;

    vec3 rd = normalize(r1.xyz - r0.xyz);

    // calculate hit point of ray on sphere
    vec3 sp = raySphereIntersect(r0.xyz, rd, 1.0);

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
    p.y *= -1.0;
    p *= 2.5;

    // Setup uv rect, i.e. a rect (x, y, width, height) that defines a (sub)section of the texture
    vec4 uv_rect = vec4(0.0, 0.0, 1.0, 1.0); // mono: complete texture

    // Scale to aspect ratio
    gl_FragColor = trace(p, uv_rect);
}
