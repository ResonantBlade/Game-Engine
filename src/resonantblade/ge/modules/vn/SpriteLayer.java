package resonantblade.ge.modules.vn;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import resonantblade.ge.gui.IterationTools;
import resonantblade.ge.gui.Layer;
import resonantblade.ge.modules.Module;

public class SpriteLayer implements Layer
{
	protected volatile List<Displayable> sprites = new ArrayList<Displayable>();
	private boolean visible = true;
	private final Module module;
	
	public SpriteLayer(Module module)
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
		return "SpriteLayer";
	}
	
	@Override
	public double getPriority()
	{
		return 1.0D;
	}
	
	@Override
	public void init()
	{
		
	}
	
	@Override
	public void paint(Graphics2D graphics)
	{
		IterationTools.forEach(sprites, displayable -> displayable.paint(graphics));
	}
	
	@Override
	public void update()
	{
		sprites.forEach(d -> {
			d.move();
			d.fade();
		});
	}
	
	@Override
	public boolean isUpdating()
	{
		return sprites.stream().anyMatch(Displayable::changing);
	}
	
	@Override
	public boolean isBlocking()
	{
		return sprites.stream().anyMatch(Displayable::isBlocking);
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