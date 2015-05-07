package resonantblade.vne.modules.vn;

import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

import resonantblade.vne.gui.Layer;
import resonantblade.vne.modules.Module;
import resonantblade.vne.script.Interpreter;

public class VisualNovelModule implements Module
{
	@Override
	public String getName()
	{
		return "Visual Novel Module";
	}
	
	@Override
	public List<Interpreter> getInterpreters()
	{
		List<Interpreter> interpreters = new LinkedList<Interpreter>();
		
		return interpreters;
	}
	
	@Override
	public List<Layer> getLayers()
	{
		return null;
	}
	
	@Override
	public List<EventListener> getListeners()
	{
		List<EventListener> listeners = new LinkedList<EventListener>();
		listeners.add(new UserInputListener());
		return listeners;
	}
}