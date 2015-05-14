package resonantblade.ge.script;

import java.io.Reader;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JSInterpreter
{
	private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	
	public static Object eval(String[] code)
	{
		return eval(toString(code));
	}
	
	public static Object eval(Reader reader)
	{
		try
		{
			return engine.eval(reader);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static Object callFunction(String name, Object... params)
	{
		try
		{
			return ((Invocable) engine).invokeFunction(name, params);
		}
		catch(Exception e)
		{
			throw new IllegalStateException("Failed to find function with name: " + name + " and arguments: " + Arrays.toString(params));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T eval(String data)
	{
		try
		{
			return (T) engine.eval(data);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static String toString(String[] sa)
	{
		return Arrays.stream(sa).collect(Collectors.joining("\n"));
	}
}