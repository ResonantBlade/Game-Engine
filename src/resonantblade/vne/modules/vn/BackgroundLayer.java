package resonantblade.vne.modules.vn;

import java.awt.Graphics2D;

import resonantblade.vne.gui.Layer;
import resonantblade.vne.modules.Module;

public class BackgroundLayer implements Layer
{
	protected volatile SceneBG background = SceneBG.BLANK;
	private boolean visible = true;
	private boolean forced = false;
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
	public void paint(Graphics2D graphics)
	{
		background.paint(graphics);
	}
	
	@Override
	public void update()
	{
		background.fade();
		forced = false;
	}
	
	@Override
	public void forceUpdate()
	{
		forced = true;
	}
	
	@Override
	public boolean isUpdating()
	{
		return forced || background.changing();
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