package matt.sunrider.gui.audio;

import java.nio.ShortBuffer;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;

public class VolumeControlMediaTool extends MediaToolAdapter
{
	public double volume = 1.0D;
	
	@Override
	public void onAudioSamples(IAudioSamplesEvent event)
	{
		ShortBuffer buffer = event.getAudioSamples().getByteBuffer().asShortBuffer();
		for(int i = 0; i < buffer.limit(); i ++)
		{
			buffer.put((short) (buffer.get(i) * volume));
		}
		
		super.onAudioSamples(event);
	}
}