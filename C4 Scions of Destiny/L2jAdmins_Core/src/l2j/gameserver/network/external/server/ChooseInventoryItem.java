package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class ChooseInventoryItem extends AServerPacket
{
	private final int itemId;
	
	public ChooseInventoryItem(int item)
	{
		itemId = item;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x6f);
		writeD(itemId);
	}
}
