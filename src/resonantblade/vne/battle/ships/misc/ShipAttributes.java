package matt.sunrider.battle.ships.misc;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import matt.sunrider.battle.ships.Battleship;

public class ShipAttributes
{
	public String name;
	public int maxHp;
	public int hp;
	public int maxEn;
	public int en;
	public int maxArmor;
	public int armor;
	public int armorColor;
	public double shieldGen;
	public double shields;
	public int shieldRange;
	public int shieldColor;
	public int flak;
	public int flakRange;
	public double flakEffectiveness;
	public int morale;
	public int hate;
	public HashMap<String, Integer> fear;
	public double kineticDmg = 1.0D;
	public double kineticAcc = 1.0D;
	public double kineticCost = 1.0D;
	public double missileDmg = 1.0D;
	public int missileEccm = 0;
	public double missileCost = 1.0D;
	public double energyDmg = 1.0D;
	public double energyAcc = 1.0D;
	public double energyCost = 1.0D;
	public double meleeDmg = 1.0D;
	public double meleeAcc = 1.0D;
	public double meleeCost = 1.0D;
	public double moveCostModifier = 1.0D;
	public HashMap<String, Upgrade> upgrades;
	public int totalDamageDone = 0;
	public int totalKineticDamageDone = 0;
	public int totalMissileDamageDone = 0;
	public int totalEnergyDamageDone = 0;
	public List<Weapon> weapons = new ArrayList<Weapon>();
	public int maxMissiles;
	public int missiles;
	public int maxRockets;
	public int rockets;
	public int moveCost;
	public int cmdReward;
	public int moneyReward;
	public boolean boss;
	public boolean mercenary;
	public List<Battleship> spawns = new ArrayList<Battleship>();
	public boolean support;
	public Point location;
	public HashMap<String, Modifier> modifiers;
	public Faction faction;
	public List<Faction> allies = new ArrayList<Faction>();
	
	protected ShipAttributes(String name, int maxHp, int maxEn, int maxArmor, double shieldGen,
			int shieldRange, int flak, int flakRange, double flakEffectiveness, int morale,
			int hate, List<Weapon> weapons, int maxMissiles, int maxRockets, int moveCost,
			int cmdReward, int moneyReward, boolean boss, boolean mercenary,
			List<Battleship> spawns, Point location, Faction faction)
	{
		this.name = name;
		this.hp = this.maxHp = maxHp;
		this.en = this.maxEn = maxEn;
		this.armor = this.maxArmor = maxArmor;
		this.armorColor = 0;
		this.shields = this.shieldGen = shieldGen;
		this.shieldRange = shieldRange;
		this.shieldColor = 0;
		this.flak = flak;
		this.flakRange = flakRange;
		this.flakEffectiveness = flakEffectiveness;
		this.morale = morale;
		this.hate = hate;
		this.fear = new HashMap<String, Integer>();
		fear.put("kinetic", 20);
		fear.put("missile", 20);
		fear.put("energy", 20);
		this.upgrades = new HashMap<String, Upgrade>();
		upgrades.put("hp", new Upgrade("Hull Plating", 100, 100, 1.5D));
		upgrades.put("en", new Upgrade("Energy Reactor", 5, 200, 1.4D));
		upgrades.put("moveCost", new Upgrade("Move Cost", -0.05D, 100, 2.5D));
		upgrades.put("evasion", new Upgrade("Evasion", 5, 500, 2.5D));
		upgrades.put("kineticDmg", new Upgrade("Kinetic Damage", 0.05D, 105, 1.55D));
		upgrades.put("kineticAcc", new Upgrade("Kinetic Accuracy", 0.05D, 100, 1.5D));
		upgrades.put("kineticCost", new Upgrade("Kinetic Energy Cost", -0.05D, 100, 2.0D));
		upgrades.put("energyDmg", new Upgrade("Energy Damage", 0.075D, 100, 1.3D));
		upgrades.put("energyAcc", new Upgrade("Energy Accuracy", 0.05D, 120, 1.5D));
		upgrades.put("energyCost", new Upgrade("Energy Energy Cost", -0.05D, 100, 1.8D));
		upgrades.put("missileDmg", new Upgrade("Missile Damage", 0.1D, 100, 1.5D));
		upgrades.put("missileEccm", new Upgrade("Missile Flak Resistance", 1, 100, 1.5D));
		upgrades.put("missileCost", new Upgrade("Missile Energy Cost", -0.5D, 100, 2.0D));
		upgrades.put("maxMissiles", new Upgrade("Missile Storage", 1, 500, 3.0D));
		upgrades.put("meleeDmg", new Upgrade("Melee Damage", 0.05D, 100, 1.5D));
		upgrades.put("meleeAcc", new Upgrade("Melee Accuracy", 0.05D, 100, 1.5D));
		upgrades.put("meleeCost", new Upgrade("Melee Energy Cost", -0.05D, 100, 2.0D));
		upgrades.put("shieldGen", new Upgrade("Shield Power", 0.05D, 500, 2.0D));
		upgrades.put("shieldRange", new Upgrade("Shield Range", 1, 1000, 5.0D));
		upgrades.put("flak", new Upgrade("Flak", 0.05D, 500, 2.0D));
		upgrades.put("armor", new Upgrade("Armor", 1, 100, 1.3D));
		upgrades.put("repair", new Upgrade("Repair Crew", 50D, 500, 2.0D));
		this.weapons.addAll(weapons);
		this.missiles = this.maxMissiles = maxMissiles;
		this.rockets = this.maxRockets = maxRockets;
		this.moveCost = moveCost;
		this.cmdReward = cmdReward;
		this.moneyReward = moneyReward;
		this.boss = boss;
		this.mercenary = mercenary;
		this.spawns.addAll(spawns);
		// TODO this.support = weapons.stream().filter()
		this.location = location;
		this.modifiers = new HashMap<String, Modifier>();
		modifiers.put("accuracy", new Modifier(0.0D, 0));
		modifiers.put("moveCost", new Modifier(0.0D, 0));
		modifiers.put("evasion", new Modifier(0.0D, 0));
		modifiers.put("damage", new Modifier(0.0D, 0));
		modifiers.put("armor", new Modifier(0.0D, 0));
		modifiers.put("shield", new Modifier(0.0D, 0));
		modifiers.put("flak", new Modifier(0.0D, 0));
		modifiers.put("energy", new Modifier(0.0D, 0));
		modifiers.put("stealth", new Modifier(0.0D, 0));
		modifiers.put("shieldGen", new Modifier(0.0D, 0));
		modifiers.put("energyRegen", new Modifier(0.0D, 0));
		modifiers.put("noDeath", new Modifier(0.0D, 0));
		this.faction = faction;
	}
}