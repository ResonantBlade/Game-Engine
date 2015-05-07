package resonantblade.vne.gui;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import resonantblade.vne.gui.displayable.Displayable;
import javafx.geometry.Point3D;

public class ImageDisplayable extends Displayable
{
	public ImageDisplayable(Image image, Point3D position, float alpha, String... transitions)
	{
		super(image, position, alpha, transitions);
	}
	
	@Override
	public void paint(Graphics2D g)
	{
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		Composite prevComposite = g.getComposite();
		g.setComposite(ac);
		
		BufferedImage image = getImage().getImage();
		double zoom = position.getZ();
		
		double newWidth = image.getWidth() * zoom;
		double newHeight = image.getHeight() * zoom;
		double halfWidth = newWidth / 2.0D;
		double halfHeight = newHeight / 2.0D;
		double drawX = position.getX() * GUI.WIDTH - halfWidth;
		double drawY = position.getY() * GUI.HEIGHT - halfHeight;
		
		g.drawImage(image, (int) drawX, (int) drawY, (int) newWidth, (int) newHeight, null);
		g.setComposite(prevComposite);
	}
	
	@Override
	public boolean changing()
	{
		return moveAmt != 0 || fadeAmt != 0;
	}
}