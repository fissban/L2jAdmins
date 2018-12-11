package l2j.gameserver.model.privatestore;

/**
 * Los valores asignados a cada enumerador solo son usados en los paquetes cliente <-> server
 * @author fissban
 */
public enum PcStoreType
{
	NONE(0),
	SELL(1),
	SELL_MANAGE(2),
	BUY(3),
	BUY_MANAGE(4),
	MANUFACTURE(5),
	PACKAGE_SELL(8);
	
	private int val;
	
	PcStoreType(int val)
	{
		this.val = val;
	}
	
	public int getValue()
	{
		return val;
	}
}
