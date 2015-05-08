package resonantblade.vne.modules.vn;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javafx.geometry.Point3D;
import resonantblade.vne.audio.AudioSystem;
import resonantblade.vne.gui.GUI;
import resonantblade.vne.script.Interpreter;
import resonantblade.vne.script.JSInterpreter;
import resonantblade.vne.script.Label;
import resonantblade.vne.script.LabelIterator;
import resonantblade.vne.script.ScriptUtils;
import resonantblade.vne.script.ScriptUtils.Quote;

public class ScriptInterpreter implements Interpreter
{
	private HashMap<String, Label> labels = new HashMap<String, Label>();
	
	@Override
	public List<String> getLabelHeaders()
	{
		return Arrays.asList(new String[]{"script"});
	}
	
	@Override
	public boolean hasLabel(String labelName)
	{
		return labels.containsKey(labelName);
	}
	
	@Override
	public void addLabel(Label label)
	{
		labels.put(label.name, label);
	}
	
	@Override
	public void jump(String labelName)
	{
		interpret(labelName);
	}
	
	@Override
	public void call(String labelName)
	{
		interpret(labelName);
	}
	
	@Override
	public void interpret(String labelName)
	{
		if(hasLabel(labelName))
		{
			boolean restart = interpretLabel(new LabelIterator(labels.get(labelName)));
			System.out.println("Game has exited");
		}
		else
		{
			throw new IllegalStateException(String.format("Label \"%s\" does not exist", labelName));
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean interpretLabel(LabelIterator script)
	{
		Stack<Object> labelStack = new Stack<Object>();
		
		boolean restart = true;
		outer: while(true)
		{
			Stack<Integer> scope = new Stack<Integer>();
			int curIndent = 0;
			
			while(!script.hasNextLine())
			{
				if(labelStack.isEmpty())
				{
					System.err.println("LabelStack was empty. This should jump back to the start");
					script = new LabelIterator(labels.get("start"));
					curIndent = 0;
					scope.clear();
					break;
				}
				curIndent = (Integer) labelStack.pop();
				scope = (Stack<Integer>) labelStack.pop();
				script = (LabelIterator) labelStack.pop();
			}
			
			while(script.hasNextLine())
			{
				String line = script.nextLine();
				if(line.trim().isEmpty() || line.trim().startsWith("#"))
					continue;
				
				int indent = 0;
				while(line.charAt(indent) == ' ')
					indent++;
				
				if(indent < 1)
					throw new IllegalStateException("Scope is not correct");
				
				if(indent > curIndent)
				{
					if(curIndent != 0)
						scope.push(curIndent);
					curIndent = indent;
				}
				else
				{
					while(curIndent > indent)
						curIndent = scope.pop();
					if(curIndent != indent)
						throw new IllegalStateException("ERROR: Indents are misaligned");
				}
				
				line = ScriptUtils.cleanComments(line).trim();
				System.out.println("Now on line: " + line);
				String start = line.substring(0, line.indexOf(' ') == -1 ? line.length() : line.indexOf(' '));
				line = line.substring(start.length()).trim();
				switch(start)
				{
				case "say":
					Quote text = ScriptUtils.nextQuote(line);
					if(text.startIndex == 0)
					{
						String name = text.quoteText;
						line = line.substring(text.endIndex + 1).trim();
						text = ScriptUtils.nextQuote(line);
						if(text != null)
						{
							GUI.say(name, Color.BLACK, text.quoteText);
						}
						else
						{
							GUI.say("", Color.BLACK, name);
						}
					}
					else
					{
						String name = line.substring(0, text.startIndex).trim();
						Character charr = VisualNovelModule.characters.get(name);
						GUI.say(charr, text.quoteText);
					}
					break;
				case "call":
					labelStack.push(script);
					labelStack.push(scope);
					labelStack.push(curIndent);
				case "jump":
					script = new LabelIterator(labels.get(line));
					break;
				case "play":
					String type = line.substring(0, line.indexOf(" "));
					line = line.substring(type.length()).trim();
					text = ScriptUtils.nextQuote(line);
					if(text.startIndex == 0)
					{
						String audioFile = text.quoteText;
						line = line.substring(text.endIndex + 1).trim();
						boolean loop = line.contains("loop");
						boolean fade = false;
						double fadeDuration = 0.0D;
						int fadeAmtIndex = line.indexOf("fade") + 4;
						if(fadeAmtIndex != 3)
						{
							line = line.substring(fadeAmtIndex).trim();
							fadeDuration = Double.parseDouble(line.substring(0, line.indexOf(" ") == -1 ? line.length() : line.indexOf(" ")));
							fade = true;
						}
						AudioSystem.playAudio(type, audioFile, loop, fade, fadeDuration);
					}
					else
					{
						String file = line.substring(0, line.indexOf(" ") == -1 ? line.length() : line.indexOf(" "));
						File audioFile = VisualNovelModule.audio.get(file);
						line = line.substring(file.length()).trim();
						boolean loop = line.contains("loop");
						boolean fade = false;
						double fadeDuration = 0.0D;
						int fadeAmtIndex = line.indexOf("fade") + 4;
						if(fadeAmtIndex != 3)
						{
							line = line.substring(fadeAmtIndex).trim();
							fadeDuration = Double.parseDouble(line.substring(0, line.indexOf(" ") == -1 ? line.length() : line.indexOf(" ")));
							fade = true;
						}
						AudioSystem.playAudio(type, audioFile, loop, fade, fadeDuration);
					}
					break;
				case "stop":
					type = line.substring(0, line.indexOf(" ") == -1 ? line.length() : line.indexOf(" "));
					line = line.substring(type.length()).trim();
					boolean fade = false;
					double fadeDuration = 0.0D;
					int fadeAmtIndex = line.indexOf("fade") + 4;
					if(fadeAmtIndex != 3)
					{
						line = line.substring(fadeAmtIndex).trim();
						fadeDuration = Double.parseDouble(line.substring(0, line.indexOf(" ") == -1 ? line.length() : line.indexOf(" ")));
						fade = true;
					}
					AudioSystem.stopAudio(type, fade, fadeDuration);
					break;
				case "wait":
					int millis = 0;
					if(!line.trim().isEmpty())
					{
						try
						{
							millis = (int) (Double.parseDouble(line) * 1000);
						}
						catch(NumberFormatException e)
						{
							millis = 1000;
						}
					}
					synchronized(GUI.userInteractLock)
					{
						try{GUI.userInteractLock.wait(millis);}catch(Exception e){}
					}
					break;
				case "scene":
					String[] nameAndTags = line.replaceAll(" {2,}", " ").split(" ");
					String name = nameAndTags[0];
					String[] tags = nameAndTags;
					nameAndTags = null;
					int withIndex = 0;
					while(withIndex < tags.length - 1)
					{
						if(tags[withIndex].equals("with"))
							break;
						withIndex++;
					}
					String[] transitions = Arrays.copyOfRange(tags, withIndex + 1, tags.length);
					tags = Arrays.copyOfRange(tags, 0, withIndex == tags.length - 1 ? tags.length : withIndex);
					GUI.changeScene(VisualNovelModule.images.get(name, Arrays.asList(tags)), transitions);
					break;
				case "show":
					double x = Double.NaN;
					double y = Double.NaN;
					double z = Double.NaN;
					float alpha = 1.0F;
					if(line.contains(" xpos "))
					{
						int xIndex = line.indexOf(" xpos ");
						String temp = line.substring(xIndex + 5).trim();
						int end = temp.indexOf(" ");
						if(end == -1)
							end = temp.length();
						x = Double.parseDouble(temp.substring(0, end));
						line = line.substring(0, xIndex).trim() + " " + temp.substring(end, temp.length()).trim();
					}
					if(line.contains(" ypos "))
					{
						int yIndex = line.indexOf(" ypos ");
						String temp = line.substring(yIndex + 5).trim();
						int end = temp.indexOf(" ");
						if(end == -1)
							end = temp.length();
						y = Double.parseDouble(temp.substring(0, end));
						line = line.substring(0, yIndex).trim() + " " + temp.substring(end, temp.length()).trim();
					}
					if(line.contains(" zoom "))
					{
						int zIndex = line.indexOf(" zoom ");
						String temp = line.substring(zIndex + 5).trim();
						int end = temp.indexOf(" ");
						if(end == -1)
							end = temp.length();
						z = Double.parseDouble(temp.substring(0, end));
						line = line.substring(0, zIndex).trim() + " " + temp.substring(end, temp.length()).trim();
					}
					if(line.contains(" alpha "))
					{
						int aIndex = line.indexOf(" alpha ");
						String temp = line.substring(aIndex + 6).trim();
						int end = temp.indexOf(" ");
						if(end == -1)
							end = temp.length();
						alpha = Float.parseFloat(temp.substring(0, end));
						line = line.substring(0, aIndex).trim() + " " + temp.substring(end, temp.length()).trim();
					}
					transitions = new String[0];
					if(line.contains(" with "))
					{
						withIndex = line.indexOf(" with ");
						if(withIndex != -1)
						{
							String temp = line.substring(withIndex + 5).trim();
							line = line.substring(0, withIndex).trim();
							transitions = temp.replaceAll(" {2,}", " ").split(" ");
						}
					}
					
					name = line.substring(0, line.indexOf(" ") != -1 ? line.indexOf(" ") : line.length());
					//line = line.substring(name.length()).trim();
					tags = line.replaceAll(" {2,}", " ").split(" ");
					GUI.showImage(VisualNovelModule.images.get(name, Arrays.asList(tags)), new Point3D(x, y, z), alpha, transitions);
					break;
				case "hide":
					nameAndTags = line.replaceAll(" {2,}", " ").split(" ");
					name = nameAndTags[0];
					tags = nameAndTags;
					nameAndTags = null;
					transitions = new String[0];
					withIndex = 0;
					while(withIndex < tags.length - 1)
					{
						if(tags[withIndex].equals("with"))
							break;
						withIndex++;
					}
					if(tags.length >= 2 && tags[tags.length - 2].equals("with"))
					{
						transitions = Arrays.copyOfRange(tags, withIndex + 1, tags.length);
						tags = Arrays.copyOfRange(tags, 0, withIndex == tags.length - 1 ? tags.length : withIndex);
					}
					GUI.hideImage(VisualNovelModule.images.get(name, Arrays.asList(tags)), transitions);
					break;
				case "quit":
					if(!Boolean.parseBoolean(line))
						restart = false;
					break outer;
				case "layer":
					if(line.startsWith("show"))
					{
						GUI.showLayer(Integer.parseInt(line.substring(4).trim()));
					}
					else if(line.startsWith("hide"))
					{
						GUI.hideLayer(Integer.parseInt(line.substring(4).trim()));
					}
					else
					{
						throw new IllegalStateException("Unknown layer command");
					}
					break;
				case "script":
					int count = 1;
					while(!script.nextLine().trim().startsWith("end_script"))
						count++;
					String[] stra = new String[count - 1];
					for(int i = 0; i < count; i++)
						script.rollback();
					for(int i = 0; i < count - 1; i++)
						stra[i] = script.nextLine();
					script.nextLine();
					JSInterpreter.eval(stra);
					break;
				// TODO variables, menu, screen
				default:
					System.err.println("Unknown command: " + start + " " + line);
				}
				while(GUI.updating())
					continue;
			}
		}
		
		if(!restart)
		{
			AudioSystem.kill();
			GUI.kill();
		}
		
		return restart;
	}
}