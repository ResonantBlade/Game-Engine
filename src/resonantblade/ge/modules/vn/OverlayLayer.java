package resonantblade.ge.modules.vn;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.File;

import resonantblade.ge.FontHandler;
import resonantblade.ge.Resources;
import resonantblade.ge.gui.Layer;
import resonantblade.ge.modules.Module;
import resonantblade.ge.script.JSInterpreter;

public class OverlayLayer implements Layer
{
	protected volatile TextDisplayable character = TextDisplayable.BLANK;
	protected volatile TextDisplayable text = TextDisplayable.BLANK;
	private boolean visible = true;
	private final Module module;
	private BufferedImage background;
	private Font font;
	private int size;
	private int xMin;
	private int xMax;
	private int yMin;
	private int yMax;
	private int paddingLeft;
	private int paddingRight;
	private int paddingTop;
	private int paddingBottom;
	private double speed;
	
	public OverlayLayer(Module module)
	{
		this.module = module;
		
		String background = JSInterpreter.eval("style.dialogue_window.background");
		this.background = Resources.loadImage(new File(background));
		
		xMin = JSInterpreter.<Number>eval("style.dialogue_window.xMin").intValue();
		xMax = JSInterpreter.<Number>eval("style.dialogue_window.xMin").intValue();
		yMin = JSInterpreter.<Number>eval("style.dialogue_window.xMin").intValue();
		yMax = JSInterpreter.<Number>eval("style.dialogue_window.xMin").intValue();
		paddingLeft = JSInterpreter.<Number>eval("style.dialogue_window.padding_left").intValue();
		paddingRight = JSInterpreter.<Number>eval("style.dialogue_window.padding_right").intValue();
		paddingTop = JSInterpreter.<Number>eval("style.dialogue_window.padding_top").intValue();
		paddingBottom = JSInterpreter.<Number>eval("style.dialogue_window.padding_bottom").intValue();
		speed = JSInterpreter.<Number>eval("config.default_afm_time").doubleValue();
		
		String font = JSInterpreter.eval("style.default.font");
		size = JSInterpreter.<Number>eval("style.default.size").intValue();
		this.font = FontHandler.getFont(font).deriveFont((float) size);
	}
	
	@Override
	public Module getModule()
	{
		return module;
	}
	
	@Override
	public String getName()
	{
		return "OverlayLayer";
	}
	
	@Override
	public double getPriority()
	{
		return 3.0D;
	}
	
	@Override
	public void paint(Graphics2D graphics)
	{
		graphics.drawImage(background, xMin + paddingLeft, yMin + paddingTop, xMax - paddingRight, yMax - paddingBottom, null);
		character.paint(graphics);
		text.paint(graphics);
	}
	
	public void setText(String name, Color color, String text)
	{
		FontRenderContext frc = new FontRenderContext(null, true, true);
		int height = (int) Math.ceil(font.getLineMetrics(name, frc).getHeight());
		character = new TextDisplayable(xMin + paddingLeft, yMin + paddingTop, xMax - paddingRight, yMin + height, name, color, font, speed);
		character = new TextDisplayable(xMin + paddingLeft, yMin + paddingTop + height, xMax - paddingRight, yMax - paddingBottom, name, color, font, speed);
	}
	
	@Override
	public void update()
	{
		text.move();
	}
	
	@Override
	public boolean isUpdating()
	{
		return text.changing();
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	@Override
	public boolean isVisible()
	{
		return visible;
	}
}