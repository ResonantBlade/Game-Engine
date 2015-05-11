package resonantblade.vne.modules.vn;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import resonantblade.vne.gui.IterationTools;
import resonantblade.vne.gui.Layer;
import resonantblade.vne.modules.Module;

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