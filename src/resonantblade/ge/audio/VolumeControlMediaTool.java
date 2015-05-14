package resonantblade.ge.audio;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;

/**
 * This class is used for controlling the volume of each stream.
 * @author Matthew
 *
 */
public class VolumeControlMediaTool extends MediaToolAdapter
{
	public double volume = 1.0D;
	
	@Override
	public void onAudioSamples(IAudioSamplesEvent event)
	{
		VolumeUtils.changeVolume(event, volume);
		super.onAudioSamples(event);
	}
}