#version 150

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

layout(std140) uniform RoundedTextureUniform {
    vec4 radius;
    vec2 size;
    vec2 center;
    float scaleFactor;
};

uniform sampler2D Sampler0;
in vec2 texCoord0;
out vec4 fragColor;

// From: https://iquilezles.org/articles/distfunctions2d/
float sdRoundedBox(vec2 p, vec2 b, vec4 r){
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x  : r.y;
    vec2 q = abs(p)-b+r.x;
    return min(max(q.x,q.y),0.0) + length(max(q,0.0)) - r.x;
}

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a == 0.0) {
        discard;
    }

    vec2 halfSize = size / 2.0;
    float distance = sdRoundedBox(gl_FragCoord.xy - center, halfSize, radius * scaleFactor);

    if (distance > 0.0) {
        discard;
    }

    fragColor = color * ColorModulator;
}