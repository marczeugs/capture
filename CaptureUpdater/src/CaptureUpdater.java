import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CaptureUpdater {
	public static Logger logger = Logger.getLogger(CaptureUpdater.class.getName());
	public static String savePath;
	public static String localVersion = "0";
	
	public static void main(String[] args) {
		savePath = System.getenv("APPDATA");
		if(savePath == null) {
			savePath = "./";
		}
		savePath += "/Capture/";
		new File(savePath).mkdirs();

		logger.setLevel(Level.WARNING);
		try {
			File errorLog = new File(savePath + "updater.log");
			
			if(errorLog.exists())
				errorLog.delete();
			errorLog.createNewFile();
			
			System.setErr(new PrintStream(new FileOutputStream(errorLog)));
		} catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to initialize error logging. Details have been logged to the error log.", "Capture updater: Warning", JOptionPane.WARNING_MESSAGE);
			logger.log(Level.WARNING, "Failed to initialize error logging.", e);
		}
		
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(null, "Exception while setting Java Look and Feel. Details have been logged to the error log.", "Capture updater: Warning", JOptionPane.WARNING_MESSAGE);
			logger.log(Level.WARNING, "Exception while setting Java Look and Feel.", e);
		}
		
		
		String latestVersion = "1", versionInfo = "Changelog could not be loaded.", downloadLink = "https://github.com/marczeugs/capture/raw/master/releases/CaptureV1.jar";;
		try {
			URL latestVersionURL = new URL("https://raw.githubusercontent.com/marczeugs/capture/master/releases/latest.version");
			Properties latestVersionInfo = new Properties();
			latestVersionInfo.load(latestVersionURL.openStream());
			
			latestVersion = latestVersionInfo.getProperty("version");
			versionInfo = latestVersionInfo.getProperty("info");
			downloadLink = latestVersionInfo.getProperty("downloadLink");
		} catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to fetch information for latest version. Existing version will be started. Details have been logged to the error log.", "Capture updater: Warning", JOptionPane.WARNING_MESSAGE);
			logger.log(Level.WARNING, "Failed to fetch information for latest version. Existing version will be started.", e);
			startCapture();
			return;
		}
		
		
		try {
			File file = new File(savePath + "updater.settings");
			
			if(file.exists()) {
				FileInputStream fileInput = new FileInputStream(file);
				Properties localVersionInfo = new Properties();
				localVersionInfo.load(fileInput);
				fileInput.close();

				localVersion = localVersionInfo.getProperty("version");
			} else {
				JOptionPane.showMessageDialog(null, "No updater info file found. A new one will be created.", "Warning", JOptionPane.WARNING_MESSAGE);
				logger.log(Level.WARNING, "No updater info file found.");
			}
		} catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to read updater info file. Default settings will be used, existing version will be started. Details have been logged to the error log.", "Capture updater: Warning", JOptionPane.WARNING_MESSAGE);
			logger.log(Level.WARNING, "Failed to read updater info file. Existing version will be started.", e);
			startCapture();
			return;
		}
		
		
		try {
			if(localVersion.compareTo(latestVersion) < 0 || !(new File(savePath + "Capture.jar").exists())) {
				int selection;
				if(localVersion.compareTo(latestVersion) < 0) {
					selection = JOptionPane.showConfirmDialog(null, 
						"A newer version of Capture than the one currently installed has been found.\n" + 
						"Locally installed version: " + ((localVersion.equals("0")) ? "Not installed" : localVersion) + "\n" + 
						"Available version: " + latestVersion + "\n" + 
						"Changes: " + versionInfo + "\n" + 
						"Do you want to update?", "Capture", JOptionPane.INFORMATION_MESSAGE);
				} else {
					selection = JOptionPane.showConfirmDialog(null, 
						"No valid installation of Capture has been found.\n" + 
						"Do you want to install the lastest version?", "Capture", JOptionPane.INFORMATION_MESSAGE);
				}
				
				if(selection == JOptionPane.OK_OPTION) {
					URL downloadURL = new URL(downloadLink);
					try(InputStream inStream = downloadURL.openStream()) {
					    Files.copy(inStream, Paths.get(savePath + "Capture.jar"), StandardCopyOption.REPLACE_EXISTING);
					}
					
					localVersion = latestVersion;
					
					JOptionPane.showMessageDialog(null, "New version successfully downloaded.", "Info", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Failed to download update, existing version will be started. Details have been logged to the error log.", "Capture updater: Warning", JOptionPane.WARNING_MESSAGE);
			logger.log(Level.WARNING, "Failed to read updater info file. Existing version will be started.", e);
		}
		
		startCapture();
	}
	
	public static void startCapture() {
		try {
			Properties properties = new Properties();
			properties.setProperty("version", String.valueOf(localVersion));
			
			File file = new File(savePath + "updater.settings");
			file.getParentFile().mkdirs();
			file.createNewFile();
			
			FileOutputStream fileOut = new FileOutputStream(file);
			properties.store(fileOut, null);
			fileOut.flush();
			fileOut.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to write updater info file. Your local version won't be saved. Details have been logged to the error log." + e.getMessage(), "Capture updater: Warning", JOptionPane.WARNING_MESSAGE);
			logger.log(Level.WARNING, "Failed to read updater info file. Local version won't be saved.", e);
		}
		
		try {
			if(new File(savePath + "Capture.jar").exists()) {
				Runtime.getRuntime().exec("javaw -jar \"" + savePath + "Capture.jar\"");
			} else {
				JOptionPane.showMessageDialog(null, "No local installation found. Cannot start Capture.", "Capture updater: Fatal error", JOptionPane.ERROR_MESSAGE);
				logger.log(Level.SEVERE, "No local installation found. Cannot start Capture.");
			}
		} catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to start Capture. Details have been logged to the error log.", "Capture updater: Fatal error", JOptionPane.ERROR_MESSAGE);
			logger.log(Level.SEVERE, "Failed to start Capture.", e);
		}
	}
	
}