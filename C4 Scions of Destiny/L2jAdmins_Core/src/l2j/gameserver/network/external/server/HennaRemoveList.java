package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.HennaHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Micr0(Rework for L2jAdmins)
 */
public class HennaRemoveList extends AServerPacket
{
	private final L2PcInstance player;
	
	public HennaRemoveList(L2PcInstance player)
	{
		this.player = player;
	}
	
	@Override
	public final void writeImpl()
	{
		writeC(0xe5);
		writeD(player.getInventory().getAdena());
		writeD(player.getHennaEmptySlots());
		writeD(Math.abs(player.getHennaEmptySlots() - 3));
		
		for (int i = 1; i <= 3; i++)
		{
			HennaHolder henna = player.getHenna(i);
			if (henna != null)
			{
				writeD(henna.getSymbolId());
				writeD(henna.getItemIdDye());
				writeD(henna.getAmountDyeRequire() / 2);
				writeD(henna.getCancelFee());
				writeD(0x01);
			}
		}
	}
}
