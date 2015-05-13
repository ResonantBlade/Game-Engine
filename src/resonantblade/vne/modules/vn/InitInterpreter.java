package resonantblade.vne.modules.vn;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import resonantblade.vne.script.Interpreter;
import resonantblade.vne.script.JSInterpreter;
import resonantblade.vne.script.Label;
import resonantblade.vne.script.ScriptUtils;
import resonantblade.vne.script.ScriptUtils.Quote;

public class InitInterpreter implements Interpreter
{
	private List<InitLabel> labels = new ArrayList<InitLabel>();
	private VisualNovelModule core;
	
	public InitInterpreter(VisualNovelModule core)
	{
		this.core = core;
	}
	
	@Override
	public List<String> getLabelHeaders()
	{
		return Arrays.asList(new String[]{"init"});
	}
	
	@Override
	public boolean hasLabel(String labelName)
	{
		return labels.stream().map(label -> label.name).anyMatch(name -> name.equals(labelName));
	}
	
	@Override
	public void addLabel(Label label)
	{
		labels.add(new InitLabel(label));
	}
	
	@Override
	public void jump(String labelName)
	{
		throw new UnsupportedOperationException("InitInterpreter does not support label jumping");
	}
	
	@Override
	public void call(String labelName)
	{
		throw new UnsupportedOperationException("InitInterpreter does not support label calling");
	}
	
	@Override
	public void interpret(String labelName)
	{
		labels.stream().sorted((l1, l2) -> Double.compare(l1.priority, l2.priority)).forEachOrdered(label -> interpretInit(label.data));
	}
	
	private void interpretInit(String[] script)
	{
		Stack<Integer> scope = new Stack<Integer>();
		int curIndent = 0;
		
		for(int i = 0; i < script.length; i++)
		{
			String line = script[i];
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
			}
			
			line = ScriptUtils.cleanComments(line).trim();
			if(line.isEmpty())
				continue;
			
			String start = line.substring(0, line.indexOf(' ') == -1 ? line.length() : line.indexOf(' '));
			line = line.substring(start.length()).trim();
			switch(start)
			{
			case "Character":
				Quote charName = ScriptUtils.nextQuote(line);
				String identifier = line.substring(0, charName.startIndex).trim();
				if(identifier.contains(" "))
					throw new IllegalStateException("Name identifiers cannot contain spaces");
				
				int color = 0;
				if(charName.endIndex < line.length())
					color = Integer.parseInt(line.substring(charName.endIndex + 1), 16);
				VisualNovelModule.characters.put(identifier, new Character(charName.quoteText, color));
				break;
			case "Image":
				Quote path = ScriptUtils.nextQuote(line);
				line = line.substring(0, path.startIndex).trim();
				String[] lineData = line.split(" ");
				String name = lineData[0];
				VisualNovelModule.images.put(name, new Image(path.quoteText, lineData));
				break;
			case "TextImage":
				path = ScriptUtils.nextQuote(line);
				String modifiers = line.substring(path.endIndex + 1).trim();
				line = line.substring(0, path.startIndex).trim();
				lineData = line.split(" ");
				name = lineData[0];
				VisualNovelModule.images.put(name, new TextImage(path.quoteText, modifiers, lineData));
				break;
			case "Audio":
				path = ScriptUtils.nextQuote(line);
				identifier = line.substring(0, path.startIndex).trim();
				if(identifier.contains(" "))
					throw new IllegalStateException("Audio identifiers cannot contain spaces");
				VisualNovelModule.audio.put(identifier, new File(path.quoteText));
				break;
			case "javascript":
				for(int j = i + 1; j < script.length; j++)
				{
					if(script[j].trim().startsWith("end_javascript"))
					{
						JSInterpreter.eval(Arrays.copyOfRange(script, i + 1, j));
						i = j;
						break;
					}
				}
				break;
			default:
				throw new IllegalStateException("Unknown data in init: " + start);
			}
		}
	}
	
	private static class InitLabel extends Label
	{
		public final double priority;
		
		public InitLabel(String header, String priority, String extraHeaderData, String[] data)
		{
			super(header, priority, extraHeaderData, data);
			this.priority = Double.parseDouble(priority);
		}
		
		public InitLabel(Label other)
		{
			this(other.header, other.name, other.extraHeaderData, other.data);
		}
	}
}