package resonantblade.vne.gui;

import java.awt.Graphics2D;

import resonantblade.vne.modules.Module;

public interface Layer
{
	Module getModule();
	String getName();
	double getPriority();
	void paint(Graphics2D graphics);
	void update();
	void forceUpdate();
	boolean isUpdating();
	void setVisible(boolean visible);
	boolean isVisible();
}