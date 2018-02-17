#version 130

out vec4 defaultColor;

void main(void) {
  gl_Position = gl_Vertex;
  defaultColor = vec4(0.7, 0.2, 0.2, 0);
}
