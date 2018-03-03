#version 130

out vec2 texcoord;

void main(void) {
  gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;

  texcoord = gl_Position.xy;
}
