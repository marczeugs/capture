package gui;
import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import main.Capture;

@SuppressWarnings("serial")
public class CaptureFrame extends JFrame implements MouseListener, MouseMotionListener, KeyListener {
	private int startX = -1, startY = -1, currentX = -1, currentY = -1;
	private BufferedImage screenshot; // for frozen display
	private int currentScreenIndex;
	private GraphicsDevice[] screens;
	private boolean selectable;
	
	
	public CaptureFrame() {
		super();
		
		screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		this.currentScreenIndex = 0;
		for(int i = 0; i < screens.length; i++) {
			if(screens[i] == GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()) {
				this.currentScreenIndex = i;
				break;
			}
		}
		
		this.setUndecorated(true);
		this.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		this.setIconImage(Capture.frameIcon);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		updateScreen();
		this.selectable = true;
		
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.requestFocus();
	}
	
	public void takeScreenshot() {
		if(Math.abs(this.currentX - this.startX) == 0 && Math.abs(this.currentY - this.startY) == 0) {
			this.dispose();
			Capture.openDialog = null;
			return;
		}

		this.selectable = false;
		this.setBackground(new Color(0, 0, 0, 0));
		
		try {
			BufferedImage image = new Robot().createScreenCapture(new Rectangle(
				screens[currentScreenIndex].getDefaultConfiguration().getBounds().x + Math.min(this.startX, this.currentX), 
				screens[currentScreenIndex].getDefaultConfiguration().getBounds().y + Math.min(this.startY, this.currentY), 
				Math.abs(this.currentX - this.startX), 
				Math.abs(this.currentY - this.startY)
			));
			
			if(Capture.trySaving) {
				JFileChooser fc = new JFileChooser();
			
				fc.setSelectedFile(new File("image.png"));
				
				if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					String ex = Arrays.asList(new String[] {"png", "jpeg", "jpg", "gif", "bmp"}).contains(fc.getName(fc.getSelectedFile()).split("\\.")[fc.getName(fc.getSelectedFile()).split("\\.").length - 1]) ? fc.getName(fc.getSelectedFile()).split("\\.")[fc.getName(fc.getSelectedFile()).split("\\.").length - 1] : "png";
					ImageIO.write(image, ex, fc.getSelectedFile());
					
					if(Capture.showAfterSaving)
						Desktop.getDesktop().open(fc.getSelectedFile());
				}
			}
			
			if(Capture.copyToClipboard)
				this.getToolkit().getSystemClipboard().setContents(new TransferableImage(image), null);
		} catch(IOException | AWTException e) {
            JOptionPane.showMessageDialog(null, "Error while processing screenshot. Details have been logged to the error log.", "Capture: Warning", JOptionPane.WARNING_MESSAGE);
            Capture.logger.log(Level.WARNING, "Error while processing screenshot.", e);
		}
		
		this.dispose();
		Capture.openDialog = null;
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		
		super.paint(g2D);
		
		if(Capture.freezeScreen)
			g2D.drawImage(this.screenshot, 0, 0, null);
		
		if(this.selectable) {
			g2D.setColor(new Color(Capture.overlayColor.getRGB()));
			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Capture.overlayColor.getAlpha() / 255.0f));
			g2D.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			g2D.setColor(new Color(Capture.selectionColor.getRGB()));
			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Capture.selectionColor.getAlpha() / 255.0f));
			g2D.fillRect(startX, startY, currentX - startX, currentY - startY);
			
			if(this.currentX != -1 && this.currentY != -1) {
				g2D.setColor(Capture.textColor);
				g2D.setFont(new Font("Segoe UI", Font.PLAIN, 18));
				g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				g2D.drawString("X: " + this.currentX + ", Y: " + this.currentY + ", Width: " + Math.abs(this.currentX - this.startX) + ", Height: " + Math.abs(this.currentY - this.startY), this.currentX + 10, this.currentY + 25);
			}
		}
	}
	
	public void updateScreen() {
		this.setBounds(
			screens[currentScreenIndex].getDefaultConfiguration().getBounds().x, 
			screens[currentScreenIndex].getDefaultConfiguration().getBounds().y, 
			screens[currentScreenIndex].getDefaultConfiguration().getBounds().width, 
			screens[currentScreenIndex].getDefaultConfiguration().getBounds().height
		);
		
		try {
			this.screenshot = new Robot().createScreenCapture(new Rectangle(
				screens[currentScreenIndex].getDefaultConfiguration().getBounds().x, 
				screens[currentScreenIndex].getDefaultConfiguration().getBounds().y, 
				screens[currentScreenIndex].getDefaultConfiguration().getBounds().width, 
				screens[currentScreenIndex].getDefaultConfiguration().getBounds().height
			));
		} catch(AWTException e) {
            JOptionPane.showMessageDialog(null, "Error while taking screenshot. Details have been logged to the error log.", "Capture: Warning", JOptionPane.WARNING_MESSAGE);
            Capture.logger.log(Level.WARNING, "Error while taking screenshot.", e);
		}
		
		this.requestFocus();
	}

	
	public void mousePressed(MouseEvent event) {
		this.startX = event.getX();
		this.startY = event.getY();
	}
	
	public void mouseReleased(MouseEvent e) {
		this.currentX = e.getX();
		this.currentY = e.getY();
		
		takeScreenshot();
	}

	public void mouseDragged(MouseEvent e) {
		if(this.startX == -1 || this.startY == -1) return;
		
		this.currentX = e.getX();
		this.currentY = e.getY();
		
		this.repaint();
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_LEFT) {
			if(this.currentScreenIndex > 0)
				this.currentScreenIndex--;
		} else if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if(this.currentScreenIndex < this.screens.length - 1)
				this.currentScreenIndex++;
		} else {
			this.dispose();
			Capture.openDialog = null;
			return;
		}
		
		updateScreen();
	}
	
	 private class TransferableImage implements Transferable {
        private Image image;

        public TransferableImage(Image image) {
            this.image = image;
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if(flavor.equals(DataFlavor.imageFlavor) && image != null) {
                return image;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = DataFlavor.imageFlavor;
            
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for(int i = 0; i < flavors.length; i++) {
                if(flavor.equals(flavors[i])) {
                    return true;
                }
            }

            return false;
        }
    }
	
	public void keyReleased(KeyEvent event) {}
	public void keyTyped(KeyEvent event) {}
	public void mouseMoved(MouseEvent event) {}
	public void mouseClicked(MouseEvent event) {}
	public void mouseEntered(MouseEvent event) {}
	public void mouseExited(MouseEvent event) {}

}
