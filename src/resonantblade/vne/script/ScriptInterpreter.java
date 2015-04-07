package resonantblade.vne.script;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import resonantblade.vne.TaggedImageMap;

public class ScriptInterpreter
{
	protected static HashMap<String, Label> script = new HashMap<String, Label>();
	protected static HashMap<String, Label> battles = new HashMap<String, Label>();
	protected static List<InitLabel> initializers = new ArrayList<InitLabel>();
	
	protected static HashMap<String, Character> characters = new HashMap<String, Character>();
	protected static TaggedImageMap images = new TaggedImageMap();
	protected static HashMap<String, File> audio = new HashMap<String, File>();
	
	public static void load()
	{
		try
		{
			File root = new File(ScriptInterpreter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
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
		}).min().getAsInt();
		if(indent > 0)
			script.stream().map(str -> str.substring(indent)).collect(Collectors.toList());
		
		parseScript(script);
	}
	
	private static void parseScript(List<String> script)
	{
		parseInitializers(script);
		parseDialogue(script);
		parseBattles(script);
	}
	
	private static void parseDialogue(List<String> script)
	{
		int labelStart = 0;
		Label prevLabel = null;
		while(labelStart < script.size())
		{
			while(labelStart < script.size() - 1 && !script.get(labelStart).startsWith("label"))
				labelStart++;
			
			int labelEnd = labelStart + 1;
			while(labelEnd < script.size() && script.get(labelEnd).startsWith(" "))
				labelEnd++;
			
			Label temp = addLabel(script.get(labelStart), script.subList(labelStart + 1, labelEnd));
			if(prevLabel != null && !checkJump(prevLabel.data))
			{
				prevLabel.next = temp;
			}
			prevLabel = temp;
			labelStart = labelEnd;
		}
	}
	
	private static boolean checkJump(String[] data)
	{
		int index = data.length - 1;
		while(index > 0 && data[index].trim().isEmpty())
			index--;
		return data[index].trim().startsWith("jump ");
	}
	
	private static Label addLabel(String name, List<String> labelData)
	{
		name = name.substring(name.indexOf("label") + 5).trim();
		
		String data = labelData.stream().collect(Collectors.joining("\n"));
		Label label = new Label(name, data);
		script.put(name, label);
		return label;
	}
	
	private static void parseBattles(List<String> script)
	{
		int labelStart = 0;
		while(labelStart < script.size())
		{
			while(labelStart < script.size() - 1 && !script.get(labelStart).startsWith("battle"))
				labelStart++;
			
			int labelEnd = labelStart + 1;
			while(labelEnd < script.size() && script.get(labelEnd).startsWith(" "))
				labelEnd++;
			
			addBattle(script.get(labelStart), script.subList(labelStart + 1, labelEnd));
			labelStart = labelEnd;
		}
	}
	
	private static void addBattle(String name, List<String> label)
	{
		name = name.substring(name.indexOf("battle") + 6).trim();
		
		String data = label.stream().collect(Collectors.joining("\n"));
		battles.put(name, new Label(name, data));
	}
	
	private static void parseInitializers(List<String> script)
	{
		int labelStart = 0;
		while(labelStart < script.size())
		{
			while(labelStart < script.size() - 1 && !script.get(labelStart).startsWith("init"))
				labelStart++;
			
			int labelEnd = labelStart + 1;
			while(labelEnd < script.size() && script.get(labelEnd).startsWith(" "))
				labelEnd++;
			
			addInitializer(script.get(labelStart), script.subList(labelStart + 1, labelEnd));
			labelStart = labelEnd;
		}
	}
	
	private static void addInitializer(String name, List<String> label)
	{
		int id = Integer.parseInt(name.substring(name.indexOf("init") + 4).trim());
		
		String data = label.stream().collect(Collectors.joining("\n"));
		initializers.add(new InitLabel(id, data));
	}
	
	public static void init()
	{
		initializers.stream().sorted((il1, il2) -> il1.priority - il2.priority)
		.forEachOrdered(initLabel -> InitInterpreter.interpretInit(initLabel.data));
	}
	
	public static void start()
	{
		boolean cont = true;
		if(script.get("splashscreen") != null)
			cont = LabelInterpreter.interpretLabelSingle(script.get("splashscreen"));
		if(cont)
			LabelInterpreter.interpretLabel(script.get("start"));
	}
	
	protected static class Label
	{
		public final String name;
		public final String[] data;
		public Label next;
		
		public Label(String name, String data)
		{
			this.name = name;
			this.data = data.split("\n");
			next = null;
		}
	}
	
	protected static class InitLabel extends Label
	{
		public final int priority;
		
		public InitLabel(int priority, String data)
		{
			super(Integer.toString(priority), data);
			this.priority = priority;
		}
	}
	
	public static class Character
	{
		public String name;
		public Color color;
		
		public Character(String name, int color)
		{
			this.name = name;
			this.color = new Color(color);
		}
	}
}