package resonantblade.vne.modules.vn;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import resonantblade.vne.FontHandler;
import resonantblade.vne.gui.GUI;
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
		
		String[] texta = text.split("\n");
		int width = 0;
		int height = 0;
		int heightMod = 0;
		Font font = FontHandler.getFont(fontLocStr).deriveFont((float) size);
		FontRenderContext frc = new FontRenderContext(null, true, true);
		TextLayout tl = new TextLayout(text, font, frc);
		height = (int) tl.getBounds().getHeight();
		heightMod = (int) Math.ceil(tl.getAscent() + tl.getDescent());
		for(String str : texta)
		{
			tl = new TextLayout(str, font, frc);
			Rectangle2D bounds = tl.getBounds();
			width = (int) Math.max(width, bounds.getWidth());
		}
		width = (int) Math.min(width * 1.2D, GUI.WIDTH);
		image = new BufferedImage(width, height * texta.length + heightMod, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.WHITE);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		for(int i = 0; i < texta.length; i++)
		{
			g.drawString(texta[i], (int) ((width - fm.stringWidth(texta[i])) * textAlign), (i + 1) * height + fm.getAscent());
		}
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