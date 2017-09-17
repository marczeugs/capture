package main;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import gui.CaptureFrame;
import gui.CapturePopupMenu;
import listeners.GlobalKeyListener;
import listeners.GlobalMouseListener;

public class Capture {
	public static Logger logger = Logger.getLogger(Capture.class.getName());
	public static String version = "2";
	
	public static SystemTray systemTray;
	public static TrayIcon trayIcon;
	public static JFrame openDialog = null;

	public static CapturePopupMenu popupMenu;
	public static Shortcut shortcut = new Shortcut(true, false, false, true, "C");
	public static Image frameIcon = Toolkit.getDefaultToolkit().getImage(Capture.class.getResource("/frameIcon.png"));

	public static boolean popupMenuClicked;
	public static String savePath;
	
	public static boolean freezeScreen = true;
	public static boolean copyToClipboard = true;
	public static boolean trySaving = false;
	public static boolean showAfterSaving = false;
	public static boolean showStartupMessage = true;
	public static Color overlayColor = new Color(0, 0, 0, 0.3f);
	public static Color selectionColor = new Color(96, 96, 255, (int) (255 * 0.3f));
	public static Color textColor = new Color(255, 255, 255);

	
	public static void main(String[] args) {
		savePath = System.getenv("APPDATA");
		if(savePath == null) {
			savePath = "./";
		}
		savePath += "/Capture/";
		new File(savePath).mkdirs();

		logger.setLevel(Level.WARNING);
		try {
			File errorLog = new File(savePath + "capture.log");
			
			if(errorLog.exists())
				errorLog.delete();
			errorLog.createNewFile();
			
			System.setErr(new PrintStream(new FileOutputStream(errorLog)));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to initialize error logging. Details have been logged to the error log.", "Capture: Warning", JOptionPane.WARNING_MESSAGE);
			logger.log(Level.WARNING, "Failed to initialize error logging.", e);
		}
		
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				init();
			}
		});
	}
	
	public static void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(null, "Exception while setting Java Look and Feel. Details have been logged to the error log.", "Capture: Warning", JOptionPane.WARNING_MESSAGE);
			logger.log(Level.WARNING, "Exception while setting Java Look and Feel.", e);
		}
		
		try {
			File file = new File(savePath + "capture.settings");
			
			if(file.exists()) {
				FileInputStream fileInput = new FileInputStream(file);
				Properties properties = new Properties();
				properties.load(fileInput);
				fileInput.close();

				if(properties.getProperty("freezeScreen") != null) freezeScreen = Boolean.valueOf(properties.getProperty("freezeScreen"));
				if(properties.getProperty("copyToClipboard") != null) copyToClipboard = Boolean.valueOf(properties.getProperty("copyToClipboard"));
				if(properties.getProperty("trySaving") != null) trySaving = Boolean.valueOf(properties.getProperty("trySaving"));
				if(properties.getProperty("showAfterSaving") != null) showAfterSaving = Boolean.valueOf(properties.getProperty("showAfterSaving"));
				if(properties.getProperty("showStartupMessage") != null) showStartupMessage = Boolean.valueOf(properties.getProperty("showStartupMessage"));
				if(properties.getProperty("shortcut") != null) shortcut = new Shortcut(properties.getProperty("shortcut"));
				if(properties.getProperty("overlayColor") != null) overlayColor = new Color(Integer.parseInt(properties.getProperty("overlayColor")), true);
				if(properties.getProperty("selectionColor") != null) selectionColor = new Color(Integer.parseInt(properties.getProperty("selectionColor")), true);
				if(properties.getProperty("textColor") != null) textColor = new Color(Integer.parseInt(properties.getProperty("textColor")), true);
			} else {
				JOptionPane.showMessageDialog(null, "No properties file found. Default settings will be used.", "Capture: Warning", JOptionPane.WARNING_MESSAGE);
				logger.log(Level.WARNING, "No properties file found.");
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to read properties file. Default settings may be used. Details have been logged to the error log.", "Capture: Warning", JOptionPane.WARNING_MESSAGE);
			logger.log(Level.WARNING, "Failed to read properties file.", e);
		}
		

		Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			JOptionPane.showMessageDialog(null, "Failed to register native hook. Details have been logged to the error log.", "Capture: Fatal Error", JOptionPane.ERROR_MESSAGE);
			logger.log(Level.SEVERE, "Failed to register native hook.", e);
			System.exit(0);
		}
		
		GlobalScreen.addNativeMouseListener(new GlobalMouseListener());
		GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
		
		if(!SystemTray.isSupported()) {
            JOptionPane.showMessageDialog(null, "Your operating system doesn't support usage of the system tray.", "Capture: Fatal Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Operating system doesn't support usage of the system tray.");
            System.exit(0);
        }
		
		systemTray = SystemTray.getSystemTray();
		popupMenu = new CapturePopupMenu();
		
		try {
			trayIcon = new TrayIcon(ImageIO.read(Capture.class.getResource("/icon.png")), "Capture", null);
			trayIcon.setImageAutoSize(true);
			
			trayIcon.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if(e.isPopupTrigger()) {
						popupMenu.setLocation(e.getX(), e.getY());
						popupMenu.setInvoker(popupMenu);
						popupMenu.setVisible(true);
		            } else {
		            	if(Capture.openDialog == null) Capture.openDialog = new CaptureFrame();
		    			else Capture.openDialog.requestFocus();
					}
				}
			});

			systemTray.add(trayIcon);
		} catch (IOException | AWTException e) {
            JOptionPane.showMessageDialog(null, "Error while adding the icon to the system tray. Details have been logged to the error log.", "Capture: Fatal Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Error while adding the icon to the system tray.");
            System.exit(0);
		}
		
		Thread shutdownThread = new Thread(new Runnable() {
			@Override
			public void run() {
				finalizeProgram();
			}
		});
		shutdownThread.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(shutdownThread);
		
		if(showStartupMessage)
			trayIcon.displayMessage("Capture", "Capture has been started.", TrayIcon.MessageType.INFO);
	}
	
	public static void finalizeProgram() {
		try {
			Properties properties = new Properties();
			properties.setProperty("showAfterSaving", String.valueOf(showAfterSaving));
			properties.setProperty("freezeScreen", String.valueOf(freezeScreen));
			properties.setProperty("copyToClipboard", String.valueOf(copyToClipboard));
			properties.setProperty("trySaving", String.valueOf(trySaving));
			properties.setProperty("showStartupMessage", String.valueOf(showStartupMessage));
			properties.setProperty("shortcut", shortcut.toString());
			properties.setProperty("overlayColor", String.valueOf(overlayColor.getRGB()));
			properties.setProperty("selectionColor", String.valueOf(selectionColor.getRGB()));
			properties.setProperty("textColor", String.valueOf(textColor.getRGB()));
			
			File file = new File(savePath + "capture.settings");
			file.getParentFile().mkdirs();
			file.createNewFile();
			
			FileOutputStream fileOut = new FileOutputStream(file);
			properties.store(fileOut, null);
			fileOut.flush();
			fileOut.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to write properties file. Your settings won't be saved. Details have been logged to the error log.", "Capture: Warning", JOptionPane.WARNING_MESSAGE);
			logger.log(Level.WARNING, "Failed to read properties file. Default settings may be used.", e);
		}
	}
}