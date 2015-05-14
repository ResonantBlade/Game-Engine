package resonantblade.ge;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Resources
{
	public static BufferedImage loadImage(File file)
	{
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