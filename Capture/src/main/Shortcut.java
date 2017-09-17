package main;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

public class Shortcut {
	public boolean control;
	public boolean meta;
	public boolean shift;
	public boolean alt;
	public String key;
	
	public Shortcut(boolean control, boolean meta, boolean shift, boolean alt, String key) {
		this.control = control;
		this.meta = meta;
		this.shift = shift;
		this.alt = alt;
		this.key = key;
	}
	
	public Shortcut(String s) {
		Arrays.stream(s.split(",")).map(kv -> kv.split(":")).forEach(a -> {
			try {
				if(a[1].equals("true") || a[1].equals("false"))
					this.getClass().getField(a[0]).set(this, Boolean.valueOf(a[1]));
				else
					this.getClass().getField(a[0]).set(this, a[1]);
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Failed to deserialize shortcut. Your settings may not be loaded. Details have been logged to the error log.", "Capture: Warning", JOptionPane.WARNING_MESSAGE);
				Capture.logger.log(Level.WARNING, "Failed to deserialize shortcut. Default settings may be used.", e);
			}
		});
	}
	
	public String toString() {
		String serializedShortcut = Arrays.stream(this.getClass().getFields()).map(field -> {
			String s = "";
			try {
				s = field.getName() + ":" + field.get(this);
			} catch(IllegalAccessException e) {
				JOptionPane.showMessageDialog(null, "Failed to serialize shortcut. Your settings may not be saved. Details have been logged to the error log.", "Capture: Warning", JOptionPane.WARNING_MESSAGE);
				Capture.logger.log(Level.WARNING, "Failed to serialize shortcut. Default settings may be used.", e);
			}

			return s;
		}).collect(Collectors.joining(","));
		
		if(serializedShortcut == null) 
			serializedShortcut = "";
		
		return serializedShortcut;
	}
}