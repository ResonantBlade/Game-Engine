package resonantblade.vne;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jdk.nashorn.api.scripting.URLReader;
import resonantblade.vne.battle.ships.Battleship;
import resonantblade.vne.gui.FPSController;
import resonantblade.vne.gui.GUI;
import resonantblade.vne.script.JSInterpreter;
import resonantblade.vne.script.ScriptInterpreter;

public class VisualNovelEngine
{
	public static final List<Battleship> playerShips = new ArrayList<Battleship>();
	public static final List<Battleship> enemyShips = new ArrayList<Battleship>();
	protected volatile static GUI gui;
	
	public static void main(String[] args) throws Exception
	{
		URL url = ClassLoader.getSystemClassLoader().getResource("DefaultOptions.js");
		JSInterpreter.eval(new URLReader(url));
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