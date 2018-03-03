#version 410

layout(triangles) in;
layout (triangle_strip, max_vertices=18) out;

uniform mat4 projection[6];
uniform mat4 modelview;

in vec4 defaultColor[];
out vec4 color;

 void main()
{
  for (int a = 0; a < 6; a++) {
    for (int i = 0; i < gl_in.length(); i++) {
      gl_Layer = a;
      color = defaultColor[i];
      vec4 vertex = gl_in[i].gl_Position;
      gl_Position = projection[a]*modelview*vertex;
      EmitVertex();
    }
    EndPrimitive();
  }
}
