package resonantblade.ge;

import java.net.URL;

import jdk.nashorn.api.scripting.URLReader;
import resonantblade.ge.gui.FPSController;
import resonantblade.ge.gui.GUI;
import resonantblade.ge.modules.ModuleHandler;
import resonantblade.ge.modules.vn.VisualNovelModule;
import resonantblade.ge.script.JSInterpreter;
import resonantblade.ge.script.LabelLoader;

public class VisualNovelEngine
{
	protected volatile static GUI gui;
	
	public static void main(String[] args) throws Exception
	{
		URL url = ClassLoader.getSystemClassLoader().getResource("DefaultOptions.js");
		JSInterpreter.eval(new URLReader(url));
		ModuleHandler.loadModules();
		ModuleHandler.registerModules(new VisualNovelModule());
		LabelLoader.load();
		ModuleHandler.init();
		
		java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FPSController fpscon = new FPSController();
                gui = new GUI((String) JSInterpreter.eval("config.window_title"), fpscon);
            }
        });
		
		ModuleHandler.start();
	}
}