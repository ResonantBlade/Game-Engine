package resonantblade.vne.battle.ships;

import java.util.List;

import resonantblade.vne.battle.BattleManager;
import resonantblade.vne.battle.ships.misc.Faction;
import resonantblade.vne.battle.ships.misc.Modifier;
import resonantblade.vne.battle.ships.misc.ShipAttributes;
import resonantblade.vne.battle.ships.misc.ShipType;
import resonantblade.vne.battle.ships.misc.WeaponType;
import resonantblade.vne.script.ScriptInterpreter;

public abstract class Battleship
{
	public ShipAttributes attributes;
	public ShipType type;
	public String label;
	public String portrait;
	
	public Battleship(ShipType type, Faction faction, String label, String portrait, ShipAttributes attributes)
	{
		this.attributes = attributes;
		this.type = type;
		this.label = label;
		this.portrait = portrait;
	}
	
	public void updateArmor()
	{
		int armor = attributes.armor = (int) (attributes.maxArmor * ((100 + attributes.modifiers.get("armor").value) / 100.0D) * attributes.hp / attributes.maxHp);
		attributes.armorColor = armor > attributes.maxArmor ? 0x007F00 : armor < attributes.maxArmor ? 0x7F0000 : 0;
	}
	
	public void updateStats()
	{
		Modifier energyRegen = attributes.modifiers.get("energyRegen");
		if(energyRegen.value == -100)
			attributes.en = 0;
		
		attributes.shields = 0;
		List<Battleship> ships = BattleManager.BM.ships;
		for(Battleship ship : ships)
		{
			if(attributes.faction == ship.attributes.faction && Functions.getDistance(this, ship) <= ship.attributes.shieldRange)
			{
				double additionalShields = ship.attributes.shieldGen;
				Modifier mod = ship.attributes.modifiers.get("shieldGen");
				additionalShields += mod.value;
				if(additionalShields > 0)
					attributes.shields += additionalShields;
			}
		}
		
		if(attributes.shields > 1.0D)
			attributes.shields = 1.0D;
		attributes.shieldColor = attributes.shields > attributes.shieldGen ? 0x007F00 : 0;
		updateArmor();
	}
	
	public void recieveDamage(int damage, Battleship attacker, WeaponType type)
	{
		if(attributes.faction == Faction.PLAYER && type == WeaponType.VANGUARD)
		{
			// TODO show animation
			
			ScriptInterpreter.jump("sunrider_destroyed");
		}
		else if(type == WeaponType.SUPPORT)
		{
			attributes.hp += Math.min(attributes.hp + damage, attributes.maxHp);
		}
		
		attributes.totalDamageDone += damage;
		switch(type)
		{
		case KINETIC:
		case ASSAULT:
			attributes.totalKineticDamageDone += damage;
			attributes.fear.put("kinetic", attributes.fear.get("kinetic") + damage / 10);
			break;
		case LASER:
		case PULSE:
			attributes.totalEnergyDamageDone += damage;
			attributes.fear.put("energy", attributes.fear.get("energy") + damage / 10);
			break;
		case MISSILE:
		case ROCKET:
			attributes.totalMissileDamageDone += damage;
			attributes.fear.put("missile", attributes.fear.get("missile") + damage / 10);
			break;
		}
		
		attacker.attributes.hate += damage * 0.5D;
		attributes.hate = Math.max(attributes.hate - damage, 100);
		
		damage = getModifiedDamage(damage, attributes.faction);
		
		if(attributes.modifiers.get("noDeath").value == 1)
		{
			damage = Math.min(damage, attributes.hp - 1);
		}
		
		if(damage == 0)
		{
			// TODO miss animation
			return;
		}
		
		attributes.hp -= damage;
		
		// show animation
		
		if(attributes.hp <= 0)
		{
			destroy(attacker);
			BattleManager.BM.ships.stream().filter(ship -> attributes.faction == ship.attributes.faction).forEach(ship -> ship.updateStats());
		}
		else
		{
			updateStats();
		}
	}
	
	public void destroy(Battleship attacker, boolean skipAnimation)
	{
		attributes.en = 0;
		
		if(BattleManager.BM.selected == this)
			BattleManager.BM.unselectShip(this);
		
		attributes.hate = 100;
		if(attributes.faction != Faction.PLAYER)
		{
			attacker.attributes.hate += attributes.maxHp * 0.3;
			BattleManager.BM.ships.stream()
			.filter(ship -> !attributes.allies.contains(ship.attributes.faction))
			.filter(ship -> getDistance(this, ship) <= 4)
			.forEach(ship -> ship.attributes.morale -= 20);
		}
		
		if(!skipAnimation)
		{
			// TODO death animation
		}
		
		BattleManager.BM.destroyedShips.add(this);
		BattleManager.BM.setCellAvailable(attributes.location);
		BattleManager.BM.ships.remove(this);
		
		if(attributes.boss)
			BattleManager.BM.bossDied(this);
		
		BattleManager.BM.checkForLoss();
		BattleManager.BM.checkForWin();
	}
	
	public void destroy(Battleship attacker)
	{
		destroy(attacker, false);
	}
}