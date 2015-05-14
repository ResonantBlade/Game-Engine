package resonantblade.ge.audio;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;

/**
 * This class is used for fading out audio over a period of time.
 * @author Matthew
 *
 */
public class FadeOutMediaTool extends MediaToolAdapter
{
	private double volume = 1.0D;
	private double changePerSecond;
	public volatile boolean isDone = false;
	
	public FadeOutMediaTool(double seconds)
	{
		// calculate the change in volume per second
		changePerSecond = 1.0D / seconds;
	}
	
	@Override
	public void onAudioSamples(IAudioSamplesEvent event)
	{
		int sampleRate = event.getAudioSamples().getSampleRate();
		double volumeDecrement = -changePerSecond / sampleRate;
		volume = VolumeUtils.changeVolume(event, volume, volumeDecrement, 0.0D);
		isDone = volume <= 0.0D;
		
		super.onAudioSamples(event);
	}
}