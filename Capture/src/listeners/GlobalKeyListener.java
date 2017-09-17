package listeners;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import gui.CaptureFrame;
import main.Capture;

public class GlobalKeyListener implements NativeKeyListener {
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		boolean flag = true;
		int modifiers = e.getModifiers();
		
		if(
			Capture.shortcut.control && (modifiers & NativeKeyEvent.CTRL_MASK) == 0 ||
			Capture.shortcut.meta && (modifiers & NativeKeyEvent.META_MASK) == 0 ||
			Capture.shortcut.shift && (modifiers & NativeKeyEvent.SHIFT_MASK) == 0 ||
			Capture.shortcut.alt && (modifiers & NativeKeyEvent.ALT_MASK) == 0 ||
			!NativeKeyEvent.getKeyText(e.getKeyCode()).equals(Capture.shortcut.key) ||
			Capture.openDialog != null
		) 
			flag = false;
		
		if(flag) {
			Capture.openDialog = new CaptureFrame();
		}
	}

	public void nativeKeyReleased(NativeKeyEvent e) {}
	public void nativeKeyTyped(NativeKeyEvent e) {}
}
