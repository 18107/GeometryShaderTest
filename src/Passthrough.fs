#version 130

in vec2 texcoord;

uniform sampler2D tex;

void main(void) {
  gl_FragColor = texture(tex, texcoord);
}
