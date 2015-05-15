package resonantblade.ge;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Resources
{
	public static BufferedImage loadImage(File file)
	{
		if(file == null)
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		try
		{
			return ImageIO.read(file);
		}
		catch(Exception e)
		{
			throw new IllegalStateException("Unable to load image: " + file.getPath());
		}
	}
}