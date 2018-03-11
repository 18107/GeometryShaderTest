#version 130

out vec4 defaultColor;

void main(void) {
  gl_Position = gl_Vertex;
  defaultColor = gl_Vertex/2+0.5;
}
