package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.trade.CharacterTradeList;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Util;

/**
 * This class ...
 * @version $Revision: 1.6.2.2.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class TradeDone extends AClientPacket
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
		
		CharacterTradeList trade = player.getActiveTradeList();
		if (trade == null)
		{
			// LOG.warning("player.getTradeList == null in " + getClass().getSimpleName() + " for player " + player.getName());
			return;
		}
		
		if (trade.isLocked())
		{
			return;
		}
		
		if (response == 1)
		{
			if ((trade.getPartner() == null) || (L2World.getInstance().getObject(trade.getPartner().getObjectId()) == null))
			{
				// Trade partner not found, cancel trade
				player.cancelActiveTrade();
				player.sendPacket(new SystemMessage(SystemMessage.TARGET_IS_NOT_FOUND_IN_THE_GAME));
				return;
			}
			
			if ((trade.getOwner().getActiveEnchantItem() != null) || (trade.getPartner().getActiveEnchantItem() != null))
			{
				return;
			}
			
			if (Config.GM_DISABLE_TRANSACTION && player.isGM())
			{
				player.cancelActiveTrade();
				player.sendMessage("Transactions are disabled for your Access Level.");
				return;
			}
			
			if (Util.calculateDistance(player, trade.getPartner(), true) > 150)
			{
				player.cancelActiveTrade();
				return;
			}
			
			trade.confirm();
		}
		else
		{
			player.cancelActiveTrade();
		}
	}
}
