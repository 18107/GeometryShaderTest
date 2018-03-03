import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class Shaders {
	
	public static final String vertex1 = "#version 130\n" + 
			"\n" + 
			"out vec4 defaultColor;\n" + 
			"\n" + 
			"void main(void) {\n" + 
			"  gl_Position = gl_Vertex;\n" + 
			"  defaultColor = gl_Vertex/2+0.5;\n" + 
			"}";
	
	public static final String geometry1 = "#version 410\n" + 
			"\n" + 
			"layout(triangles) in;\n" + 
			"layout (triangle_strip, max_vertices=18) out;\n" + 
			"\n" + 
			"uniform mat4 projection[6];\n" + 
			"uniform mat4 modelview;\n" + 
			"\n" + 
			"in vec4 defaultColor[];\n" + 
			"out vec4 color;\n" + 
			"\n" + 
			" void main()\n" + 
			"{\n" + 
			"  for (int a = 0; a < 6; a++) {\n" + 
			"    for (int i = 0; i < gl_in.length(); i++) {\n" + 
			"      gl_Layer = a;\n" + 
			"      color = defaultColor[i];\n" + 
			"      vec4 vertex = gl_in[i].gl_Position;\n" + 
			"      gl_Position = projection[a]*modelview*vertex;\n" + 
			"      EmitVertex();\n" + 
			"    }\n" + 
			"    EndPrimitive();\n" + 
			"  }\n" + 
			"}";
	
	public static final String fragment1 = "#version 130\n" + 
			"\n" + 
			"in vec4 color;\n" + 
			"\n" + 
			"void main(){\n" + 
			"    gl_FragData[0] = color;\n" + 
			"}";
	
	public static final String quad = "#version 130\n" + 
			"\n" + 
			"out vec2 texcoord;\n" + 
			"\n" + 
			"void main(void) {\n" + 
			"  gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;\n" + 
			"\n" + 
			"  texcoord = gl_Position.xy;\n" + 
			"}";
	
	public static final String cubic = "#version 130\n" + 
			"\n" + 
			"in vec2 texcoord;\n" + 
			"\n" + 
			"uniform samplerCube tex;\n" + 
			"\n" + 
			"void main(void) {\n" + 
			"  vec4 color = vec4(0,0,0,0);\n" + 
			"  if (texcoord.y >= -1.0/3 && texcoord.y < 1.0/3) {\n" + 
			"    if (texcoord.x < -0.5) {\n" + 
			"      color = texture(tex, vec3(1, -texcoord.y*3, -texcoord.x*4-3));\n" + 
			"    } else if (texcoord.x < 0) {\n" + 
			"      color = texture(tex, vec3(-texcoord.x*4-1, -texcoord.y*3, -1));\n" + 
			"    } else if (texcoord.x < 0.5) {\n" + 
			"      color = texture(tex, vec3(-1, -texcoord.y*3, texcoord.x*4-1));\n" + 
			"    } else {\n" + 
			"      color = texture(tex, vec3(texcoord.x*4-3, -texcoord.y*3, 1));\n" + 
			"    }\n" + 
			"  } else if (texcoord.x >= -0.5 && texcoord.x < 0) {\n" + 
			"    if (texcoord.y < -1.0/3) {\n" + 
			"      color = texture(tex, vec3(-texcoord.x*4-1, 1, -texcoord.y*3-2));\n" + 
			"    } else if (texcoord.y >= 1.0/3) {\n" + 
			"      color = texture(tex, vec3(-texcoord.x*4-1, -1, texcoord.y*3-2));\n" + 
			"    }\n" + 
			"  } else {\n" + 
			"    color = vec4(0.3,0.3,0.3,0);\n" + 
			"  }\n" + 
			"\n" + 
			"  gl_FragColor = color;\n" + 
			"}";
	
	public static final String equirectangular = "#version 130\n" + 
			"\n" + 
			"#define PI 3.14159265\n" + 
			"\n" + 
			"in vec2 texcoord;\n" + 
			"\n" + 
			"uniform samplerCube tex;\n" + 
			"\n" + 
			"vec3 rotate(vec3 ray, vec2 angle) {\n" + 
			"\n" + 
			"  //rotate y\\n\n" + 
			"  float y = -sin(angle.y)*ray.z;\n" + 
			"  float z = cos(angle.y)*ray.z;\n" + 
			"  ray.y = y;\n" + 
			"  ray.z = z;\n" + 
			"\n" + 
			"  //rotate x\\n\n" + 
			"  float x = -sin(angle.x)*ray.z;\n" + 
			"  z = cos(angle.x)*ray.z;\n" + 
			"  ray.x = x;\n" + 
			"  ray.z = z;\n" + 
			"\n" + 
			"  return ray;\n" + 
			"}\n" + 
			"\n" + 
			"void main(void) {\n" + 
			"  gl_FragColor = texture(tex, rotate(vec3(0,0,-1), vec2(-texcoord.x*PI, -texcoord.y*PI/2)));\n" + 
			"}";
	
	private int program;
	private int vertexShader;
	private int geometryShader;
	private int fragmentShader;
	
	public Shaders(String vertex, String fragment) {
		vertexShader = createShader(vertex, GL20.GL_VERTEX_SHADER);
		geometryShader = 0;
		fragmentShader = createShader(fragment, GL20.GL_FRAGMENT_SHADER);
		
		if (vertexShader == 0 || fragmentShader == 0) {
			throw new RuntimeException("Shader not created.");
		}
		
		program = GL20.glCreateProgram();
		
		if (program == 0) {
			throw new RuntimeException("Program not created.");
		}
		
		GL20.glAttachShader(program, vertexShader);
		GL20.glAttachShader(program, fragmentShader);
		GL20.glLinkProgram(program);
	}
	
	public Shaders(String vertex, String geometry, String fragment) {
		vertexShader = createShader(vertex, GL20.GL_VERTEX_SHADER);
		geometryShader = createShader(geometry, GL32.GL_GEOMETRY_SHADER);
		fragmentShader = createShader(fragment, GL20.GL_FRAGMENT_SHADER);
		
		if (vertexShader == 0 || fragmentShader == 0 || geometryShader == 0) {
			throw new RuntimeException("Shader not created.");
		}
		
		program = GL20.glCreateProgram();
		
		if (program == 0) {
			throw new RuntimeException("Program not created.");
		}
		
		GL20.glAttachShader(program, vertexShader);
		GL20.glAttachShader(program, geometryShader);
		GL20.glAttachShader(program, fragmentShader);
		GL20.glLinkProgram(program);
	}

	private int createShader(String shaderIn, int shaderType) {
		int shader = 0;
		shader = GL20.glCreateShader(shaderType);
		if (shader == 0)
			return 0;
		GL20.glShaderSource(shader, shaderIn);
		GL20.glCompileShader(shader);
		
		return shader;
	}
	
	public void end() {
		GL20.glDetachShader(program, vertexShader);
		GL20.glDetachShader(program, geometryShader);
		GL20.glDetachShader(program, fragmentShader);
		GL20.glDeleteProgram(program);
		GL20.glDeleteShader(vertexShader);
		GL20.glDeleteShader(geometryShader);
		GL20.glDeleteShader(fragmentShader);
	}
	
	public int getProgram() {
		return program;
	}
}
