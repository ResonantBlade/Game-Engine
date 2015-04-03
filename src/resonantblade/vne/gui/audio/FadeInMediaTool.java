package resonantblade.vne.gui.audio;

import java.nio.ShortBuffer;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;

public class FadeInMediaTool extends MediaToolAdapter
{
	private double volume = 0.0D;
	private double changePerSecond;
	
	public FadeInMediaTool(double seconds)
	{
		changePerSecond = 1.0D / seconds;
	}
	
	@Override
	public void onAudioSamples(IAudioSamplesEvent event)
	{
		int sampleRate = event.getAudioSamples().getSampleRate();
		double volumeIncrement = changePerSecond / sampleRate;
		ShortBuffer buffer = event.getAudioSamples().getByteBuffer().asShortBuffer();
		for(int i = 0; i < buffer.limit() && volume < 1.0D; i++, volume += volumeIncrement)
		{
			buffer.put((short) (buffer.get(i) * volume));
		}
		
		super.onAudioSamples(event);
	}
}