package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.warehouse.enums.WareHouseType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * 0x53 WareHouseDepositList dh (h dddhh dhhh d)
 * @version $Revision: 1.4.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class WareHouseDepositList extends AServerPacket
{
	private final L2PcInstance player;
	private final WareHouseType whtype;
	
	public WareHouseDepositList(L2PcInstance player, WareHouseType type)
	{
		this.player = player;
		whtype = type;
		
		// Definimos el warehouse que vera el personaje dependiendo del type indicado.
		switch (whtype)
		{
			case CLAN:
				player.setActiveWarehouse(player.getClan().getWarehouse());
				break;
			
			case PRIVATE:
				player.setActiveWarehouse(player.getWarehouse());
				break;
			
			case FREIGHT:
				player.setActiveWarehouse(player.getFreight());
				break;
		}
		
		player.tempInventoryDisable();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x41);
		writeH(whtype.ordinal());
		writeD(player.getInventory().getAdena());
		writeH(player.getInventory().getAvailableItems(true).size());
		
		for (ItemInstance item : player.getInventory().getAvailableItems(true))
		{
			writeH(item.getItem().getType1().getMask()); // item type1 //unconfirmed, works
			writeD(item.getObjectId()); // unconfirmed, works
			writeD(item.getId()); // unconfirmed, works
			writeD(item.getCount()); // unconfirmed, works
			writeH(item.getItem().getType2().ordinal()); // item type2 //unconfirmed, works
			writeH(0x00); // ? 100
			writeD(item.getItem().getBodyPart().getMask()); // ?
			writeH(item.getEnchantLevel()); // enchant level -confirmed
			writeH(0x00);
			writeH(item.getCustomType2());
			writeD(item.getObjectId()); // item id - confirmed
		}
	}
}
