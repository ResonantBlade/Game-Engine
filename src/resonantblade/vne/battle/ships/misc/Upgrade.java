package matt.sunrider.battle.ships.misc;

public class Upgrade
{
	public final String dispName;
	public int level;
	public final double increase;
	public final int cost;
	public final double costMultiplier;
	
	public Upgrade(String dispName, double increase, int cost, double costMultiplier)
	{
		this.dispName = dispName;
		this.level = 0;
		this.increase = increase;
		this.cost = cost;
		this.costMultiplier = costMultiplier;
	}
	
	public double apply(double value)
	{
		return value + level * increase;
	}
	
	public double getCost()
	{
		return cost * Math.pow(costMultiplier, level);
	}
}