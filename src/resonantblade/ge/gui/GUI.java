package resonantblade.ge.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import resonantblade.ge.modules.ModuleHandler;
import resonantblade.ge.script.JSInterpreter;

public class GUI
{
	public static int WIDTH = 1920;
	public static int HEIGHT = 1080;
	
	private JFrame frame;
	private volatile BufferedImage buffer;
	private FPSController fpsController;
	
	public static final Object userInteractLock = new Object();
	
	public GUI(String title, FPSController fpscon)
	{
		fpsController = fpscon;
		WIDTH = JSInterpreter.<Number>eval("config.screen_width").intValue();
		HEIGHT = JSInterpreter.<Number>eval("config.screen_height").intValue();
		
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		frame = new JFrame(title);
		Dimension size = getOptimalScreenSize(frame, WIDTH, HEIGHT);
		frame.setSize(size.width, size.height);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		@SuppressWarnings("serial")
		JPanel panel = new JPanel()
		{
			@Override
			public void paintComponent(Graphics g)
			{
				Component c = frame.getContentPane();
				g.drawImage(buffer, 0, 0, c.getWidth(), c.getHeight(), null);
				fpsController.resume();
			}
		};
		frame.getContentPane().add(panel);
		
		ModuleHandler.getModules().stream().flatMap(module -> module.getListeners().stream()).forEach(listener -> {
			if(listener instanceof KeyListener)
				frame.addKeyListener((KeyListener) listener);
			if(listener instanceof MouseListener)
				frame.addMouseListener((MouseListener) listener);
			if(listener instanceof MouseMotionListener)
				frame.addMouseMotionListener((MouseMotionListener) listener);
			if(listener instanceof MouseWheelListener)
				frame.addMouseWheelListener((MouseWheelListener) listener);
		});
		
		frame.validate();
		fpsController.start(this);
	}
	
	private Dimension getOptimalScreenSize(JFrame frame, int preferredWidth, int preferredHeight)
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		Dimension screenSize = toolkit.getScreenSize();
		Insets insets = toolkit.getScreenInsets(frame.getGraphicsConfiguration());
		screenSize.width -= insets.left + insets.right;
		screenSize.height -= insets.top + insets.bottom;
		insets = frame.getInsets();
		screenSize.width -= insets.left + insets.right;
		screenSize.height -= insets.top + insets.bottom;
		
		double wRatio = (double) screenSize.width / preferredWidth;
		double hRatio = (double) screenSize.height / preferredHeight;
		
		if(wRatio >= 1.0D && hRatio >= 1.0D)
			return new Dimension(preferredWidth, preferredHeight);
		
		if(wRatio < hRatio)
		{
			// use width
			double scale = (double) screenSize.width / preferredWidth;
			screenSize.width = (int) (preferredWidth * scale);
			screenSize.height = (int) (preferredHeight * scale);
		}
		else
		{
			// use height
			double scale = (double) screenSize.height / preferredHeight;
			screenSize.width = (int) (preferredWidth * scale);
			screenSize.height = (int) (preferredHeight * scale);
		}
		
		return screenSize;
	}
	
	public void updateVisible()
	{
		for(Layer layer : ModuleHandler.layers)
		{
			if(layer.isUpdating())
			{
				layer.update();
				if(layer.isVisible() && layer.isBlocking())
					return;
			}
		}
	}
	
	public void drawToImageBuffer()
	{
		Graphics2D g = buffer.createGraphics();
		for(Layer layer : ModuleHandler.layers)
			if(layer.isVisible())
				layer.paint(g);
		g.dispose();
	}
	
	public void repaint()
	{
		frame.repaint();
	}
	
	public static void showLayer(String name)
	{
		ModuleHandler.layers.stream().filter(module -> module.getName().equals(name)).findFirst().ifPresent(layer -> layer.setVisible(true));
	}
	
	public static void hideLayer(String name)
	{
		ModuleHandler.layers.stream().filter(module -> module.getName().equals(name)).findFirst().ifPresent(layer -> layer.setVisible(false));
	}
	
	/**
	 * This method is called whenever the game is closed. Use this method to clean up
	 * anything that might not be cleaned up automatically when the JVM shuts down.
	 * @see resonantblade.ge.audio.AudioSystem#kill()
	 */
	public static void kill()
	{
		// TODO
	}
}