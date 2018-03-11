import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.glu.GLU;

public class Renderer {
	
	private static FloatBuffer matrix;
	private static FloatBuffer projectionMatrix;
	private static int vaoId;
	private static int vboId;
	private static int fboId;
	private static int texId;
	private static int depId;
	
	private static float resolutionModifier = 4f; //1 will do, 4 is highest quality
	private static int antialiasing = 16;
	
	private static float[] vertices = {
			//front
			-1,-1,-1,
			-1,1,-1,
			1,1,-1,
			1,1,-1,
			1,-1,-1,
			-1,-1,-1,
			
			//back
			-1,-1,1,
			1,-1,1,
			1,1,1,
			1,1,1,
			-1,1,1,
			-1,-1,1,
			
			//left
			-1,-1,-1,
			-1,-1,1,
			-1,1,1,
			-1,1,1,
			-1,1,-1,
			-1,-1,-1,
			
			//right
			1,-1,1,
			1,-1,-1,
			1,1,-1,
			1,1,-1,
			1,1,1,
			1,-1,1,
			
			//top
			-1,1,-1,
			-1,1,1,
			1,1,1,
			1,1,1,
			1,1,-1,
			-1,1,-1,
			
			//bottom
			-1,-1,-1,
			1,-1,-1,
			1,-1,1,
			1,-1,1,
			-1,-1,1,
			-1,-1,-1
	};
	
	public static void setResolutionModifier(float modifier) {
		if (modifier != resolutionModifier) {
			resolutionModifier = modifier;
			resize();
		}
	}

