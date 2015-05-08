package resonantblade.vne.modules;

import java.util.EventListener;
import java.util.List;

import resonantblade.vne.gui.Layer;
import resonantblade.vne.script.Interpreter;

public interface Module
{
	String getName();
	List<Interpreter> getInterpreters();
	List<Layer> getLayers();
	List<EventListener> getListeners();
	void init();
	void start();
}