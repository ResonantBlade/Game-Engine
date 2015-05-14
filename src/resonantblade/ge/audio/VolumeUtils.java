package resonantblade.ge.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.xuggle.mediatool.event.IAudioSamplesEvent;

public class VolumeUtils
{
	public static double changeVolume(IAudioSamplesEvent event, double volume, double changeInVolume, double limit)
	{
		ByteBuffer buffer = event.getAudioSamples().getByteBuffer();
		long depth = event.getMediaData().getSampleBitDepth();
		if(depth <= 8)
			return changeVolumeByte(buffer, volume, changeInVolume, limit);
		if(depth <= 16)
			return changeVolumeShort(buffer.asShortBuffer(), volume, changeInVolume, limit);
		if(depth <= 32)
			return changeVolumeInt(buffer.asIntBuffer(), volume, changeInVolume, limit);
		throw new IllegalStateException("Unable to play audio with a bit depth of " + depth);
	}
	
	public static void changeVolume(IAudioSamplesEvent event, double volume)
	{
		changeVolume(event, volume, 0.0D, Double.POSITIVE_INFINITY);
	}
	
	private static double changeVolumeByte(ByteBuffer buffer, double volume, double changeInVolume, double limit)
	{
		// multiply the audio data by the volume
		for(int i = 0; i < buffer.limit() && (changeInVolume < 0.0D ? volume > limit : volume < limit); i++, volume += changeInVolume)
		{
			buffer.put((byte) (buffer.get(i) * volume));
		}
		return volume;
	}
	
	private static double changeVolumeShort(ShortBuffer buffer, double volume, double changeInVolume, double limit)
	{
		// multiply the audio data by the volume
		for(int i = 0; i < buffer.limit() && (changeInVolume < 0.0D ? volume > limit : volume < limit); i++, volume += changeInVolume)
		{
			buffer.put((short) (buffer.get(i) * volume));
		}
		return volume;
	}
	
	private static double changeVolumeInt(IntBuffer buffer, double volume, double changeInVolume, double limit)
	{
		// multiply the audio data by the volume
		for(int i = 0; i < buffer.limit() && (changeInVolume < 0.0D ? volume > limit : volume < limit); i++, volume += changeInVolume)
		{
			buffer.put((int) (buffer.get(i) * volume));
		}
		return volume;
	}
}