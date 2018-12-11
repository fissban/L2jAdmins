package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ExSendManorList;

/**
 * Format: ch c (id) 0xD0 h (subid) 0x08
 * @author l3x
 */
public class RequestManorList extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		player.sendPacket(new ExSendManorList());
	}
}
