package resonantblade.vne.battle;

import java.util.ArrayList;
import java.util.List;

import resonantblade.vne.battle.ships.Battleship;

public class BattleManager
{
	public static final BattleManager BM = new BattleManager();
	
	public int cmd = 0;
	public int money = 0;
	public boolean seenSkirmish = false;
	public List<Battleship> ships = new ArrayList<Battleship>();
	//private List<Cover> covers = new ArrayList<Cover>();
	
	public BattleManager()
	{
		
	}
}