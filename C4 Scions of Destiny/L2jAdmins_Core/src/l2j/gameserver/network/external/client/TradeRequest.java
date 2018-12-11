package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.PcBlockList;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SendTradeRequest;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Util;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class TradeRequest extends AClientPacket
{
	private int objectId;
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
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
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		L2Object target = L2World.getInstance().getObject(objectId);
		if ((target == null) || !player.getKnownList().getObject(target) || !(target instanceof L2PcInstance) || (target.getObjectId() == player.getObjectId()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.TARGET_IS_INCORRECT));
			return;
		}
		
		if (player.isDead())
		{
			player.sendMessage("Dead players cannot request for a trade.");
			return;
		}
		
		if (player.getFishing().isFishing())
		{
			player.sendMessage("Cannot request for a trade while fishing.");
			return;
		}
		
		L2PcInstance partner = (L2PcInstance) target;
		if (partner.isInOlympiadMode() || player.isInOlympiadMode())
		{
			player.sendMessage("You or your target cant request trade in Olympiad mode.");
			return;
		}
		
		if (PcBlockList.isBlocked(partner, player))
		{
			player.sendMessage("This player has added you to his/her block list, therefore you cannot request for a trade.");
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && ((player.getKarma() > 0) || (partner.getKarma() > 0)))
		{
			player.sendMessage("Chaotic players can't use Trade.");
			return;
		}
		
		if ((player.getPrivateStore().isInStoreMode()) || (partner.getPrivateStore().isInStoreMode()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE));
			return;
		}
		
		if (player.isRequestActive())
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1_IS_BUSY_TRY_LATER).addString(partner.getName()));
		}
		
		if (player.getActiveTradeList() != null)
		{
			player.sendPacket(new SystemMessage(SystemMessage.ALREADY_TRADING));
		}
		
		if (partner.getTradeRefusal())
		{
			player.sendMessage("Target is in trade refusal mode.");
			return;
		}
		
		if (Util.calculateDistance(player, partner, true) > 150)
		{
			player.sendPacket(new SystemMessage(SystemMessage.TARGET_TOO_FAR));
			return;
		}
		
		if (!player.getRequestTrade().startRequest(partner))
		{
			return;
		}
		
		partner.sendPacket(new SendTradeRequest(player.getObjectId()));
		player.sendPacket(new SystemMessage(SystemMessage.REQUEST_C1_FOR_TRADE).addString(partner.getName()));
	}
}
