#version 410 compatibility

layout(triangles) in;
layout (triangle_strip, max_vertices=18) out;

uniform mat4 projection;
uniform mat4 modelview;

in vec4 defaultColor[];
out vec4 color;

 void main()
{
  mat4 rotation[6]; //+X-X+Y-Y+Z-Z
  rotation[0] = mat4(0,0,1,0, 0,1,0,0, -1,0,0,0, 0,0,0,1);//right
  rotation[1] = mat4(0,0,-1,0, 0,1,0,0, 1,0,0,0, 0,0,0,1);//left
  rotation[2] = mat4(1,0,0,0, 0,0,1,0, 0,-1,0,0, 0,0,0,1);//up
  rotation[3] = mat4(1,0,0,0, 0,0,-1,0, 0,1,0,0, 0,0,0,1);//down
  rotation[4] = mat4(-1,0,0,0, 0,1,0,0, 0,0,-1,0, 0,0,0,1);//back
  rotation[5] = mat4(1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1);//front
  vec4 colors[6];
  colors[0] = vec4(0,0,1,0);
  colors[1] = vec4(0,1,1,0);
  colors[2] = vec4(1,1,1,0);
  colors[3] = vec4(1,1,0,0);
  colors[4] = vec4(0,1,0,0);
  colors[5] = vec4(1,0,0,0);

  for (int a = 0; a < 6; a++) {
    for (int i = 0; i < gl_in.length(); i++) {
      color = colors[a];
      vec4 vertex = gl_in[i].gl_Position;
      gl_Position = projection*rotation[a]*modelview*vertex;
      EmitVertex();
    }
    EndPrimitive();
  }
}
