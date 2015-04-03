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

import resonantblade.vne.Image;
import resonantblade.vne.gui.audio.FadeInMediaTool;
import resonantblade.vne.gui.audio.FadeOutMediaTool;
import resonantblade.vne.gui.audio.VolumeControlMediaTool;
import resonantblade.vne.script.ScriptInterpreter.Character;

import com.xuggle.mediatool.AMediaToolMixin;
import com.xuggle.mediatool.IMediaListener;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaViewer;
import com.xuggle.mediatool.ToolFactory;

public class GUI
{
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	
	private static AudioThread music = new AudioThread("music");
	private static AudioThread sound = new AudioThread("sound");
	private static AudioThread voice = new AudioThread("voice");
	public static VolumeControlMediaTool musicVolume = new VolumeControlMediaTool();
	public static VolumeControlMediaTool soundVolume = new VolumeControlMediaTool();
	public static VolumeControlMediaTool voiceVolume = new VolumeControlMediaTool();
	private static volatile SceneBG background = SceneBG.BLANK;
	private static List<Displayable> battle = new ArrayList<Displayable>();
	private static List<Displayable> sprites = new ArrayList<Displayable>();
	private static List<Displayable> screen = new ArrayList<Displayable>();
	private static List<Displayable> overlay = new ArrayList<Displayable>();
	
	private JFrame frame;
	private volatile BufferedImage buffer;
	private FPSController fpsController;
	public static volatile boolean[] changingLayer = new boolean[5];
	
	public GUI(FPSController fpscon)
	{
		fpsController = fpscon;
		buffer = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
		frame = new JFrame("ADF");
		frame.setSize(800, 600);
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
		// add key and mouse listeners
		
		frame.validate();
		fpsController.start(this);
	}
	
	public void updateVisible()
	{
		if(changingLayer[0])
		{
			background.fade();
			changingLayer[0] = background.changing();
		}
		else if(changingLayer[1])
		{
			changingLayer[1] = battle.stream().map(Displayable::changing).anyMatch(Boolean::booleanValue);
		}
		else if(changingLayer[2])
		{
			sprites.forEach(d -> {
				d.move();
				d.fade();
			});
			changingLayer[2] = sprites.stream().map(Displayable::changing).anyMatch(Boolean::booleanValue);
		}
		else if(changingLayer[3])
		{
			changingLayer[3] = screen.stream().map(Displayable::changing).anyMatch(Boolean::booleanValue);
		}
		else if(changingLayer[4])
		{
			changingLayer[4] = overlay.stream().map(Displayable::changing).anyMatch(Boolean::booleanValue);
		}
	}
	
	public void drawToImageBuffer()
	{
		Graphics2D g = buffer.createGraphics();
		background.paint(g);
		if(!changingLayer[0])
		{
			battle.forEach(displayable -> displayable.paint(g));
			sprites.forEach(displayable -> displayable.paint(g));
			screen.forEach(displayable -> displayable.paint(g));
			overlay.forEach(displayable -> displayable.paint(g));
		}
		g.dispose();
	}
	
	public void repaint()
	{
		frame.repaint();
	}
	
	static
	{
		music.start();
		sound.start();
		voice.start();
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
	
	public static void showImage(Image img, Point3D position, String... transitions)
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
			for(int i = 0; i < transitions.length - 1; i++)
			{
				if(transitions[i].trim().equals("ease"))
				{
					ease = Double.parseDouble(transitions[i + 1]);
					break;
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
			sprites.add(new Displayable(img, position, transitions));
		}
	}
	
	public static void hideImage(Image img, String transition)
	{
		// TODO
	}
	
	public static void playAudio(String type, String file, boolean loop, boolean fade, double fadeDuration)
	{
		IMediaReader reader = ToolFactory.makeReader(file);
		IMediaViewer viewer = ToolFactory.makeViewer(IMediaViewer.Mode.AUDIO_ONLY);
		if(type.equals("music"))
		{
			music.playAudio(reader, loop, musicVolume, fade ? new FadeInMediaTool(fadeDuration) : null, viewer);
		}
		else if(type.equals("sound"))
		{
			sound.playAudio(reader, loop, soundVolume, fade ? new FadeInMediaTool(fadeDuration) : null, viewer);
		}
		else if(type.equals("voice"))
		{
			voice.playAudio(reader, loop, voiceVolume, fade ? new FadeInMediaTool(fadeDuration) : null, viewer);
		}
		else
		{
			System.err.println("Error: Unknown audio channel");
		}
	}
	
	public static void playAudio(String type, File file, boolean loop, boolean fade, double fadeDuration)
	{
		playAudio(type, file.getPath(), loop, fade, fadeDuration);
	}
	
	public static void stopAudio(String type, boolean fade, double fadeDuration)
	{
		if(type.equals("music"))
		{
			music.stopAudio(fade ? new FadeOutMediaTool(fadeDuration) : null);
		}
		else if(type.equals("sound"))
		{
			sound.stopAudio(fade ? new FadeOutMediaTool(fadeDuration) : null);
		}
		else if(type.equals("voice"))
		{
			voice.stopAudio(fade ? new FadeOutMediaTool(fadeDuration) : null);
		}
		else
		{
			System.err.println("Error: Unknown audio channel");
		}
	}
	
	public static void playVideo(String file)
	{
		IMediaReader reader = ToolFactory.makeReader(file);
		reader.addListener(ToolFactory.makeViewer());
		while(reader.readPacket() == null)
			continue;
	}
	
	public static void kill()
	{
		AudioThread.kill = true;
	}
	
	private static class AudioThread extends Thread
	{
		private final String name;
		public static volatile boolean kill = false;
		private IMediaReader reader;
		private volatile boolean loop = false;
		private final Object lock = new Object();
		
		public AudioThread(String name)
		{
			this.name = name;
		}
		
		public void playAudio(IMediaReader newReader, boolean loop, IMediaListener... listeners)
		{
			this.loop = loop;
			if(listeners[1] != null)
			{
				((AMediaToolMixin) listeners[0]).addListener(listeners[1]);
				((AMediaToolMixin) listeners[1]).addListener(listeners[2]);
			}
			else
			{
				((AMediaToolMixin) listeners[0]).addListener(listeners[2]);
			}
			
			newReader.addListener(listeners[0]);
			synchronized(lock)
			{
				reader = newReader;
			}
		}
		
		public synchronized void stopAudio(FadeOutMediaTool fadeOut)
		{
			if(reader != null && fadeOut != null)
			{
				reader.addListener(fadeOut);
				while(!fadeOut.isDone)
					continue;
			}
			reader = null;
		}
		
		public void run()
		{
			while(!kill)
			{
				synchronized(lock)
				{
					if(reader != null && reader.readPacket() != null)
					{
						if(loop)
							GUI.playAudio(name, reader.getUrl(), loop, false, 0.0D);
						else
							reader = null;
					}
				}
			}
		}
	}
}