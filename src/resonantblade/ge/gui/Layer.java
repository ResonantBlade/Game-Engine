package resonantblade.ge.gui;

import java.awt.Graphics2D;

import resonantblade.ge.modules.Module;

public interface Layer
{
	Module getModule();
	String getName();
	double getPriority();
	void paint(Graphics2D graphics);
	void update();
	boolean isUpdating();
	void setVisible(boolean visible);
	boolean isVisible();
}