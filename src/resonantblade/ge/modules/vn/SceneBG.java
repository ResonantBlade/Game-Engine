package resonantblade.ge.modules.vn;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import resonantblade.ge.gui.GUI;
import javafx.geometry.Point3D;

public class SceneBG extends ImageDisplayable
{
	public static final SceneBG BLANK;
	
	static
	{
		BufferedImage bi = new BufferedImage(GUI.WIDTH, GUI.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		g.dispose();
		BLANK = new SceneBG(new Image(bi), false, 1.0D);
	}
	
	public SceneBG(Image image, boolean fade, double duration)
	{
		super(image, new Point3D(0.5D, 0.5D, 1.0D), 1.0F);
		if(fade)
			fade(duration, true);
	}
	
	@Override
	public void paint(Graphics2D g)
	{
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		Composite prevComposite = g.getComposite();
		g.setComposite(ac);
		
		BufferedImage image = getImage().getImage();
		
		g.drawImage(image, (int) (GUI.WIDTH * position.getX()), (int) (GUI.HEIGHT * position.getY()), null);
		g.setComposite(prevComposite);
	}
}