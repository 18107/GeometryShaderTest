import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

public class Renderer {
	
	private static float rot = 0;
	private static FloatBuffer matrix;

	public static void init() {
		GL11.glViewport(0, 0, Game.width, Game.height);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(90, Game.width/(float)Game.height, 0.001f, 1000f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		matrix = BufferUtils.createFloatBuffer(16);
	}
	
	public static void render(int program) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
		
		Camera.update();
		
		drawBox(program);
	}
	
	private static void drawBox(int program) {
		GL20.glUseProgram(program);
		
		GL11.glRotatef(Camera.ry, 0, 1, 0);
		GL11.glRotatef(Camera.rx, 1, 0, 0);
		GL11.glTranslatef(-Camera.x, -Camera.y, -Camera.z);
		
		GL11.glColor3f(1, 0, 1);
		//GL11.glRotatef(90, 0, 0, 1);
		//GL11.glRotatef(90, 0, 1, 0);
		GL11.glTranslatef(0, 0, -10);
		
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, matrix);
		int proj = GL20.glGetUniformLocation(program, "projection");
		GL20.glUniformMatrix4(proj, false, matrix);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrix);
		int model = GL20.glGetUniformLocation(program, "modelview");
		GL20.glUniformMatrix4(model, false, matrix);
		//System.out.printf("%f, %f, %f, %f\n", matrix.get(0), matrix.get(4), matrix.get(8), matrix.get(0xC));
		//System.out.printf("%f, %f, %f, %f\n", matrix.get(1), matrix.get(5), matrix.get(9), matrix.get(0xD));
		//System.out.printf("%f, %f, %f, %f\n", matrix.get(2), matrix.get(6), matrix.get(0xA), matrix.get(0xE));
		//System.out.printf("%f, %f, %f, %f\n\n", matrix.get(3), matrix.get(7), matrix.get(0xB), matrix.get(0xF));
		
		rot+=0.2f;
		
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			GL11.glVertex3f(-1, -1, 0);
			GL11.glVertex3f(-1, 1, 0);
			GL11.glVertex3f(1, 1, 0);
			//GL11.glVertex3f(1, 1, 0);
			//GL11.glVertex3f(1, -1, 0);
			//GL11.glVertex3f(-1, -1, 0);
		}
		GL11.glEnd();
		GL20.glUseProgram(0);
	}
}