	public static void init() {
		fboId = GL30.glGenFramebuffers();
		texId = GL11.glGenTextures();
		depId = GL11.glGenTextures();
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		for (int face = 0; face < 6; face++) {
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0,
					GL30.GL_RGBA32F, (int) (Display.getHeight()*resolutionModifier),
					(int) (Display.getHeight()*resolutionModifier), 0,
					GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer)null);
		}
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texId, 0);
		
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, depId);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL14.GL_TEXTURE_COMPARE_MODE, GL30.GL_COMPARE_REF_TO_TEXTURE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_GREATER);
		for (int face = 0; face < 6; face++) {
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0,
					GL14.GL_DEPTH_COMPONENT24, (int) (Display.getHeight()*resolutionModifier),
					(int) (Display.getHeight()*resolutionModifier), 0,
					GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
		}
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depId, 0);
		
		IntBuffer buffer = BufferUtils.createIntBuffer(2);
		buffer.put(GL30.GL_COLOR_ATTACHMENT0);
		buffer.put(GL30.GL_COLOR_ATTACHMENT1);
		buffer.flip();
		GL20.glDrawBuffers(buffer);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
		vertexBuffer.put(vertices);
		vertexBuffer.flip();
		
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		
		projectionMatrix = BufferUtils.createFloatBuffer(16*6);
		matrix = BufferUtils.createFloatBuffer(16);
		FloatBuffer rotation[] = new FloatBuffer[6];
		rotation[0] = BufferUtils.createFloatBuffer(16);
		rotation[0].put(new float[] {0,0,1,0, 0,1,0,0, -1,0,0,0, 0,0,0,1}); //right
		rotation[0].flip();
		rotation[1] = BufferUtils.createFloatBuffer(16);
		rotation[1].put(new float[] {0,0,-1,0, 0,1,0,0, 1,0,0,0, 0,0,0,1}); //left
		rotation[1].flip();
		rotation[2] = BufferUtils.createFloatBuffer(16);
		rotation[2].put(new float[] {-1,0,0,0, 0,0,1,0, 0,1,0,0, 0,0,0,1}); //up
		rotation[2].flip();
		rotation[3] = BufferUtils.createFloatBuffer(16);
		rotation[3].put(new float[] {-1,0,0,0, 0,0,-1,0, 0,-1,0,0, 0,0,0,1}); //down
		rotation[3].flip();
		rotation[4] = BufferUtils.createFloatBuffer(16);
		rotation[4].put(new float[] {-1,0,0,0, 0,1,0,0, 0,0,-1,0, 0,0,0,1}); //back
		rotation[4].flip();
		rotation[5] = BufferUtils.createFloatBuffer(16);
		rotation[5].put(new float[] {1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1}); //front
		rotation[5].flip();
		
		GL11.glViewport(0, 0, (int) (Display.getHeight()*resolutionModifier),
				(int) (Display.getHeight()*resolutionModifier));
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(90, 1, 0.001f, 1000f);
		for (int i = 0; i < 6; i++) {
			GL11.glPushMatrix();
			GL11.glMultMatrix(rotation[i]);
			GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, matrix);
			projectionMatrix.put(matrix);
			matrix.flip();
			GL11.glPopMatrix();
		}
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		projectionMatrix.flip();
		
		//GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public static void resize() {
		GL11.glDeleteTextures(depId);
		GL11.glDeleteTextures(texId);
		
		texId = GL11.glGenTextures();
		depId = GL11.glGenTextures();
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		for (int face = 0; face < 6; face++) {
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0,
					GL30.GL_RGBA32F, (int) (Display.getHeight()*resolutionModifier),
					(int) (Display.getHeight()*resolutionModifier), 0,
					GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer)null);
		}
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texId, 0);
		
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, depId);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL14.GL_TEXTURE_COMPARE_MODE, GL30.GL_COMPARE_REF_TO_TEXTURE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_GREATER);
		for (int face = 0; face < 6; face++) {
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0,
					GL14.GL_DEPTH_COMPONENT24, (int) (Display.getHeight()*resolutionModifier),
					(int) (Display.getHeight()*resolutionModifier), 0,
					GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
		}
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depId, 0);
	}
	
	public static void render(int program1, int program2) {
		Camera.update();
		
		renderPass1(program1);
		renderPass2(program2);
	}
	
	private static void renderPass1(int program) {
		GL20.glUseProgram(program);
		
		GL11.glViewport(0, 0, (int) (Display.getHeight()*resolutionModifier),
				(int) (Display.getHeight()*resolutionModifier));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
		
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
		
		GL11.glRotatef(Camera.rx, 1, 0, 0);
		GL11.glRotatef(Camera.ry, 0, 1, 0);
		GL11.glTranslatef(-Camera.x, -Camera.y, -Camera.z);
		
		int projection = GL20.glGetUniformLocation(program, "projection");
		GL20.glUniformMatrix4(projection, false, projectionMatrix);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrix);
		int modelview = GL20.glGetUniformLocation(program, "modelview");
		GL20.glUniformMatrix4(modelview, false, matrix);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertices.length/3);
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		GL20.glUseProgram(0);
	}
	
	private static void renderPass2(int program) {
		GL20.glUseProgram(program);
		
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		int tex = GL20.glGetUniformLocation(program, "tex");
		GL20.glUniform1i(tex, 0);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 1, 0, 1, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId);
		
		antialiasing = 16;
		
		int antialiasingUniform = GL20.glGetUniformLocation(program, "antialiasing");
		GL20.glUniform1i(antialiasingUniform, antialiasing);
		int pixelOffsetUniform;
		if (antialiasing == 1) {
			pixelOffsetUniform = GL20.glGetUniformLocation(program, "pixelOffset[0]");
			GL20.glUniform2f(pixelOffsetUniform, 0, 0);
		}
		else if (antialiasing == 4) {
			pixelOffsetUniform = GL20.glGetUniformLocation(program, "pixelOffset[0]");
			GL20.glUniform2f(pixelOffsetUniform, -0.5f/Display.getWidth(), -0.5f/Display.getHeight());
			pixelOffsetUniform = GL20.glGetUniformLocation(program, "pixelOffset[1]");
			GL20.glUniform2f(pixelOffsetUniform, 0.5f/Display.getWidth(), -0.5f/Display.getHeight());
			pixelOffsetUniform = GL20.glGetUniformLocation(program, "pixelOffset[2]");
			GL20.glUniform2f(pixelOffsetUniform, -0.5f/Display.getWidth(), 0.5f/Display.getHeight());
			pixelOffsetUniform = GL20.glGetUniformLocation(program, "pixelOffset[3]");
			GL20.glUniform2f(pixelOffsetUniform, 0.5f/Display.getWidth(), 0.5f/Display.getHeight());
		}
		else if (antialiasing == 16) {
			float left = (-1f+0.25f)/Display.getWidth();
			float top = (-1f+0.25f)/Display.getHeight();
			float right = 0.5f/Display.getWidth();
			float down = 0.5f/Display.getHeight();
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 4; x++) {
					pixelOffsetUniform = GL20.glGetUniformLocation(program, "pixelOffset[" + (y*4+x) + "]");
					GL20.glUniform2f(pixelOffsetUniform, left + right*x, top + down*y);
				}
			}
		}
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(1, 0);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(1, 1);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(0, 1);
		}
		GL11.glEnd();
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL20.glUseProgram(0);
	}
	
	public static void end() {
		GL15.glDeleteBuffers(vboId);
		GL30.glDeleteVertexArrays(vaoId);
		
		GL11.glDeleteTextures(depId);
		GL11.glDeleteTextures(texId);
		GL30.glDeleteFramebuffers(fboId);
	}
}
