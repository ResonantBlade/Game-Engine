package resonantblade.vne.modules.vn;

import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import resonantblade.vne.gui.Layer;
import resonantblade.vne.gui.TaggedImageMap;
import resonantblade.vne.modules.Module;
import resonantblade.vne.script.Interpreter;

public class VisualNovelModule implements Module
{
	private InitInterpreter initInterpreter = new InitInterpreter();
	private ScriptInterpreter scriptInterpreter = new ScriptInterpreter();
	private List<EventListener> listeners = new ArrayList<EventListener>();
	
	protected static HashMap<String, Character> characters = new HashMap<String, Character>();
	protected static TaggedImageMap images = new TaggedImageMap();
	protected static HashMap<String, File> audio = new HashMap<String, File>();
	
	public VisualNovelModule()
	{
		listeners.add(new UserInputListener());
	}
	
	@Override
	public String getName()
	{
		return "Visual Novel Module";
	}
	
	@Override
	public List<Interpreter> getInterpreters()
	{
		List<Interpreter> interpreters = new LinkedList<Interpreter>();
		interpreters.add(initInterpreter);
		interpreters.add(scriptInterpreter);
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
		return listeners;
	}
	
	@Override
	public void init()
	{
		initInterpreter.interpret(null);
	}
	
	@Override
	public void start()
	{
		scriptInterpreter.interpret("start");
	}
}