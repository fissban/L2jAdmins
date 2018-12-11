package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.HennaRemoveList;

/**
 * RequestHennaRemoveList
 * @author Tempy
 */
public class RequestHennaRemoveList extends AClientPacket
{
	@SuppressWarnings("unused")
	private int unknown;
	
	@Override
	protected void readImpl()
	{
		unknown = readD(); // ??
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.sendPacket(new HennaRemoveList(activeChar));
	}
}
