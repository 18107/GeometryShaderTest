import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Game {
	
	public static int width, height;
	public Shaders shaders1, shaders2;
	
	private void start() throws LWJGLException {
		width = 800;
		height = 800;
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setTitle("Geometry Shader Test");
		Display.create();
		
		shaders1 = new Shaders(Shaders.vertex1, Shaders.geometry1, Shaders.fragment1);
		shaders2 = new Shaders(Shaders.vertex2, Shaders.fragment2);
		Renderer.init();
	}
	
	private void run() {
		
		while (!Display.isCloseRequested()) {
			Renderer.render(shaders1.getProgram(), shaders2.getProgram());
			
			Display.update();
			Display.sync(60);
		}
	}
	
	private void end() {
		Renderer.end();
		if (shaders1 != null)
			shaders1.end();
		if (shaders2 != null)
			shaders2.end();
		Display.destroy();
	}

	public static void main(String[] args) {
		Game game = new Game();
		try {
			game.start();
			game.run();
		} catch (LWJGLException e) {
			e.printStackTrace();
		} finally {
			game.end();
		}
	}
}
