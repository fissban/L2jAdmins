package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class TradeStart extends AServerPacket
{
	private final L2PcInstance player;
	
	public TradeStart(L2PcInstance player)
	{
		this.player = player;
	}
	
	@Override
	public void writeImpl()
	{
		// 0x2e TradeStart d h (h dddhh dhhh)
		if ((player.getActiveTradeList() == null) || (player.getActiveTradeList().getPartner() == null))
		{
			return;
		}
		
		writeC(0x1E);
		writeD(player.getActiveTradeList().getPartner().getObjectId());
		
		writeH(player.getInventory().getAvailableItems(true).size());
		for (ItemInstance item : player.getInventory().getAvailableItems(true))// int i = 0; i < count; i++)
		{
			if (item == null)
			{
				continue;
			}
			
			writeH(item.getItem().getType1().getMask()); // item type1
			writeD(item.getObjectId());
			writeD(item.getId());
			writeD(item.getCount());
			writeH(item.getItem().getType2().ordinal()); // item type2
			writeH(0x00); // ?
			
			writeD(item.getItem().getBodyPart().getMask()); // slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			writeH(item.getEnchantLevel()); // enchant level
			
			writeH(0x00);
			writeH(item.getCustomType2());
		}
	}
}
