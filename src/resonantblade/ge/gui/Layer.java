package resonantblade.ge.gui;

import java.awt.Graphics2D;

import resonantblade.ge.modules.Module;

public interface Layer
{
	Module getModule();
	String getName();
	double getPriority();
	void init();
	void paint(Graphics2D graphics);
	void update();
	boolean isUpdating();
	boolean isBlocking();
	void setVisible(boolean visible);
	boolean isVisible();
}