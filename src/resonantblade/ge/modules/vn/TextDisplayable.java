package resonantblade.ge.modules.vn;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.List;

import javafx.geometry.Point3D;

import java.awt.Color;

public class TextDisplayable extends Displayable
{
	public static final TextDisplayable BLANK = new TextDisplayable(0, 0, 1, 1, "", Color.BLACK, Font.getFont("Arial"), 1.0D);
	
	private int width;
	private int height;
	private String text;
	private Color color;
	private Font font;
	private double speed;
	private double charsRendered;
	private BufferedImage bufferedImage;
	
	public TextDisplayable(int x, int y, int width, int height, String text, Color color, Font font, double speed)
	{
		super(null, new Point3D(x, y, 0.0D), 1.0F, new String[0]);
		this.width = width;
		this.height = height;
		this.text = text;
		this.color = color;
		this.font = font;
		this.speed = speed;
		charsRendered = 0.0D;
		
		bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] data = ((DataBufferInt) bufferedImage.getData().getDataBuffer()).getData();
		Arrays.fill(data, 0);
		
		this.image = new Image(bufferedImage);
	}
	
	@Override
	public void move()
	{
		if(charsRendered == text.length())
			return;
		
		charsRendered += speed;
		if(charsRendered > text.length())
			charsRendered = text.length();
	}
	
	@Override
	public void paint(Graphics2D g)
	{
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		List<String> list = StringUtils.wrap(text, fm, width);
		int height = fm.getHeight();
		int count = 0;
		int lastLine = 0;
		int lastCharRead = 0;
		while(count < (int) charsRendered)
		{
			if(count + list.get(lastLine).length() <= (int) charsRendered)
			{
				count += list.get(lastLine).length();
				lastLine++;
				lastCharRead = 0;
				continue;
			}
			
			lastCharRead = (int) charsRendered - count;
			break;
		}
		
		g.setColor(color);
		for(int i = 0; i < lastLine && height * i + height < this.height; i++)
		{
			g.drawString(list.get(i), (int) position.getX(), (int) position.getY() + height * i);
		}
		if(lastLine < list.size() && height * lastLine + height < this.height)
			g.drawString(list.get(lastLine).substring(0, lastCharRead), (int) position.getX(), (int) position.getY() + height * lastLine);
	}
	
	@Override
	public boolean changing()
	{
		return charsRendered < text.length();
	}
}