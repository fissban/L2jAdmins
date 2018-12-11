package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestShortCutDel extends AClientPacket
{
	private int slot;
	private int page;
	
	@Override
	protected void readImpl()
	{
		int id = readD();
		slot = id % 12;
		page = id / 12;
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		// client needs no confirmation.
		// This packet is just to inform the server
		activeChar.getShortCuts().deleteShortCut(slot, page);
	}
}
