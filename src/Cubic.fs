#version 130

in vec2 texcoord;

uniform sampler2D tex;

void main(void) {
  vec4 color = vec4(0,0,0,0);
  if (texcoord.y >= 1.0/3 && texcoord.y < 2.0/3) {
    if (texcoord.x < 0.25) {
      color = texture(tex, vec2(texcoord.x*4, texcoord.y*3-1));
      if (color.a < 0.05) {
        gl_FragColor = color;
      }
    } else if (texcoord.x < 0.5) {
      color = texture(tex, vec2(texcoord.x*4-1, texcoord.y*3-1));
      if (color.a > 0.45) {
        gl_FragColor = color;
      }
    } else if (texcoord.x < 0.75) {
      color = texture(tex, vec2(texcoord.x*4-2, texcoord.y*3-1));
      if (color.a > 0.05 && color.a < 0.15) {
        gl_FragColor = color;
      }
    } else {
      color = texture(tex, vec2(texcoord.x*4-3, texcoord.y*3-1));
      if (color.a > 0.35 && color.a < 0.45) {
        gl_FragColor = color;
      }
    }
  } else if (texcoord.x >= 0.25 && texcoord.x < 0.5) {
    if (texcoord.y < 1.0/3) {
      color = texture(tex, vec2(texcoord.x*4-1, texcoord.y*3));
      if (color.a > 0.15 && color.a < 0.25) {
        gl_FragColor = color;
      }
    } else if (texcoord.y >= 2.0/3) {
      color = texture(tex, vec2(texcoord.x*4-1, texcoord.y*3-2));
      if (color.a > 0.25 && color.a < 0.35) {
        gl_FragColor = color;
      }
    }
  } else {
    gl_FragColor = vec4(0.3,0.3,0.3,0);
  }
}
