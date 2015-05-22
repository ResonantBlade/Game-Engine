package resonantblade.ge.modules.vn;

import java.awt.Graphics2D;

import resonantblade.ge.gui.Layer;
import resonantblade.ge.modules.Module;

public class BackgroundLayer implements Layer
{
	protected volatile SceneBG background = SceneBG.BLANK;
	private boolean visible = true;
	private final Module module;
	
	public BackgroundLayer(Module module)
	{
		this.module = module;
	}
	
	@Override
	public Module getModule()
	{
		return module;
	}
	
	@Override
	public String getName()
	{
		return "BackgroundLayer";
	}
	
	@Override
	public double getPriority()
	{
		return 0.0D;
	}
	
	@Override
	public void init()
	{
		
	}
	
	@Override
	public void paint(Graphics2D graphics)
	{
		background.paint(graphics);
	}
	
	@Override
	public void update()
	{
		background.fade();
		background.move();
	}
	
	@Override
	public boolean isUpdating()
	{
		return background.changing();
	}
	
	@Override
	public boolean isBlocking()
	{
		return background.isBlocking();
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