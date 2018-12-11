package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * Format: (ch)ddddddd d: Number of Inventory Slots d: Number of Warehouse Slots d: Number of Freight Slots (unconfirmed) (200 for a low level dwarf) d: Private Sell Store Slots (unconfirmed) (4 for a low level dwarf) d: Private Buy Store Slots (unconfirmed) (5 for a low level dwarf) d: Dwarven
 * Recipe Book Slots d: Normal Recipe Book Slots
 * @author -Wooden- format from KenM
 */
public class ExStorageMaxCount extends AServerPacket
{
	private final L2PcInstance player;
	
	public ExStorageMaxCount(L2PcInstance character)
	{
		player = character;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x2e);
		
		writeD(player.getInventoryLimit());
		writeD(player.getWareHouseLimit());
		writeD(player.getFreightLimit());
		writeD(player.getPrivateSellStoreLimit());
		writeD(player.getPrivateBuyStoreLimit());
		writeD(player.getDwarfRecipeLimit());
		writeD(player.getCommonRecipeLimit());
	}
}
