package resonantblade.ge.modules.vn;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import resonantblade.ge.gui.IterationTools;
import resonantblade.ge.gui.Layer;
import resonantblade.ge.modules.Module;

public class OverlayLayer implements Layer
{
	protected volatile List<Displayable> overlays = new ArrayList<Displayable>();
	private boolean visible = true;
	private final Module module;
	
	public OverlayLayer(Module module)
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
		IterationTools.forEach(overlays, displayable -> displayable.paint(graphics));
	}
	
	@Override
	public void update()
	{
		overlays.forEach(d -> {
			d.move();
			d.fade();
		});
	}
	
	@Override
	public boolean isUpdating()
	{
		return overlays.stream().anyMatch(Displayable::changing);
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