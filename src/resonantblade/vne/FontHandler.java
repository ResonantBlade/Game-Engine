package resonantblade.vne;

import java.awt.Font;
import java.io.File;
import java.util.HashMap;

public class FontHandler
{
	private static HashMap<String, Font> fonts = new HashMap<String, Font>();
	
	public static synchronized Font getFont(String fontLoc)
	{
		if(!fonts.containsKey(fontLoc))
			registerFont(fontLoc);
		return fonts.get(fontLoc);
	}
	
	private static void registerFont(String fontLoc)
	{
		try
		{
			fonts.put(fontLoc, Font.createFont(Font.TRUETYPE_FONT, new File(fontLoc)));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}
}