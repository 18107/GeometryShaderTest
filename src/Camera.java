import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class Camera {

	public static float x;
	public static float y;
	public static float z;
	public static float rx;
	public static float ry;
	
	public static void update() {
		while(Keyboard.next()) {
			if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
				if(!Keyboard.getEventKeyState()) {
					Mouse.setGrabbed(!Mouse.isGrabbed());
					Mouse.setCursorPosition(Display.getWidth()/2, Display.getHeight()/2);
				}
			}
		}
		
		int x = 0;
		int y = 0;
		int z = 0;
		if(Mouse.isGrabbed()) {
			if(Keyboard.isKeyDown(Keyboard.KEY_W)) z = -1;
			if(Keyboard.isKeyDown(Keyboard.KEY_S)) z = 1;
			if(Keyboard.isKeyDown(Keyboard.KEY_A)) x = -1;
			if(Keyboard.isKeyDown(Keyboard.KEY_D)) x = 1;
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) y = 1;
			if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) y = -1;
			
			ry += Mouse.getDX()/4;
			rx -= Mouse.getDY()/4;
			Mouse.setCursorPosition(Display.getWidth()/2, Display.getHeight()/2);
			
			if (ry > 360) ry -= 360;
			else if (ry < 0) ry += 360;
			if (rx < -90) rx = -90;
			else if (rx > 90) rx = 90;
		}
		
		Camera.x += 0.25f*(Math.cos(Math.toRadians(ry)) * x - Math.sin(Math.toRadians(ry)) * z);
		Camera.y += 0.25f*y;
		Camera.z += 0.25f*(Math.cos(Math.toRadians(ry)) * z + Math.sin(Math.toRadians(ry)) * x);
	}
}
