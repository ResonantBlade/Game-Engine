package resonantblade.vne.modules;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

import resonantblade.vne.gui.Layer;

public class ModuleHandler
{
	private static List<Class<?>> loadedClasses = new ArrayList<Class<?>>();
	private static List<Module> loadedModules = new ArrayList<Module>();
	private static URLClassLoader cl;
	private static final Object lock = new Object();
	public static volatile List<Layer> layers = new ArrayList<Layer>();
	
	public static void loadModules()
	{
		File moduleFolder = new File("Modules");
		if(!moduleFolder.exists())
			moduleFolder.mkdir();
		List<JarFile> modules = new ArrayList<JarFile>();
		LinkedList<File> toProcess = new LinkedList<File>();
		toProcess.add(moduleFolder);
		List<URL> urls = new LinkedList<URL>();
		while(toProcess.size() > 0)
		{
			File folder = toProcess.remove();
			for(File f : folder.listFiles())
			{
				if(f.isDirectory())
				{
					toProcess.add(f);
				}
				else if(f.getName().endsWith(".jar"))
				{
					try
					{
						modules.add(new JarFile(f, false));
						urls.add(f.toURI().toURL());
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		synchronized(lock)
		{
			cl = URLClassLoader.newInstance(urls.toArray(new URL[0]));
			for(JarFile moduleFile : modules)
			{
				moduleFile.stream().filter(f -> !f.isDirectory() && f.getName().endsWith(".class"))
				.map(f -> f.getName().replace('/', '.'))
				.forEach(c -> {
					try
					{
						Class<?> clazz = Class.forName(c, true, cl);
						loadedClasses.add(clazz);
						if(Module.class.isAssignableFrom(clazz))
						{
							Module module = (Module) clazz.newInstance();
							loadedModules.add(module);
							layers.addAll(module.getLayers());
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				});
			}
		}
		
		layers.sort((l1, l2) -> Double.compare(l1.getPriority(), l2.getPriority()));
	}
	
	public static void registerModule(Module module)
	{
		loadedModules.add(module);
	}
	
	public static List<Module> getModules()
	{
		return Collections.unmodifiableList(loadedModules);
	}
}