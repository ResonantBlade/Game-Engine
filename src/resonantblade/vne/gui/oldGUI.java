package resonantblade.vne.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Point3D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import resonantblade.vne.Resources;
import resonantblade.vne.gui.displayable.Displayable;
import resonantblade.vne.gui.displayable.SceneBG;
import resonantblade.vne.modules.vn.UserInputListener;
import resonantblade.vne.script.JSInterpreter;
import resonantblade.vne.script.ScriptInterpreter.Character;

public class oldGUI
{
	public static int WIDTH = 1920;
	public static int HEIGHT = 1080;
	
	private static volatile SceneBG background = SceneBG.BLANK;
	private static volatile List<Displayable> battle = new ArrayList<Displayable>();
	private static volatile List<Displayable> sprites = new ArrayList<Displayable>();
	private static volatile List<Displayable> screen = new ArrayList<Displayable>();
	private static volatile List<Displayable> overlay = new ArrayList<Displayable>();
	
	private volatile BufferedImage overlayBackground;
	private volatile int overlayXMin;
	private volatile int overlayYMin;
	private volatile int overlayXMax;
	private volatile int overlayYMax;
	private volatile int overlayPaddingLeft;
	private volatile int overlayPaddingRight;
	private volatile int overlayPaddingTop;
	private volatile int overlayPaddingBottom;
	
	private JFrame frame;
	private volatile BufferedImage buffer;
	private FPSController fpsController;
	public static volatile boolean[] changingLayer = new boolean[5];
	public static volatile boolean[] isLayerVisible = new boolean[]{true, true, true, true, true};
	
	public static final Object userInteractLock = new Object();
	
	public oldGUI(String title, FPSController fpscon)
	{
		fpsController = fpscon;
		WIDTH = JSInterpreter.<Number>eval("config.screen_width").intValue();
		HEIGHT = JSInterpreter.<Number>eval("config.screen_height").intValue();
		
		String bckgrnd = (String) JSInterpreter.eval("style.dialogue_window.background");
		if(bckgrnd != null)
			overlayBackground = Resources.loadImage(new File(bckgrnd));
		overlayXMin = JSInterpreter.<Number>eval("style.dialogue_window.xMin").intValue();
		overlayYMin = JSInterpreter.<Number>eval("style.dialogue_window.yMin").intValue();
		overlayXMax = JSInterpreter.<Number>eval("style.dialogue_window.xMax").intValue();
		overlayYMax = JSInterpreter.<Number>eval("style.dialogue_window.yMax").intValue();
		overlayPaddingLeft = JSInterpreter.<Number>eval("style.dialogue_window.padding_left").intValue();
		overlayPaddingRight = JSInterpreter.<Number>eval("style.dialogue_window.padding_right").intValue();
		overlayPaddingTop = JSInterpreter.<Number>eval("style.dialogue_window.padding_top").intValue();
		overlayPaddingBottom = JSInterpreter.<Number>eval("style.dialogue_window.padding_bottom").intValue();
		
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		frame = new JFrame(title);
		frame.setSize(960, 540);
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
		
		UserInputListener uil = new UserInputListener();
		frame.addKeyListener(uil);
		frame.addMouseListener(uil);
		frame.addMouseMotionListener(uil);
		frame.addMouseWheelListener(uil);
		//TODO add key and mouse listeners
		
		frame.validate();
		fpsController.start(this);
	}
	
	public void setTitle(String name)
	{
		frame.setTitle(name);
	}
	
	public void updateVisible()
	{
		if(changingLayer[0])
		{
			background.fade();
			changingLayer[0] = background.changing();
			if(isLayerVisible[0])
				return;
		}
		if(changingLayer[1])
		{
			changingLayer[1] = battle.stream().map(Displayable::changing).anyMatch(Boolean::booleanValue);
			if(isLayerVisible[1])
				return;
		}
		if(changingLayer[2])
		{
			sprites.forEach(d -> {
				d.move();
				d.fade();
			});
			changingLayer[2] = sprites.stream().map(Displayable::changing).anyMatch(Boolean::booleanValue);
			if(isLayerVisible[2])
				return;
		}
		if(changingLayer[3])
		{
			changingLayer[3] = screen.stream().map(Displayable::changing).anyMatch(Boolean::booleanValue);
			if(isLayerVisible[3])
				return;
		}
		if(changingLayer[4])
		{
			changingLayer[4] = overlay.stream().map(Displayable::changing).anyMatch(Boolean::booleanValue);
			if(isLayerVisible[4])
				return;
		}
	}
	
	public void drawToImageBuffer()
	{
		Graphics2D g = buffer.createGraphics();
		background.paint(g);
		if(!changingLayer[0] && isLayerVisible[0])
		{
			if(isLayerVisible[1])
				IterationTools.forEach(battle, displayable -> displayable.paint(g));
			if(isLayerVisible[2])
				IterationTools.forEach(sprites, displayable -> displayable.paint(g));
			if(isLayerVisible[3])
				IterationTools.forEach(screen, displayable -> displayable.paint(g));
			if(isLayerVisible[4])
				IterationTools.forEach(overlay, displayable -> displayable.paint(g));
			//battle.forEach(displayable -> displayable.paint(g));
			//sprites.forEach(displayable -> displayable.paint(g));
			//screen.forEach(displayable -> displayable.paint(g));
			//overlay.forEach(displayable -> displayable.paint(g));
		}
		g.dispose();
	}
	
