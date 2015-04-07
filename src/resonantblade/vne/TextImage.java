package resonantblade.vne;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import resonantblade.vne.script.ScriptUtils;
import resonantblade.vne.script.ScriptUtils.Quote;

public class TextImage extends Image
{
	public TextImage(String text, String modifiers, String... tags)
	{
		super((String) null, tags);
		text = text.replace("{p}", "\n");
		String fontLocStr = "";
		int size = 12;
		double textAlign = 0.5D;
		if(modifiers.contains("font"))
		{
			int fontIndex = modifiers.indexOf("font");
			Quote fontLoc = ScriptUtils.nextQuote(modifiers, fontIndex + 4);
			fontLocStr = fontLoc.quoteText;
			modifiers = modifiers.substring(0, fontIndex).trim() + " " + modifiers.substring(fontLoc.endIndex + 1).trim();
		}
		String[] modifierArray = modifiers.split(" ");
		for(int i = 0; i < modifierArray.length - 1; i++)
		{
			if(modifierArray[i].equals("size"))
			{
				size = Integer.parseInt(modifierArray[i++ + 1]);
			}
			else if(modifierArray[i].equals("text_align"))
			{
				textAlign = Double.parseDouble(modifierArray[i++ + 1]);
			}
		}
		
		Font font = FontHandler.getFont(fontLocStr).deriveFont((float) size);
		FontRenderContext frc = new FontRenderContext(null, true, true);
		Rectangle2D bounds = font.getStringBounds(text, frc);
		image = new BufferedImage((int) bounds.getWidth(), (int) bounds.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.drawString(text, 0, 0);
	}
	
	@Override
	public String getUrl()
	{
		return null;
	}
	
	@Override
	public BufferedImage getImage()
	{
		return image;
	}
}