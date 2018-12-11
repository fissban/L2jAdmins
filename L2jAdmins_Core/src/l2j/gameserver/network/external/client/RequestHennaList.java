package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.HennaEquipList;

/**
 * RequestHennaList - 0xba
 * @author Tempy
 */
public class RequestHennaList extends AClientPacket
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
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.sendPacket(new HennaEquipList(activeChar));
	}
}