	public void repaint()
	{
		frame.repaint();
	}
	
	public static void say(String name, Color color, String text)
	{
		System.out.println((name.isEmpty() ? "" : name + ": ") + text);
	}
	
	public static void say(Character charr, String text)
	{
		say(charr.name, charr.color, text);
	}
	
	public static void changeScene(Image img, String... transitions)
	{
		String transition = Arrays.stream(transitions).collect(Collectors.joining(" "));
		int fadeIndex = transition.indexOf("fade");
		double fadeDuration = 1.0D;
		if(fadeIndex != -1)
		{
			int amtIndex = fadeIndex + 4;
			String temp = transition.substring(amtIndex).trim();
			if(!temp.isEmpty())
			{
				int spaceIndex = temp.indexOf(' ');
				if(spaceIndex == -1)
					try{fadeDuration = Double.parseDouble(transition);}catch(Exception e){}
				else
					try{fadeDuration = Double.parseDouble(transition.substring(0, spaceIndex));}catch(Exception e){}
			}
		}
		background = new SceneBG(img, fadeIndex != -1, fadeDuration);
		changingLayer[0] = true;
		battle.clear();
		sprites.clear();
		screen.clear();
		overlay.clear();
	}
	
	public static void showLayer(int layer)
	{
		isLayerVisible[layer] = true;
	}
	
	public static void hideLayer(int layer)
	{
		isLayerVisible[layer] = false;
	}
	
	public static void showImage(Image img, Point3D position, float alpha, String... transitions)
	{
		Displayable current = sprites.stream().filter(displayable -> displayable.getImage().tagsAreSame(img.getTags())).findFirst().orElse(null);
		if(current != null)
		{
			current.setImage(img);
			current.setTransitions(transitions);
			if(Double.isNaN(position.getX()))
				position = new Point3D(current.getPosition().getX(), position.getY(), position.getZ());
			if(Double.isNaN(position.getY()))
				position = new Point3D(position.getX(), current.getPosition().getY(), position.getZ());
			if(Double.isNaN(position.getZ()))
				position = new Point3D(position.getX(), position.getY(), current.getPosition().getZ());
			double ease = 0.0D;
			for(int i = 0; i < transitions.length; i++)
			{
				if(transitions[i].equals("ease"))
				{
					if(i != transitions.length - 1)
						ease = Double.parseDouble(transitions[i + 1]);
					else
						ease = 1.0D;
				}
				if(transitions[i].equals("fade"))
				{
					if(i != transitions.length - 1)
						current.fade(Double.parseDouble(transitions[i + 1]), true);
					else
						current.fade(1.0D, true);
				}
			}
			if(ease == 0.0D)
				current.setPosition(position);
			else
				current.moveTo(position, ease);
		}
		else
		{
			if(Double.isNaN(position.getX()))
				position = new Point3D(0.5D, position.getY(), position.getZ());
			if(Double.isNaN(position.getY()))
				position = new Point3D(position.getX(), 0.5D, position.getZ());
			if(Double.isNaN(position.getZ()))
				position = new Point3D(position.getX(), position.getY(), 1.0D);
			current = new ImageDisplayable(img, position, alpha, transitions);
			for(int i = 0; i < transitions.length; i++)
			{
				if(transitions[i].equals("fade"))
				{
					if(i != transitions.length - 1)
						current.fade(Double.parseDouble(transitions[i + 1]), true);
					else
						current.fade(1.0D, true);
				}
			}
			sprites.add(current);
		}
		changingLayer[2] = true;
	}
	
	public static void hideImage(Image img, String[] transitions)
	{
		Displayable current = sprites.stream().filter(displayable -> displayable.getImage().tagsAreSame(img.getTags())).findFirst().orElse(null);
		if(current != null)
		{
			for(int i = 0; i < transitions.length; i++)
			{
				if(transitions[i].equals("fade"))
				{
					if(i != transitions.length - 1)
						current.fade(Double.parseDouble(transitions[i + 1]), false);
					else
						current.fade(1.0D, false);
				}
			}
		}
		changingLayer[2] = true;
	}
	
	public static boolean updating()
	{
		for(int i = 0; i < changingLayer.length; i++)
			if(changingLayer[i] && isLayerVisible[i])
				return true;
		return false;
	}
	
	/**
	 * This method is called whenever the game is closed. Use this method to clean up
	 * anything that might not be cleaned up automatically when the JVM shuts down.
	 * @see resonantblade.vne.audio.AudioSystem#kill()
	 */
	public static void kill()
	{
		// TODO
	}
}