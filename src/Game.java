import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Game {
	
	public static int width, height;
	public Shaders shaders;
	
	private void start() throws LWJGLException {
		width = 800;
		height = 600;
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setTitle("Geometry Shader Test");
		Display.create();
		
		Renderer.init();
		shaders = new Shaders();
		shaders.init();
	}
	
	private void run() {
		
		while (!Display.isCloseRequested()) {
			Renderer.render(shaders.getProgram());
			
			Display.update();
			Display.sync(60);
		}
	}
	
	private void end() {
		Renderer.end();
		shaders.end();
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
