package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

public class TradeUpdate extends AServerPacket
{
	private final L2PcInstance activeChar;
	
	public TradeUpdate(final L2PcInstance activeChar)
	{
		this.activeChar = activeChar;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x74);
		
		writeH(activeChar.getActiveTradeList().getItems().size());
		for (final TradeItemHolder tradeHolder : activeChar.getActiveTradeList().getItems())
		{
			int availableCount = 1;
			boolean stackable = false;
			
			ItemInstance item = activeChar.getInventory().getItemByObjectId(tradeHolder.getObjectId());
			if (item == null)
			{
				continue;
			}
			
			if ((item.getCount() - tradeHolder.getCount()) > 0)
			{
				availableCount = item.getCount() - tradeHolder.getCount();
				stackable = tradeHolder.getItem().isStackable();
			}
			
			writeH(stackable ? 3 : 2);
			writeH(tradeHolder.getItem().getType1().getMask()); // item type1
			writeD(tradeHolder.getObjectId());
			writeD(tradeHolder.getItem().getId());
			writeD(availableCount);
			writeH(tradeHolder.getItem().getType2().ordinal()); // item type2
			writeH(0x00); // ?
			writeD(tradeHolder.getItem().getBodyPart().getMask()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			writeH(tradeHolder.getEnchant()); // enchant level
			writeH(0x00); // ?
			writeH(0x00);
		}
	}
}
