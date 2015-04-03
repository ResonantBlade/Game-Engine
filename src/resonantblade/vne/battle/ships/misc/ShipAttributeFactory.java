package resonantblade.vne.battle.ships.misc;

public class ShipAttributeFactory
{
	public static ShipAttributeFactory getInstance(ShipType type)
	{
		switch(type)
		{
		case ASSAULT_CARRIER:
			
			break;
		case BATTLESHIP:
			
			break;
		case CARRIER:
			
			break;
		case CRUISER:
			
			break;
		case DESTROYER:
			
			break;
		case FRIGATE:
			
			break;
		case RYDER:
			
			break;
		case SHIP:
			
			break;
		case STATION:
			
			break;
		case SUPER_DREADNOUGHT:
			
			break;
		}
	}
	
	private final ShipAttributes attributes;
	
	private ShipAttributeFactory(ShipAttributes attributes)
	{
		this.attributes = attributes;
	}
	
	public ShipAttributes getAttributes()
	{
		return attributes;
	}
}