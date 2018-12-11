package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.data.HennaTreeData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.HennaHolder;
import l2j.gameserver.network.AServerPacket;

public class HennaEquipList extends AServerPacket
{
	private final L2PcInstance player;
	private final List<HennaHolder> hennaEquipList;
	
	public HennaEquipList(L2PcInstance player)
	{
		this.player = player;
		hennaEquipList = HennaTreeData.getInstance().getAvailableHenna(player.getClassId());
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xe2);
		writeD(player.getInventory().getAdena()); // activeChar current amount of adena
		writeD(3); // available equip slot
		
		writeD(hennaEquipList.size());
		
		for (HennaHolder element : hennaEquipList)
		{
			if ((player.getInventory().getItemById(element.getItemIdDye())) != null)
			{
				writeD(element.getSymbolId()); // symbolid
				writeD(element.getItemIdDye()); // itemid of dye
				writeD(element.getAmountDyeRequire()); // amount of dye require
				writeD(element.getPrice()); // amount of adena require
				writeD(0x01); // meet the requirement or not
			}
			else
			{
				writeD(0x00);
				writeD(0x00);
				writeD(0x00);
				writeD(0x00);
				writeD(0x00);
			}
		}
	}
}
