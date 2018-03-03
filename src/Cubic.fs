#version 130

in vec2 texcoord;

uniform samplerCube tex;

void main(void) {
  vec4 color = vec4(0,0,0,0);
  if (texcoord.y >= -1.0/3 && texcoord.y < 1.0/3) {
    if (texcoord.x < -0.5) {
      color = texture(tex, vec3(1, -texcoord.y*3, -texcoord.x*4-3));
    } else if (texcoord.x < 0) {
      color = texture(tex, vec3(-texcoord.x*4-1, -texcoord.y*3, -1));
    } else if (texcoord.x < 0.5) {
      color = texture(tex, vec3(-1, -texcoord.y*3, texcoord.x*4-1));
    } else {
      color = texture(tex, vec3(texcoord.x*4-3, -texcoord.y*3, 1));
    }
  } else if (texcoord.x >= -0.5 && texcoord.x < 0) {
    if (texcoord.y < -1.0/3) {
      color = texture(tex, vec3(-texcoord.x*4-1, 1, -texcoord.y*3-2));
    } else if (texcoord.y >= 1.0/3) {
      color = texture(tex, vec3(-texcoord.x*4-1, -1, texcoord.y*3-2));
    }
  } else {
    color = vec4(0.3,0.3,0.3,0);
  }

  gl_FragColor = color;
}
