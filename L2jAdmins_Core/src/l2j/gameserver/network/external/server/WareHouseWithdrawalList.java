package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.warehouse.enums.WareHouseType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * 0x54 WarehouseWithdrawalList dh (h dddhh dhhh d)
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/29 23:15:10 $
 */
public class WareHouseWithdrawalList extends AServerPacket
{
	private L2PcInstance player;
	private WareHouseType whtype;
	
	public WareHouseWithdrawalList(L2PcInstance player, WareHouseType type)
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
		
		// Si el personaje tiene el inventario vacio le enviamos un mensaje avisandole
		if (player.getActiveWarehouse().getSize() == 0)
		{
			player.sendPacket(SystemMessage.NO_ITEM_DEPOSITED_IN_WH);
			return;
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x42);
		writeH(whtype.ordinal());
		writeD(player.getInventory().getAdena());
		writeH(player.getActiveWarehouse().getItems().size());
		
		for (final ItemInstance item : player.getActiveWarehouse().getItems())
		{
			writeH(item.getItem().getType1().getMask()); // item type1 //unconfirmed, works
			writeD(item.getObjectId()); // unconfirmed, works
			writeD(item.getId()); // unconfirmed, works
			writeD(item.getCount()); // unconfirmed, works
			writeH(item.getItem().getType2().ordinal()); // item type2 //unconfirmed, works
			writeH(0x00); // ?
			writeD(item.getItem().getBodyPart().getMask()); // ?
			writeH(item.getEnchantLevel()); // enchant level -confirmed
			writeH(0x00);
			writeH(item.getCustomType2());
			writeD(item.getObjectId()); // item id - confirmed
		}
	}
}
