package gui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import main.Capture;
import main.Shortcut;

@SuppressWarnings("serial")
public class ShortcutConfigurator extends JFrame implements KeyListener, WindowListener {
	private JLabel selectedShortcut;
	public Shortcut tempShortcut;
	
	
	public ShortcutConfigurator() {
		super("Shortcut Configurator");
		
		this.setAlwaysOnTop(true);
		this.requestFocus();
		this.requestFocusInWindow();
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setPreferredSize(new Dimension(400, 300));
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setIconImage(Capture.frameIcon);
		this.setResizable(false);
		
		this.addKeyListener(this);
		this.addWindowListener(this);
		
		
		this.tempShortcut = new Shortcut(
			Capture.shortcut.control, 
			Capture.shortcut.meta, 
			Capture.shortcut.shift, 
			Capture.shortcut.alt, 
			Capture.shortcut.key
		);
		
		
		JLabel instruction = new JLabel("<html><span align=\"center\">Enter a normal key + optionally modifier keys like Shift, close this window to save</span></html>");
		instruction.setBorder(new EmptyBorder(10, 10, 110, 10));
		instruction.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(instruction);
		
		this.selectedShortcut = new JLabel("");
		this.selectedShortcut.setAlignmentX(Component.CENTER_ALIGNMENT);
		updateLabel();
		this.add(selectedShortcut);
		
		
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	
	private void updateLabel() {
		String controlDown = this.tempShortcut.control ? "Control + " : "";
		String metaDown = this.tempShortcut.meta ? "Meta + " : "";
		String shiftDown = this.tempShortcut.shift ? "Shift + " : "";
		String altDown = this.tempShortcut.alt ? "Alt + " : "";
		
		this.selectedShortcut.setText(controlDown + metaDown + shiftDown + altDown + this.tempShortcut.key);
	}

	
	@Override
	public void keyPressed(KeyEvent e) {
		if(
			e.getKeyCode() == KeyEvent.VK_CONTROL || 
			e.getKeyCode() == KeyEvent.VK_META ||
			e.getKeyCode() == KeyEvent.VK_SHIFT ||
			e.getKeyCode() == KeyEvent.VK_ALT ||
			e.getKeyCode() == KeyEvent.VK_ALT_GRAPH
		)
			return;
		
		this.tempShortcut.control = e.isControlDown();
		this.tempShortcut.meta = e.isMetaDown();
		this.tempShortcut.shift = e.isShiftDown();
		this.tempShortcut.alt = e.isAltDown();
		this.tempShortcut.key = KeyEvent.getKeyText(e.getKeyCode());
		
		updateLabel();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		Capture.shortcut = this.tempShortcut;
		Capture.openDialog = null;
		this.dispose();
	}
	
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
}
