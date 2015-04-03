package matt.sunrider.gui;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javafx.geometry.Point3D;
import matt.sunrider.Image;

public class Displayable
{
	protected Image image;
	protected Point3D position;
	protected Point3D deltaPosition = Point3D.ZERO;
	protected int moveAmt = 0;
	protected float alpha = 1.0F;
	protected float deltaAlpha = 0.0F;
	protected int fadeAmt = 0;
	protected String[] transitions;
	
	public Displayable(Image image, Point3D position, String... transitions)
	{
		this.image = image;
		this.position = position;
		this.transitions = transitions;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public Point3D getPosition()
	{
		return position;
	}
	
	public String[] getTransitions()
	{
		return transitions;
	}
	
	public void setImage(Image img)
	{
		image = img;
	}
	
	public void setPosition(Point3D pos)
	{
		position = pos;
	}
	
	public void setTransitions(String[] transitions)
	{
		this.transitions = transitions;
	}
	
	public void moveTo(Point3D newPosition, double seconds)
	{
		Point3D delta = newPosition.subtract(position);
		double amt = Math.ceil(seconds * 100.0D / FPSController.REFRESH_INTERVAL_MS);
		deltaPosition = delta.multiply(1.0D / amt);
		this.moveAmt = (int) amt;
	}
	
	public void move()
	{
		if(moveAmt == 0)
			return;
		
		position = position.add(deltaPosition);
		moveAmt--;
	}
	
	public void fade(double seconds, boolean fadeIn)
	{
		double amt = Math.ceil(seconds * 1000.0D / FPSController.REFRESH_INTERVAL_MS);
		alpha = fadeIn ? 0.0F : 1.0F;
		deltaAlpha = (float) ((fadeIn ? 1.0D : -1.0D) / amt);
		fadeAmt = (int) amt;
	}
	
	public void fade()
	{
		if(fadeAmt == 0)
			return;
		
		alpha += deltaAlpha;
		fadeAmt--;
		if(alpha < 0.0F)
			alpha = 0.0F;
		if(alpha > 1.0F)
			alpha = 1.0F;
	}
	
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
	
	public boolean changing()
	{
		return moveAmt != 0 || fadeAmt != 0;
	}
}