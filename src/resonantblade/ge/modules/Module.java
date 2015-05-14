package resonantblade.ge.modules;

import java.net.URL;
import java.util.EventListener;
import java.util.List;

import resonantblade.ge.gui.Layer;
import resonantblade.ge.script.Interpreter;

public interface Module
{
	String getName();
	URL getDefaultOptions();
	List<Interpreter> getInterpreters();
	List<Layer> getLayers();
	List<EventListener> getListeners();
	void init();
	void start();
}