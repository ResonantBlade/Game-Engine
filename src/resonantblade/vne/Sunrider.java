package resonantblade.vne;

import java.util.ArrayList;
import java.util.List;

import resonantblade.vne.battle.ships.Battleship;
import resonantblade.vne.gui.FPSController;
import resonantblade.vne.gui.GUI;
import resonantblade.vne.script.ScriptInterpreter;

public class Sunrider
{
	public static final List<Battleship> playerShips = new ArrayList<Battleship>();
	public static final List<Battleship> enemyShips = new ArrayList<Battleship>();
	
	public static void main(String[] args)
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FPSController fpscon = new FPSController();
                GUI gui = new GUI(fpscon);
            }
        });
		
		ScriptInterpreter.load();
		ScriptInterpreter.init();
		ScriptInterpreter.start();
	}
}