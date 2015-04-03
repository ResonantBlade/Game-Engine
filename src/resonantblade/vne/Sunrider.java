package matt.sunrider;

import java.util.ArrayList;
import java.util.List;

import matt.sunrider.battle.ships.Battleship;
import matt.sunrider.gui.FPSController;
import matt.sunrider.gui.GUI;
import matt.sunrider.script.ScriptInterpreter;

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