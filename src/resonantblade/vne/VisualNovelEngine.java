package resonantblade.vne;

import java.net.URL;

import jdk.nashorn.api.scripting.URLReader;
import resonantblade.vne.gui.FPSController;
import resonantblade.vne.gui.GUI;
import resonantblade.vne.modules.ModuleHandler;
import resonantblade.vne.script.JSInterpreter;
import resonantblade.vne.script.ScriptInterpreter;

public class VisualNovelEngine
{
	protected volatile static GUI gui;
	
	public static void main(String[] args) throws Exception
	{
		URL url = ClassLoader.getSystemClassLoader().getResource("DefaultOptions.js");
		JSInterpreter.eval(new URLReader(url));
		ModuleHandler.loadModules();
		ScriptInterpreter.load();
		ScriptInterpreter.init();
		
		java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FPSController fpscon = new FPSController();
                gui = new GUI((String) JSInterpreter.eval("config.window_title"), fpscon);
            }
        });
		
		ScriptInterpreter.start();
	}
}