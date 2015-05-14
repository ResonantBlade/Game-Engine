package resonantblade.ge.audio;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;

/**
 * This class is used for fading in an audio file over a certain period of time.
 * @author Matthew
 *
 */
public class FadeInMediaTool extends MediaToolAdapter
{
	private double volume = 0.0D;
	private double changePerSecond;
	
	public FadeInMediaTool(double seconds)
	{
		// calculate the change in volume per second;
		changePerSecond = 1.0D / seconds;
	}
	
	@Override
	public void onAudioSamples(IAudioSamplesEvent event)
	{
		int sampleRate = event.getAudioSamples().getSampleRate();
		double volumeIncrement = changePerSecond / sampleRate;
		
		VolumeUtils.changeVolume(event, volume, volumeIncrement, 1.0D);
		
		super.onAudioSamples(event);
	}
}