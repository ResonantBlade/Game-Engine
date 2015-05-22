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
	List<? extends Interpreter> getInterpreters();
	List<? extends Layer> getLayers();
	List<? extends EventListener> getListeners();
	void init();
	void start();
}