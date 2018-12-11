package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ManufactureItemHolder;
import l2j.gameserver.model.privatestore.PrivateStoreList;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ... dddd d(ddd)
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class RecipeShopSellList extends AServerPacket
{
	private final L2PcInstance buyer, manufacturer;
	
	public RecipeShopSellList(L2PcInstance buyer, L2PcInstance manufacturer)
	{
		this.buyer = buyer;
		this.manufacturer = manufacturer;
	}
	
	@Override
	public void writeImpl()
	{
		PrivateStoreList createList = manufacturer.getPrivateStore().getCreateList();
		
		if (createList != null)
		{
			// dddd d(ddd)
			writeC(0xd9);
			writeD(manufacturer.getObjectId());
			writeD((int) manufacturer.getCurrentMp());// Creator's MP
			writeD(manufacturer.getStat().getMaxMp());// Creator's MP
			writeD(buyer.getInventory().getAdena());// Buyer Adena
			
			int count = createList.size();
			writeD(count);
			
			for (ManufactureItemHolder temp : createList.getList())
			{
				writeD(temp.getRecipeId());
				writeD(0x00); // unknown
				writeD(temp.getCost());
			}
		}
	}
}
