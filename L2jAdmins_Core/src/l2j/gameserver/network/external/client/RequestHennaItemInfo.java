package l2j.gameserver.network.external.client;

import l2j.gameserver.data.HennaData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.HennaHolder;
import l2j.gameserver.model.items.ItemHenna;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.HennaItemInfo;

/**
 * This class ...
 * @version $Revision$ $Date$
 */
public class RequestHennaItemInfo extends AClientPacket
{
	private int symbolId;
	
	@Override
	protected void readImpl()
	{
		symbolId = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		ItemHenna template = HennaData.getInstance().getTemplate(symbolId);
		if (template == null)
		{
			return;
		}
		
		activeChar.sendPacket(new HennaItemInfo(new HennaHolder(template), activeChar));
	}
}
