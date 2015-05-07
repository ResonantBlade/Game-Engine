package resonantblade.vne.gui.displayable;

import java.awt.Graphics2D;

import resonantblade.vne.gui.FPSController;
import resonantblade.vne.gui.Image;
import javafx.geometry.Point3D;

public abstract class Displayable
{
	protected Image image;
	protected Point3D position;
	protected Point3D deltaPosition = Point3D.ZERO;
	protected int moveAmt = 0;
	protected float alpha = 1.0F;
	protected float deltaAlpha = 0.0F;
	protected int fadeAmt = 0;
	protected String[] transitions;
	
	public Displayable(Image image, Point3D position, float alpha, String... transitions)
	{
		this.image = image;
		this.position = position;
		this.alpha = alpha;
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
		double amt = Math.ceil(seconds * 1000.0D / FPSController.REFRESH_INTERVAL_MS);
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
	
	public abstract void paint(Graphics2D g);
	
	public abstract boolean changing();
}