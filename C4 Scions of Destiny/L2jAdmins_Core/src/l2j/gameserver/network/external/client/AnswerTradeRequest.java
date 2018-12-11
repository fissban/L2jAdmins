package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.5.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class AnswerTradeRequest extends AClientPacket
{
	private int response;
	
	@Override
	protected void readImpl()
	{
		response = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (Config.GM_DISABLE_TRANSACTION && player.isGM())
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			player.onTradeCancel(null);
			return;
		}
		
		L2PcInstance partner = player.getRequestTrade().getPartner();
		if (partner == null)
		{
			// Trade partner not found, cancel trade
			player.sendPacket(SystemMessage.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			player.onTradeCancel(null);
			return;
		}
		
		if (response == 1)
		{
			// init trade
			player.onTradeStart(partner);
			partner.onTradeStart(player);
		}
		else
		{
			partner.sendPacket(new SystemMessage(SystemMessage.C1_DENIED_TRADE_REQUEST).addString(player.getName()));
			player.onTradeCancel(null);
		}
	}
}
