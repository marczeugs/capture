package listeners;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import main.Capture;

public class PopupMouseListener implements MouseListener {

	@Override
	public void mousePressed(MouseEvent arg0) {
		Capture.popupMenuClicked = true;
	}

	public void mouseReleased(MouseEvent arg0) {}
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	
}
