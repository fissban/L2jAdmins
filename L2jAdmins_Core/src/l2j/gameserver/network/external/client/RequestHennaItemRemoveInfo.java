package l2j.gameserver.network.external.client;

import l2j.gameserver.data.HennaData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.HennaItemRemoveInfo;

/**
 * @author Micr0(Rework for L2jAdmins)
 */
public class RequestHennaItemRemoveInfo extends AClientPacket
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
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.sendPacket(new HennaItemRemoveInfo(HennaData.getById(symbolId), activeChar));
	}
}
