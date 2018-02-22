import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;

public class Renderer {
	
	private static FloatBuffer matrix;
	private static int vaoId;
	private static int vboId;
	
	private static float[] vertices = {
			-1,-1,-10,
			1,-1,-10,
			1,1,-10
	};

	public static void init() {
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
		
		matrix = BufferUtils.createFloatBuffer(16);
	}
	
	public static void render(int program1, int program2) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glLoadIdentity();
		
		Camera.update();
		
		renderPass1(program1);
		renderPass2(program2);
	}
	
	private static void renderPass1(int program) {
		GL20.glUseProgram(program);
		
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
		
		GL11.glRotatef(Camera.ry, 0, 1, 0);
		GL11.glRotatef(Camera.rx, 1, 0, 0);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrix);
		int view = GL20.glGetUniformLocation(program, "view");
		GL20.glUniformMatrix4(view, false, matrix);
		GL11.glLoadIdentity();
		GL11.glTranslatef(-Camera.x, -Camera.y, -Camera.z);
		
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, matrix);
		int proj = GL20.glGetUniformLocation(program, "projection");
		GL20.glUniformMatrix4(proj, false, matrix);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrix);
		int model = GL20.glGetUniformLocation(program, "model");
		GL20.glUniformMatrix4(model, false, matrix);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertices.length/3);
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
		GL20.glUseProgram(0);
	}
	
	private static void renderPass2(int program) {
		GL20.glUseProgram(program);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(-1, 1, -1, 1, -1, 1);
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f(1, 0);
			GL11.glVertex2f(1, 1);
			GL11.glVertex2f(0, 1);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL20.glUseProgram(0);
	}
	
	public static void end() {
		GL20.glDisableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboId);
		
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
	}
}
