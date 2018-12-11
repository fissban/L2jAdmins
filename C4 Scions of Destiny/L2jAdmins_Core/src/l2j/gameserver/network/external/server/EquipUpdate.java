package l2j.gameserver.network.external.server;

import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * 5e 01 00 00 00 01 - added ? 02 - modified 7b 86 73 42 object id 08 00 00 00 body slot body slot 0000 ?? underwear 0001 ear 0002 ear 0003 neck 0004 finger (magic ring) 0005 finger (magic ring) 0006 head (l.cap) 0007 r.hand (dagger) 0008 l.hand (arrows) 0009 hands (short gloves) 000a chest (squire
 * shirt) 000b legs (squire pants) 000c feet 000d ?? back 000e lr.hand (bow) format ddd
 * @version $Revision: 1.4.2.1.2.4 $ $Date: 2005/03/27 15:29:40 $
 */
public class EquipUpdate extends AServerPacket
{
	private final ItemInstance item;
	private final int change;
	
	public EquipUpdate(ItemInstance item, int change)
	{
		this.item = item;
		this.change = change;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x4b);
		writeD(change);
		writeD(item.getObjectId());
		switch (item.getItem().getBodyPart())
		{
			case L_EAR:
				writeD(0x01);
				break;
			case R_EAR:
				writeD(0x02);
				break;
			case NECK:
				writeD(0x03);
				break;
			case R_FINGER:
				writeD(0x04);
				break;
			case L_FINGER:
				writeD(0x05);
				break;
			case HEAD:
				writeD(0x06);
				break;
			case R_HAND:
				writeD(0x07);
				break;
			case L_HAND:
				writeD(0x08);
				break;
			case GLOVES:
				writeD(0x09);
				break;
			case CHEST:
				writeD(0x0a);
				break;
			case LEGS:
				writeD(0x0b);
				break;
			case FEET:
				writeD(0x0c);
				break;
			case BACK:
				writeD(0x0d);
				break;
			case LR_HAND:
				writeD(0x0e);
				break;
			case HAIR:
				writeD(0x0f);
				break;
		}
	}
}
