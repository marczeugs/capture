package gui;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JFrame;

import main.Capture;

@SuppressWarnings("serial")
public class ColorSelectionDialog extends JFrame implements WindowListener {
	private JColorChooser colorChooser;
	private LinkedList<ColorSelectionListener> colorSelectionListeners = new LinkedList<ColorSelectionListener>();
	
	
	public ColorSelectionDialog(String title, Color defaultColor) {
		super(title);
		
		this.setAlwaysOnTop(true);
		this.requestFocus();
		this.requestFocusInWindow();
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setIconImage(Capture.frameIcon);
		this.setResizable(false);
		
		this.addWindowListener(this);
		
		
		colorChooser = new JColorChooser(defaultColor);
		this.add(colorChooser);
		
		
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	
	public void addColorSelectionListener(ColorSelectionListener listener) {
		this.colorSelectionListeners.add(listener);
	}


	@Override
	public void windowClosing(WindowEvent e) {
		for(ColorSelectionListener listener : colorSelectionListeners) 
			listener.colorSelected(colorChooser.getColor());
		
		Capture.openDialog = null;
		this.dispose();
	}
	
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
	public interface ColorSelectionListener {
		void colorSelected(Color color);
	}
	
}
