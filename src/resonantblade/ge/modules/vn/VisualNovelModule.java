package resonantblade.ge.modules.vn;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
	
	protected void changeScene(Image img, Point3D position, String... transitions)
	{
		double fadeDuration = -1.0D;
		double blockTime = -1.0D;
		double ease = 0.0D;
		
		for(int i = 0; i < transitions.length; i++)
		{
			if(transitions[i].equals("fade"))
			{
				fadeDuration = 1.0D;
				if(i < transitions.length - 1)
				{
					try
					{
						fadeDuration = Double.parseDouble(transitions[i + 1]);
						i++;
					}
					catch(NumberFormatException e) {}
				}
			}
			else if(transitions[i].equals("noblock"))
			{
				blockTime = 0.0D;
				if(i < transitions.length - 1)
				{
					try
					{
						blockTime = Double.parseDouble(transitions[i + 1]);
						i++;
					}
					catch(NumberFormatException e) {}
				}
			}
			else if(transitions[i].equals("ease"))
			{
				ease = 1.0D;
				if(i < transitions.length - 1)
				{
					try
					{
						ease = Double.parseDouble(transitions[i + 1]);
						i++;
					}
					catch(NumberFormatException e) {}
				}
			}
		}
		
		if(backgroundLayer.background.image.tagsAreSame(img.getTags()))
		{
			if(Double.isNaN(position.getX()))
				position = new Point3D(backgroundLayer.background.getPosition().getX(), position.getY(), position.getZ());
			if(Double.isNaN(position.getY()))
				position = new Point3D(position.getX(), backgroundLayer.background.getPosition().getY(), position.getZ());
			if(Double.isNaN(position.getZ()))
				position = new Point3D(position.getX(), position.getY(), backgroundLayer.background.getPosition().getZ());
			
			if(ease > 0.0D)
				backgroundLayer.background.moveTo(position, ease);
			else
				backgroundLayer.background.setPosition(position);
			
			backgroundLayer.background.noBlock(blockTime);
		}
		else
		{
			if(Double.isNaN(position.getX()))
				position = new Point3D(0.5D, position.getY(), position.getZ());
			if(Double.isNaN(position.getY()))
				position = new Point3D(position.getX(), 0.5D, position.getZ());
			if(Double.isNaN(position.getZ()))
				position = new Point3D(position.getX(), position.getY(), 1.0D);
			
			backgroundLayer.background = new SceneBG(img, fadeDuration > 0.0D, fadeDuration);
			backgroundLayer.background.setPosition(position);
			backgroundLayer.background.noBlock(blockTime);
			spriteLayer.sprites.clear();
			//((ScreenLayer) layers.get("screen")).screens.clear();
			overlayLayer.character = TextDisplayable.BLANK;
			overlayLayer.text = TextDisplayable.BLANK;
		}
	}
	
	protected void showImage(Image img, Point3D position, float alpha, boolean noBlock, double blockTime, String... transitions)
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
			
			current.noBlock(noBlock ? blockTime : -1.0D);
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
			
			current.noBlock(noBlock ? blockTime : -1.0D);
			
			spriteLayer.sprites.add(current);
		}
	}
	
	protected void hideImage(Image img, double blockTime, String[] transitions)
	{
		Displayable current = spriteLayer.sprites.stream().filter(displayable -> displayable.getImage().tagsAreSame(img.getTags())).findFirst().orElse(null);
		if(current != null)
		{
			current.noBlock(blockTime);
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
	
	protected boolean blocking()
	{
		for(Layer layer : getLayers())
			if(layer.isBlocking() && layer.isVisible())
				return true;
		return false;
	}
}