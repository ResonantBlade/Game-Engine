package resonantblade.vne.script;

import static resonantblade.vne.script.ScriptInterpreter.audio;
import static resonantblade.vne.script.ScriptInterpreter.characters;
import static resonantblade.vne.script.ScriptInterpreter.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Stack;

import resonantblade.vne.Image;
import resonantblade.vne.script.ScriptInterpreter.Character;

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
			
			line = cleanComments(line).trim();
			if(line.isEmpty() || line.startsWith("#"))
				continue;
			
			String start = line.substring(0, line.indexOf(' ') == -1 ? line.length() : line.indexOf(' '));
			switch(start)
			{
			case "Character":
				line = line.substring(9).trim();
				String[] lineData = line.split(" ", 2);
				if(lineData.length >= 2)
				{
					if(lineData[1].startsWith("\"") && lineData[1].endsWith("\""))
					{
						Character charr = new Character(lineData[1].substring(1, lineData[1].length() - 1),
								lineData.length >= 3 ? Integer.parseInt(lineData[2], 16) : 0);
						characters.put(lineData[0], charr);
					}
					else
					{
						throw new IllegalStateException("Invalid character name");
					}
				}
				else
				{
					throw new IllegalStateException("Not enough data to define a character");
				}
				break;
			case "Image":
				line = line.substring(5).trim();
				lineData = line.split(" ", 2);
				if(lineData.length >= 2)
				{
					String name = lineData[0];
					line = lineData[1];
					int fileIndex = line.indexOf('\"');
					if(fileIndex == -1 || line.indexOf('\"', fileIndex + 1) == -1)
					{
						throw new IllegalStateException("No file path exists for image");
					}
					String filePath = line.substring(fileIndex + 1, line.indexOf('\"', fileIndex + 1)).trim();
					line = line.substring(0, fileIndex).trim();
					String[] tags = line.isEmpty() ? new String[0] : line.split(" ");
					images.put(name, new Image(filePath, tags));
				}
				else
				{
					throw new IllegalStateException("Not enough data to define an image");
				}
				break;
			case "TextImage":
				line = line.substring(9).trim();
				lineData = line.split(" ", 2);
				if(lineData.length >= 2)
				{
					String name = lineData[0];
					line = lineData[1];
					int dataIndex = line.indexOf("\"");
					String[] tags = line.isEmpty() ? new String[0] : line.substring(0, dataIndex).trim().split(" ");
					line = line.substring(0, dataIndex).trim();
					images.put(name, new Image(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), tags));
				}
				else
				{
					throw new IllegalStateException("Not enough data to define an image");
				}
				break;
			case "Audio":
				line = line.substring(5).trim();
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
			default:
				throw new IllegalStateException("Unknown data in init: " + start);
			}
		}
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
}