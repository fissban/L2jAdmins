package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ExAutoSoulShot extends AServerPacket
{
	private final int itemId;
	private final AutoSoulShotType type;
	
	public enum AutoSoulShotType
	{
		DESACTIVE,
		ACTIVE;
	}
	
	/**
	 * 0xfe:0x12 ExAutoSoulShot (ch)dd
	 * @param itemId
	 * @param type
	 */
	public ExAutoSoulShot(int itemId, AutoSoulShotType type)
	{
		this.itemId = itemId;
		this.type = type;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xFE);
		writeH(0x12); // sub id
		writeD(itemId);
		writeD(type.ordinal());
	}
}
