package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * Sdh(h dddhh [dhhh] d) Sdh ddddd ddddd ddddd ddddd
 * @version $Revision: 1.1.2.1.2.4 $ $Date: 2005/03/29 23:15:10 $
 */
public class GMViewWarehouseWithdrawList extends AServerPacket
{
	private final L2PcInstance cha;
	
	public GMViewWarehouseWithdrawList(L2PcInstance cha)
	{
		this.cha = cha;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x95);
		writeS(cha.getName());
		writeD(cha.getInventory().getAdena());
		writeH(cha.getWarehouse().getItems().size());
		
		for (final ItemInstance item : cha.getWarehouse().getItems())
		{
			writeH(item.getItem().getType1().getMask());
			writeD(item.getObjectId());
			writeD(item.getId());
			writeD(item.getCount());
			writeH(item.getItem().getType2().ordinal());
			writeH(item.getCustomType1());
			writeD(item.getItem().getBodyPart().getMask());
			writeH(item.getEnchantLevel());
			writeH(0x00);
			writeH(item.getCustomType2());
			writeD(item.getObjectId());
		}
	}
}
