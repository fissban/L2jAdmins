package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.HennaHolder;
import l2j.gameserver.network.AServerPacket;

public class HennaInfo extends AServerPacket
{
	private final L2PcInstance player;
	private final HennaHolder[] hennas = new HennaHolder[3];
	private final int count;
	
	public HennaInfo(L2PcInstance player)
	{
		this.player = player;
		
		int j = 0;
		for (int i = 0; i < 3; i++)
		{
			HennaHolder h = player.getHenna(i + 1);
			if (h != null)
			{
				hennas[j++] = h;
			}
		}
		count = j;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xe4);
		
		writeC(player.getHennaStatINT()); // equip INT
		writeC(player.getHennaStatSTR()); // equip STR
		writeC(player.getHennaStatCON()); // equip CON
		writeC(player.getHennaStatMEN()); // equip MEN
		writeC(player.getHennaStatDEX()); // equip DEX
		writeC(player.getHennaStatWIT()); // equip WIT
		
		writeD(3); // slots?
		
		writeD(count); // size
		for (int i = 0; i < count; i++)
		{
			writeD(hennas[i].getSymbolId());
			writeD(hennas[i].getSymbolId());
		}
	}
}
