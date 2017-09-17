package listeners;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import main.Capture;

public class GlobalMouseListener implements NativeMouseListener {

	@Override
	public void nativeMouseReleased(NativeMouseEvent e) {
		if(!Capture.popupMenuClicked)
			Capture.popupMenu.setVisible(false);
	}
	
	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
		Capture.popupMenuClicked = false;
	}
	
	public void nativeMouseClicked(NativeMouseEvent e) {}

}
