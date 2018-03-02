import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;

public class Renderer {
	
	private static FloatBuffer matrix;
	private static FloatBuffer projectionMatrix;
	private static int vaoId;
	private static int vboId;
	private static int fboId;
	private static int texId;
	private static int depId;
	
	private static float[] vertices = {
			-1,-1,-10,
			1,-1,-10,
			1,1,-10,
			1,1,-10,
			-1,1,-10,
			-1,-1,-10
	};

	public static void init() {
		fboId = GL30.glGenFramebuffers();
		texId = GL11.glGenTextures();
		depId = GL30.glGenRenderbuffers();
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, Game.width, Game.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texId, 0);
		
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depId);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, Game.width, Game.height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER,GL30.GL_DEPTH_ATTACHMENT,GL30.GL_RENDERBUFFER, depId);
		
		IntBuffer buffer = BufferUtils.createIntBuffer(2);
		buffer.put(GL30.GL_COLOR_ATTACHMENT0);
		buffer.put(GL30.GL_COLOR_ATTACHMENT1);
		buffer.flip();
		GL20.glDrawBuffers(buffer);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		GL11.glViewport(0, 0, Game.width, Game.height);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(90, Game.width/(float)Game.height, 0.001f, 1000f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
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
		rotation[2].put(new float[] {1,0,0,0, 0,0,1,0, 0,-1,0,0, 0,0,0,1}); //up
		rotation[2].flip();
		rotation[3] = BufferUtils.createFloatBuffer(16);
		rotation[3].put(new float[] {1,0,0,0, 0,0,-1,0, 0,1,0,0, 0,0,0,1}); //down
		rotation[3].flip();
		rotation[4] = BufferUtils.createFloatBuffer(16);
		rotation[4].put(new float[] {-1,0,0,0, 0,1,0,0, 0,0,-1,0, 0,0,0,1}); //back
		rotation[4].flip();
		rotation[5] = BufferUtils.createFloatBuffer(16);
		rotation[5].put(new float[] {1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1}); //front
		rotation[5].flip();
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(90, Game.width/(float)Game.height, 0.001f, 1000f);
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
	}
	
	public static void render(int program1, int program2) {
		Camera.update();
		
		renderPass1(program1);
		renderPass2(program2);
	}
	
	private static void renderPass1(int program) {
		GL20.glUseProgram(program);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glLoadIdentity();
		
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
		
		GL11.glRotatef(Camera.ry, 0, 1, 0);
		GL11.glRotatef(Camera.rx, 1, 0, 0);
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
		
		int tex = GL20.glGetUniformLocation(program, "tex");
		GL20.glUniform1i(tex, 0);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 1, 0, 1, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glLoadIdentity();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		
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
		GL20.glDisableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboId);
		
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
		
		GL30.glDeleteRenderbuffers(depId);
		GL11.glDeleteTextures(texId);
		GL30.glDeleteFramebuffers(fboId);
	}
}
