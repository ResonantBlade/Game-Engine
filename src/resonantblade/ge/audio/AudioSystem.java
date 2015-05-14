package resonantblade.ge.audio;

import java.io.File;

import com.xuggle.mediatool.AMediaToolMixin;
import com.xuggle.mediatool.IMediaListener;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaViewer;
import com.xuggle.mediatool.ToolFactory;

public final class AudioSystem
{
	private static AudioThread music = new AudioThread("music");
	private static AudioThread sound = new AudioThread("sound");
	private static AudioThread voice = new AudioThread("voice");
	public static VolumeControlMediaTool musicVolume = new VolumeControlMediaTool();
	public static VolumeControlMediaTool soundVolume = new VolumeControlMediaTool();
	public static VolumeControlMediaTool voiceVolume = new VolumeControlMediaTool();
	
	static
	{
		music.start();
		sound.start();
		voice.start();
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
		/**
		 * This is the kill switch that's used for all audio threads
		 */
		private static volatile boolean kill = false;
		
		private final String name;
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
							AudioSystem.playAudio(name, reader.getUrl(), loop, false, 0.0D);
						else
							reader = null;
					}
				}
			}
		}
	}
}