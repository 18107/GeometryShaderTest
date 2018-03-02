#version 410

layout(triangles) in;
layout (triangle_strip, max_vertices=18) out;

uniform mat4 projection[6];
uniform mat4 modelview;

in vec4 defaultColor[];
out vec4 color;

 void main()
{
  vec4 colors[6];
  colors[0] = vec4(0,0,1,1);
  colors[1] = vec4(0,1,1,1);
  colors[2] = vec4(1,1,1,1);
  colors[3] = vec4(1,1,0,1);
  colors[4] = vec4(1,0,1,1);
  colors[5] = vec4(1,0,0,1);

  for (int a = 0; a < 6; a++) {
    for (int i = 0; i < gl_in.length(); i++) {
      color = colors[a];
      vec4 vertex = gl_in[i].gl_Position;
      gl_Position = projection[a]*modelview*vertex;
      EmitVertex();
    }
    EndPrimitive();
  }
}
