#version 130

#define PI 3.14159265

in vec2 texcoord;

uniform samplerCube tex;

vec3 rotate(vec3 ray, vec2 angle) {

  //rotate y\n
  float y = -sin(angle.y)*ray.z;
  float z = cos(angle.y)*ray.z;
  ray.y = y;
  ray.z = z;

  //rotate x\n
  float x = -sin(angle.x)*ray.z;
  z = cos(angle.x)*ray.z;
  ray.x = x;
  ray.z = z;

  return ray;
}

void main(void) {
  gl_FragColor = texture(tex, rotate(vec3(0,0,-1), vec2(texcoord.x*PI, texcoord.y*PI/2)));
}
