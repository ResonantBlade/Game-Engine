package resonantblade.ge.modules.vn;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import resonantblade.ge.gui.GUI;
import resonantblade.ge.gui.Layer;
import resonantblade.ge.modules.ModuleHandler;

public class UserInputListener implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener
{
	@Override
	public void mouseDragged(MouseEvent e)
	{
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		synchronized(GUI.userInteractLock)
		{
			GUI.userInteractLock.notifyAll();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			for(Layer layer : ModuleHandler.layers)
			{
				while(layer.isUpdating())
					layer.update();
			}
			synchronized(GUI.userInteractLock)
			{
				GUI.userInteractLock.notifyAll();
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}
}