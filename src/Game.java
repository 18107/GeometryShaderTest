import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Game {
	
	public Shaders shaders1, shaders2;
	
	public static boolean running = true;
	
	private void start() throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(1600, 800));
		Display.setTitle("Geometry Shader Test");
		Display.setResizable(true);
		Display.create();
		
		shaders1 = new Shaders(Shaders.vertex1, Shaders.geometry1, Shaders.fragment1);
		shaders2 = new Shaders(Shaders.quad, Shaders.equirectangular);
		Renderer.init();
	}
	
	private void run() {
		
		while (!Display.isCloseRequested() && running) {
			if (Display.wasResized()) {
				Renderer.resize();
			}
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
