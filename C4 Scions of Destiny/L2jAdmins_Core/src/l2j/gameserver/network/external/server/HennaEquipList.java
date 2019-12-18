package l2j.gameserver.network.external.server;

import java.util.List;

import l2j.gameserver.data.HennaData;
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
		hennaEquipList = HennaData.getByClass(player.getClassId().getId());
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xe2);
		writeD(player.getInventory().getAdena()); // activeChar current amount of adena
		writeD(3); // available equip slot
		
		writeD(hennaEquipList.size());
		
		for (HennaHolder henna : hennaEquipList)
		{
			if ((player.getInventory().getItemById(henna.getDyeId())) != null)
			{
				writeD(henna.getSymbolId()); // symbolid
				writeD(henna.getDyeId()); // itemid of dye
				writeD(henna.getDyeAmount()); // amount of dye require
				writeD(henna.getPrice()); // amount of adena require
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
