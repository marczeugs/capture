package gui;

import java.awt.Color;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import listeners.PopupMouseListener;
import main.Capture;

@SuppressWarnings("serial")
public class CapturePopupMenu extends JPopupMenu {
	
	public CapturePopupMenu() {
		LinkedList<JComponent> popupMenuComponents = new LinkedList<JComponent>();
		
		JMenuItem menuItem;
		
		menuItem = new JMenuItem("Capture v" + Capture.version + " by Marc, 2017");
		menuItem.setEnabled(false);
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		this.addSeparator();
		
		menuItem = new JMenuItem("Configure shortcut");
		menuItem.addActionListener((ActionEvent e) -> {
			if(Capture.openDialog == null) Capture.openDialog = new ShortcutConfigurator();
			else Capture.openDialog.requestFocus();
		});
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		menuItem = new JMenuItem("Take screenshot");
		menuItem.addActionListener((ActionEvent e) -> {
			if(Capture.openDialog == null) Capture.openDialog = new CaptureFrame();
			else Capture.openDialog.requestFocus();
		});
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		this.addSeparator();
		
		menuItem = new JMenuItem("Choose overlay color");
		menuItem.addActionListener((ActionEvent e) -> {
			if(Capture.openDialog != null) {
				Capture.openDialog.requestFocus();
				return;
			}
			
			ColorSelectionDialog colorSelection = new ColorSelectionDialog("Choose an overlay color.", Capture.overlayColor);
			colorSelection.addColorSelectionListener(new ColorSelectionDialog.ColorSelectionListener() {
				@Override
				public void colorSelected(Color color) {
					Capture.overlayColor = color;
				}
			});
			
			Capture.openDialog = colorSelection;
		});
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		menuItem = new JMenuItem("Choose selection color");
		menuItem.addActionListener((ActionEvent e) -> {
			if(Capture.openDialog != null) {
				Capture.openDialog.requestFocus();
				return;
			}
			
			ColorSelectionDialog colorSelection = new ColorSelectionDialog("Choose a selection color.", Capture.selectionColor);
			colorSelection.addColorSelectionListener(new ColorSelectionDialog.ColorSelectionListener() {
				@Override
				public void colorSelected(Color color) {
					Capture.selectionColor = color;
				}
			});

			Capture.openDialog = colorSelection;
		});
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		menuItem = new JMenuItem("Choose text color");
		menuItem.addActionListener((ActionEvent e) -> {
			if(Capture.openDialog != null) {
				Capture.openDialog.requestFocus();
				return;
			}
			
			ColorSelectionDialog colorSelection = new ColorSelectionDialog("Choose a text color.", Capture.textColor);
			colorSelection.addColorSelectionListener(new ColorSelectionDialog.ColorSelectionListener() {
				@Override
				public void colorSelected(Color color) {
					Capture.textColor = color;
				}
			});

			Capture.openDialog = colorSelection;
		});
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		this.addSeparator();
		
		menuItem = new JCheckBoxMenuItem("Freeze screen while taking picture", Capture.freezeScreen);
		menuItem.addActionListener((ActionEvent e) -> {
			Capture.freezeScreen = ((JCheckBoxMenuItem) e.getSource()).isSelected();
		});
		menuItem.setUI(new StayOpenJCheckBoxMenuItemUI());
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		menuItem = new JCheckBoxMenuItem("Copy image to clipboard", Capture.copyToClipboard);
		menuItem.addActionListener((ActionEvent e) -> {
			Capture.copyToClipboard = ((JCheckBoxMenuItem) e.getSource()).isSelected();
		});
		menuItem.setUI(new StayOpenJCheckBoxMenuItemUI());
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		menuItem = new JCheckBoxMenuItem("Open save dialog after taking picture", Capture.trySaving);
		menuItem.addActionListener((ActionEvent e) -> {
			Capture.trySaving = ((JCheckBoxMenuItem) e.getSource()).isSelected();
		});
		menuItem.setUI(new StayOpenJCheckBoxMenuItemUI());
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		menuItem = new JCheckBoxMenuItem("Show after saving", Capture.showAfterSaving);
		menuItem.addActionListener((ActionEvent e) -> {
			Capture.showAfterSaving = ((JCheckBoxMenuItem) e.getSource()).isSelected();
		});
		menuItem.setUI(new StayOpenJCheckBoxMenuItemUI());
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		menuItem = new JCheckBoxMenuItem("Show message on startup", Capture.showStartupMessage);
		menuItem.addActionListener((ActionEvent e) -> {
			Capture.showStartupMessage = ((JCheckBoxMenuItem) e.getSource()).isSelected();
		});
		menuItem.setUI(new StayOpenJCheckBoxMenuItemUI());
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		this.addSeparator();
		
		menuItem = new JMenuItem("Close Capture");
		menuItem.addActionListener((ActionEvent e) -> {
			SystemTray.getSystemTray().remove(Capture.trayIcon);
			System.exit(0);
		});
		this.add(menuItem);
		popupMenuComponents.add(menuItem);
		
		PopupMouseListener listener = new PopupMouseListener();
		for(JComponent component : popupMenuComponents) {
			component.addMouseListener(listener);
		}
		this.addMouseListener(listener);
	}
	
}