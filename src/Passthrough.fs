#version 130

uniform sampler2D tex;

void main(void) {
  gl_FragColor = texture(tex, gl_Texcoord[0].xy);
}
