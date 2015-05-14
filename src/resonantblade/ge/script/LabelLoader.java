package resonantblade.ge.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import resonantblade.ge.modules.Module;
import resonantblade.ge.modules.ModuleHandler;

public class LabelLoader
{
	private static HashMap<String, Interpreter> headerInterpreterMap = new HashMap<String, Interpreter>();
	
	public static void load()
	{
		for(Module module : ModuleHandler.getModules())
		{
			for(Interpreter interpreter : module.getInterpreters())
			{
				for(String header : interpreter.getLabelHeaders())
				{
					Interpreter prev = headerInterpreterMap.getOrDefault(header, null);
					if(prev != null && prev != interpreter)
					{
						throw new IllegalStateException("ERROR: multiple interpreters use the header: " + header);
					}
					headerInterpreterMap.put(header, interpreter);
				}
			}
		}
		
		try
		{
			File root = new File(LabelLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			root = root.getParentFile();
			LinkedList<File> toProcess = new LinkedList<File>();
			toProcess.add(root);
			System.out.println(root);
			while(!toProcess.isEmpty())
			{
				File file = toProcess.remove();
				for(File f : file.listFiles())
					if(f.isDirectory())
						toProcess.add(f);
					else if(f.getName().endsWith(".rvnes"))
						readScriptFile(f);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void readScriptFile(File file)
	{
		List<String> script = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			String line;
			while((line = br.readLine()) != null)
			{
				script.add(line.replace("\t", "    "));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		script.removeIf(str -> str.trim().isEmpty());
		int indent = script.stream().mapToInt(str -> {
			int i = 0;
			while(str.indexOf(' ', i) == i)
				i++;
			return i;
		}).min().orElse(0);
		if(indent > 0)
			script.stream().map(str -> str.substring(indent)).collect(Collectors.toList());
		
		parseScript(script);
	}
	
	private static void parseScript(List<String> script)
	{
		int labelStart = 0;
		while(labelStart < script.size())
		{
			int labelEnd = labelStart + 1;
			while(labelEnd < script.size() && script.get(labelEnd).startsWith(" "))
				labelEnd++;
			
			Label temp = makeLabel(script.get(labelStart), script.subList(labelStart + 1, labelEnd));
			String header = temp.header;
			headerInterpreterMap.get(header).addLabel(temp);
			labelStart = labelEnd;
		}
	}
	
	private static Label makeLabel(String name, List<String> labelData)
	{
		int splitIndex = name.indexOf(" ");
		String header = name.substring(0, splitIndex);
		name = name.substring(splitIndex).trim();
		splitIndex = name.indexOf(" ");
		String extraHeaderData = "";
		if(splitIndex != -1)
		{
			extraHeaderData = name.substring(splitIndex).trim();
			name = name.substring(0, splitIndex);
		}
		
		String[] data = labelData.toArray(new String[labelData.size()]);
		Label label = new Label(header, name, extraHeaderData, data);
		return label;
	}
}