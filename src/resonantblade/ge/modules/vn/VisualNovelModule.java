package resonantblade.ge.modules.vn;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Point3D;
import resonantblade.ge.gui.Layer;
import resonantblade.ge.modules.Module;
import resonantblade.ge.script.Interpreter;

public class VisualNovelModule implements Module
{
	private InitInterpreter initInterpreter;
	private ScriptInterpreter scriptInterpreter;
	private List<EventListener> listeners;
	private List<Layer> layers;
	private BackgroundLayer backgroundLayer;
	private SpriteLayer spriteLayer;
	private OverlayLayer overlayLayer;
	
	protected static HashMap<String, Character> characters = new HashMap<String, Character>();
	protected static TaggedImageMap images = new TaggedImageMap();
	protected static HashMap<String, File> audio = new HashMap<String, File>();
	
	public VisualNovelModule()
	{
		initInterpreter = new InitInterpreter(this);
		scriptInterpreter = new ScriptInterpreter(this);
		
		listeners = new ArrayList<EventListener>();
		listeners.add(new UserInputListener());
		
		layers = new ArrayList<Layer>();
		layers.add(backgroundLayer = new BackgroundLayer(this));
		layers.add(spriteLayer = new SpriteLayer(this));
		//layers.add(new ScreenLayer(this));
		layers.add(overlayLayer = new OverlayLayer(this));
	}
	
	@Override
	public String getName()
	{
		return "Visual Novel Module";
	}
	
	@Override
	public URL getDefaultOptions()
	{
		return ClassLoader.getSystemClassLoader().getResource("resonantblade/ge/modules/vn/DefaultOptions.js");
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
		return layers;
	}
	
	@Override
	public List<EventListener> getListeners()
	{
		return listeners;
	}
	
	@Override
	public void init()
	{
		layers.stream().forEach(layer -> layer.init());
		initInterpreter.interpret(null);
	}
	
	@Override
	public void start()
	{
		scriptInterpreter.interpret("start");
	}
	
	protected void say(String name, Color color, String text)
	{
		overlayLayer.setText(name, color, text);
		// TODO put in overlay layer
		//System.out.println((name.isEmpty() ? "" : name + ": ") + text);
	}
	
	protected void say(Character charr, String text)
	{
		say(charr.name, charr.color, text);
	}
	
	protected void changeScene(Image img, String... transitions)
	{
		String transition = Arrays.stream(transitions).collect(Collectors.joining(" "));
		int fadeIndex = transition.indexOf("fade");
		double fadeDuration = 1.0D;
		if(fadeIndex != -1)
		{
			int amtIndex = fadeIndex + 4;
			String temp = transition.substring(amtIndex).trim();
			if(!temp.isEmpty())
			{
				int spaceIndex = temp.indexOf(' ');
				if(spaceIndex == -1)
					try{fadeDuration = Double.parseDouble(transition);}catch(Exception e){}
				else
					try{fadeDuration = Double.parseDouble(transition.substring(0, spaceIndex));}catch(Exception e){}
			}
		}
		backgroundLayer.background = new SceneBG(img, fadeIndex != -1, fadeDuration);
		spriteLayer.sprites.clear();
		//((ScreenLayer) layers.get("screen")).screens.clear();
		overlayLayer.character = TextDisplayable.BLANK;
	}
	
	protected void showImage(Image img, Point3D position, float alpha, String... transitions)
	{
		Displayable current = spriteLayer.sprites.stream().filter(displayable -> displayable.getImage().tagsAreSame(img.getTags())).findFirst().orElse(null);
		if(current != null)
		{
			current.setImage(img);
			current.setTransitions(transitions);
			if(Double.isNaN(position.getX()))
				position = new Point3D(current.getPosition().getX(), position.getY(), position.getZ());
			if(Double.isNaN(position.getY()))
				position = new Point3D(position.getX(), current.getPosition().getY(), position.getZ());
			if(Double.isNaN(position.getZ()))
				position = new Point3D(position.getX(), position.getY(), current.getPosition().getZ());
			double ease = 0.0D;
			for(int i = 0; i < transitions.length; i++)
			{
				if(transitions[i].equals("ease"))
				{
					if(i != transitions.length - 1)
						ease = Double.parseDouble(transitions[i + 1]);
					else
						ease = 1.0D;
				}
				if(transitions[i].equals("fade"))
				{
					if(i != transitions.length - 1)
						current.fade(Double.parseDouble(transitions[i + 1]), true);
					else
						current.fade(1.0D, true);
				}
			}
			if(ease == 0.0D)
				current.setPosition(position);
			else
				current.moveTo(position, ease);
		}
		else
		{
			if(Double.isNaN(position.getX()))
				position = new Point3D(0.5D, position.getY(), position.getZ());
			if(Double.isNaN(position.getY()))
				position = new Point3D(position.getX(), 0.5D, position.getZ());
			if(Double.isNaN(position.getZ()))
				position = new Point3D(position.getX(), position.getY(), 1.0D);
			current = new ImageDisplayable(img, position, alpha, transitions);
			for(int i = 0; i < transitions.length; i++)
			{
				if(transitions[i].equals("fade"))
				{
					if(i != transitions.length - 1)
						current.fade(Double.parseDouble(transitions[i + 1]), true);
					else
						current.fade(1.0D, true);
				}
			}
			spriteLayer.sprites.add(current);
		}
	}
	
	protected void hideImage(Image img, String[] transitions)
	{
		Displayable current = spriteLayer.sprites.stream().filter(displayable -> displayable.getImage().tagsAreSame(img.getTags())).findFirst().orElse(null);
		if(current != null)
		{
			for(int i = 0; i < transitions.length; i++)
			{
				if(transitions[i].equals("fade"))
				{
					if(i != transitions.length - 1)
						current.fade(Double.parseDouble(transitions[i + 1]), false);
					else
						current.fade(1.0D, false);
				}
			}
		}
	}
	
	protected void showLayer(String name)
	{
		getLayers().stream().filter(layer -> layer.getName().equals(name)).findFirst().ifPresent(layer -> layer.setVisible(true));
	}
	
	protected void hideLayer(String name)
	{
		getLayers().stream().filter(layer -> layer.getName().equals(name)).findFirst().ifPresent(layer -> layer.setVisible(false));
	}
	
	protected boolean updating()
	{
		for(Layer layer : getLayers())
			if(layer.isUpdating() && layer.isVisible())
				return true;
		return false;
	}
}