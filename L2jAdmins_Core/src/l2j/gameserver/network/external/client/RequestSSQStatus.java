package l2j.gameserver.network.external.client;

import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SSQStatus;

/**
 * Seven Signs Record Update Request packet type id 0xc7 format: cc
 * @author Tempy
 */
public class RequestSSQStatus extends AClientPacket
{
	private int page;
	
	@Override
	protected void readImpl()
	{
		page = readC();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if ((SevenSignsManager.getInstance().isSealValidationPeriod() || SevenSignsManager.getInstance().isCompResultsPeriod()) && (page == 4))
		{
			return;
		}
		
		activeChar.sendPacket(new SSQStatus(activeChar, page));
	}
}
