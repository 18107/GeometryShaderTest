import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class Shaders {
	
	public static final String vertex1 = "#version 130\n" + 
			"\n" + 
			"out vec4 defaultColor;\n" + 
			"\n" + 
			"void main(void) {\n" + 
			"  gl_Position = gl_Vertex;\n" + 
			"  defaultColor = vec4(0.7, 0.2, 0.2, 0);\n" + 
			"}";
	
	public static final String geometry1 = "#version 410 compatibility\n" + 
			"\n" + 
			"layout(triangles) in;\n" + 
			"layout (triangle_strip, max_vertices=18) out;\n" + 
			"\n" + 
			"uniform mat4 projection;\n" + 
			"uniform mat4 model;\n" + 
			"uniform mat4 view;\n" + 
			"\n" + 
			"in vec4 defaultColor[];\n" + 
			"out vec4 color;\n" + 
			"\n" + 
			" void main()\n" + 
			"{\n" + 
			"  mat4 rotation[6]; //+X-X+Y-Y+Z-Z\n" + 
			"  rotation[0] = mat4(0,0,1,0, 0,1,0,0, -1,0,0,0, 0,0,0,1);//right\n" + 
			"  rotation[1] = mat4(0,0,-1,0, 0,1,0,0, 1,0,0,0, 0,0,0,1);//left\n" + 
			"  rotation[2] = mat4(1,0,0,0, 0,0,1,0, 0,-1,0,0, 0,0,0,1);//up\n" + 
			"  rotation[3] = mat4(1,0,0,0, 0,0,-1,0, 0,1,0,0, 0,0,0,1);//down\n" + 
			"  rotation[4] = mat4(-1,0,0,0, 0,1,0,0, 0,0,-1,0, 0,0,0,1);//back\n" + 
			"  rotation[5] = mat4(1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1);//front\n" + 
			"  vec4 colors[6];\n" + 
			"  colors[0] = vec4(0,0,1,0);\n" + 
			"  colors[1] = vec4(0,1,1,0);\n" + 
			"  colors[2] = vec4(1,1,1,0);\n" + 
			"  colors[3] = vec4(1,1,0,0);\n" + 
			"  colors[4] = vec4(0,1,0,0);\n" + 
			"  colors[5] = vec4(1,0,0,0);\n" + 
			"\n" + 
			"  for (int a = 0; a < 6; a++) {\n" + 
			"    for (int i = 0; i < gl_in.length(); i++) {\n" + 
			"      color = colors[a];\n" + 
			"      vec4 vertex = gl_in[i].gl_Position;\n" + 
			"      gl_Position = projection*rotation[a]*view*model*vertex;\n" + 
			"      EmitVertex();\n" + 
			"    }\n" + 
			"    EndPrimitive();\n" + 
			"  }\n" + 
			"}\n" + 
			"";
	
	public static final String fragment1 = "#version 130\n" + 
			"\n" + 
			"in vec4 color;\n" + 
			"\n" + 
			"void main(){\n" + 
			"    gl_FragColor = color;\n" + 
			"}";
	
	public static final String vertex2 = "#version 130\n" + 
			"\n" + 
			"out vec2 texcoord;\n" + 
			"\n" + 
			"void main(void) {\n" + 
			"  gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;\n" + 
			"\n" + 
			"  texcoord = gl_Position.xy/2+0.5;\n" + 
			"}";
	
	public static final String fragment2 = "#version 130\n" + 
			"\n" + 
			"in vec2 texcoord;\n" + 
			"\n" + 
			"uniform sampler2D tex;\n" + 
			"\n" + 
			"void main(void) {\n" + 
			"  gl_FragColor = texture(tex, texcoord);\n" + 
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
