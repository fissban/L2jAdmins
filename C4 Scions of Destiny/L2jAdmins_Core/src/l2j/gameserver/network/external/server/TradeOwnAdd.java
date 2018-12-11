package l2j.gameserver.network.external.server;

import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @author  Yme
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class TradeOwnAdd extends AServerPacket
{
	private final TradeItemHolder item;
	
	public TradeOwnAdd(TradeItemHolder item)
	{
		this.item = item;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x20);
		
		writeH(1); // item count
		
		writeH(item.getItem().getType1().getMask()); // item type1
		writeD(item.getObjectId());
		writeD(item.getItem().getId());
		writeD(item.getCount());
		writeH(item.getItem().getType2().ordinal()); // item type2
		writeH(0x00); // ?
		
		writeD(item.getItem().getBodyPart().getMask()); // slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
		writeH(item.getEnchant()); // enchant level
		
		writeH(0x00);
		writeH(item.getCustomType2());
	}
}
