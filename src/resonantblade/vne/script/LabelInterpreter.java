package resonantblade.vne.script;

import static resonantblade.vne.script.ScriptInterpreter.audio;
import static resonantblade.vne.script.ScriptInterpreter.characters;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.Stack;

import javafx.geometry.Point3D;
import resonantblade.vne.audio.AudioSystem;
import resonantblade.vne.gui.GUI;
import resonantblade.vne.script.ScriptInterpreter.Character;
import resonantblade.vne.script.ScriptInterpreter.Label;
import resonantblade.vne.script.ScriptUtils.Quote;

public class LabelInterpreter
{
	public static final LabelInterpreter INSTANCE = new LabelInterpreter();
	
	protected static void interpretLabel(Label label)
	{
		while(INSTANCE.interpretLabel(new LabelIterator(label), false))
			continue;
		System.out.println("Game has exited");
	}
	
	protected static boolean interpretLabelSingle(Label label)
	{
		return INSTANCE.interpretLabel(new LabelIterator(label), true);
	}
	
	@SuppressWarnings("unchecked")
	private boolean interpretLabel(LabelIterator script, boolean single)
	{
		Stack<Object> labelStack = new Stack<Object>();
		
		boolean restart = true;
		outer: while(true)
		{
			Stack<Integer> scope = new Stack<Integer>();
			int curIndent = 0;
			
			while(!script.hasMoreContent())
			{
				if(labelStack.isEmpty())
				{
					if(single)
						return true;
					
					System.err.println("LabelStack was empty. Falling back to start");
					script = new LabelIterator(ScriptInterpreter.script.get("start"));
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
				
				line = cleanComments(line).trim();
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
						Character charr = characters.get(name);
						GUI.say(charr, text.quoteText);
					}
					break;
				case "jump":
					labelStack.push(script);
					labelStack.push(scope);
					labelStack.push(curIndent);
					
					script = new LabelIterator(ScriptInterpreter.script.get(line), script.isCallStatement());
					break;
				case "call":
					labelStack.push(script);
					labelStack.push(scope);
					labelStack.push(curIndent);
					
					script = new LabelIterator(ScriptInterpreter.script.get(line), true);
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
						File audioFile = audio.get(file);
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
					GUI.changeScene(ScriptInterpreter.images.get(name, Arrays.asList(tags)), transitions);
					break;
				case "show":
					double x = Double.NaN;
					double y = Double.NaN;
					double z = Double.NaN;
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
					GUI.showImage(ScriptInterpreter.images.get(name, Arrays.asList(tags)), new Point3D(x, y, z), transitions);
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
					GUI.hideImage(ScriptInterpreter.images.get(name, Arrays.asList(tags)), transitions);
					break;
				case "quit":
					if(!Boolean.parseBoolean(line))
						restart = false;
					break outer;
				// TODO variables, menu, screen
				default:
					System.err.println("Unknown command: " + start + " " + line);
				}
				while(GUI.updating())
					continue;
			}
			
			if(script.hasNextLabel())
			{
				labelStack.push(script);
				labelStack.push(scope);
				labelStack.push(curIndent);
				
				script = script.nextLabel();
			}
		}
		
		if(!restart)
		{
			AudioSystem.kill();
			GUI.kill();
		}
		
		return restart;
	}
	
	private static String cleanComments(String line)
	{
		int index = 0;
		boolean found = false;
		while(!found && index < line.length())
		{
			while(index < line.length() && line.charAt(index) != '#')
				index++;
			int count = 0;
			for(int i = 0; i < index; i++)
			{
				if(line.charAt(i) == '\"')
					count++;
			}
			if(count % 2 == 0)
				found = true;
		}
		
		return line.substring(0, index);
	}
	
	private static class LabelIterator
	{
		private Label label;
		private int index;
		private boolean jumped;
		private boolean callStatement;
		
		public LabelIterator(Label label, boolean callStatement)
		{
			this.label = label;
			index = -1;
			jumped = false;
			this.callStatement = callStatement;
		}
		
		public LabelIterator(Label label)
		{
			this(label, false);
		}
		
		public boolean hasNextLine()
		{
			return label != null && index < label.data.length - 1;
		}
		
		public String nextLine()
		{
			return label.data[++index];
		}
		
		public String currentLine()
		{
			return index < 0 ? null : label.data[index];
		}
		
		public boolean rollback()
		{
			if(index >= 0)
			{
				index--;
				return true;
			}
			return false;
		}
		
		public boolean hasNextLabel()
		{
			return label != null && label.next != null && !callStatement;
		}
		
		public LabelIterator nextLabel()
		{
			if(callStatement)
				return null;
			
			jumped = true;
			return new LabelIterator(label.next);
		}
		
		public boolean hasMoreContent()
		{
			return hasNextLine() || (hasNextLabel() && !jumped);
		}
		
		public boolean isCallStatement()
		{
			return callStatement;
		}
	}
}