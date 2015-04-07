package resonantblade.vne.script;

import static resonantblade.vne.script.ScriptInterpreter.audio;
import static resonantblade.vne.script.ScriptInterpreter.characters;
import static resonantblade.vne.script.ScriptInterpreter.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Stack;

import resonantblade.vne.Image;
import resonantblade.vne.Properties;
import resonantblade.vne.TextImage;
import resonantblade.vne.script.ScriptInterpreter.Character;
import resonantblade.vne.script.ScriptUtils.Quote;

public class InitInterpreter
{
	protected static void interpretInit(String[] script)
	{
		Stack<Integer> scope = new Stack<Integer>();
		int curIndent = 0;
		
		for(String line : script)
		{
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
			if(line.isEmpty() || line.startsWith("#"))
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
				characters.put(identifier, new Character(charName.quoteText, color));
				break;
			case "Image":
				Quote path = ScriptUtils.nextQuote(line);
				line = line.substring(0, path.startIndex).trim();
				String[] lineData = line.split(" ");
				String name = lineData[0];
				images.put(name, new Image(path.quoteText, Arrays.copyOfRange(lineData, 1, lineData.length)));
				break;
			case "TextImage":
				path = ScriptUtils.nextQuote(line);
				String modifiers = line.substring(path.endIndex + 1).trim();
				line = line.substring(0, path.startIndex).trim();
				lineData = line.split(" ");
				name = lineData[0];
				images.put(name, new TextImage(path.quoteText, modifiers, Arrays.copyOfRange(lineData, 1, lineData.length)));
				break;
			case "Audio":
				lineData = line.split(" ", 2);
				if(lineData.length >= 2)
				{
					if(lineData[1].startsWith("\"") && lineData[1].endsWith("\""))
					{
						audio.put(lineData[0], new File(lineData[1].substring(1, lineData[1].length() - 1)));
					}
					else
					{
						throw new IllegalStateException("Invalid audio file");
					}
				}
				else
				{
					throw new IllegalStateException("Not enough data to define an audio sample");
				}
				break;
			case "title":
				if(line.charAt(0) != '"' || line.charAt(line.length() - 1) != '"')
					throw new IllegalStateException("Invalid title");
				
				line = line.substring(1, line.length() - 1);
				Properties.setGUITitle(line);
				break;
			default:
				throw new IllegalStateException("Unknown data in init: " + start);
			}
		}
	}
}