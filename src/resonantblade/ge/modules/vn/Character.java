package resonantblade.ge.modules.vn;

import java.awt.Color;

public class Character
{
	public String name;
	public Color color;
	
	public Character(String name, int color)
	{
		this.name = name;
		this.color = new Color(color);
	}
}