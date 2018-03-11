#version 130

#define PI 3.14159265

in vec2 texcoord;

uniform samplerCube tex;

uniform int antialiasing;

uniform vec2 pixelOffset[16];

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
  vec4 color[16];

  for (int loop = 0; loop < antialiasing; loop++) {
    vec3 ray = vec3(0, 0, -1);

    ray = rotate(ray, vec2((-texcoord.x+pixelOffset[loop].x)*PI, (-texcoord.y+pixelOffset[loop].y)*PI/2)); //TODO

    color[loop] = texture(tex, ray);
  }

  if (antialiasing == 16) {
	  vec4 corner[4];
	  corner[0] = mix(mix(color[0], color[1], 2.0/3.0), mix(color[4], color[5], 3.0/5.0), 5.0/8.0);
	  corner[1] = mix(mix(color[3], color[2], 2.0/3.0), mix(color[7], color[6], 3.0/5.0), 5.0/8.0);
	  corner[2] = mix(mix(color[12], color[13], 2.0/3.0), mix(color[8], color[9], 3.0/5.0), 5.0/8.0);
	  corner[3] = mix(mix(color[15], color[14], 2.0/3.0), mix(color[11], color[10], 3.0/5.0), 5.0/8.0);
	  gl_FragColor = mix(mix(corner[0], corner[1], 0.5), mix(corner[2], corner[3], 0.5), 0.5);
	}
	else if (antialiasing == 4) {
		gl_FragColor = mix(mix(color[0], color[1], 0.5), mix(color[2], color[3], 0.5), 0.5);
	}
	else { //if antialiasing == 1
		gl_FragColor = color[0];
	}

  //gl_FragColor = texture(tex, rotate(vec3(0,0,-1), vec2(-texcoord.x*PI, -texcoord.y*PI/2)));
}
