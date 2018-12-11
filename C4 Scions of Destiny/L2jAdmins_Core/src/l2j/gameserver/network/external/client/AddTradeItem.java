package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.TradeItemHolder;
import l2j.gameserver.model.trade.CharacterTradeList;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.TradeOtherAdd;
import l2j.gameserver.network.external.server.TradeOwnAdd;
import l2j.gameserver.network.external.server.TradeUpdate;

/**
 * This class ...
 * @version $Revision: 1.5.2.2.2.5 $ $Date: 2005/03/27 15:29:29 $
 */
public class AddTradeItem extends AClientPacket
{
	private int tradeId;
	private int objectId;
	private int count;
	
	@Override
	protected void readImpl()
	{
		tradeId = readD();
		objectId = readD();
		count = readD();
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
			LOG.warning("Character: " + player.getName() + " requested item:" + objectId + " add without active tradelist:" + tradeId);
			return;
		}
		
		L2PcInstance partner = trade.getPartner();
		if ((partner == null) || (L2World.getInstance().getObject(partner.getObjectId()) == null) || (partner.getActiveTradeList() == null))
		{
			// Trade partner not found, cancel trade
			if (trade.getPartner() != null)
			{
				LOG.warning("Character:" + player.getName() + " requested invalid trade object: " + objectId);
			}
			
			player.sendPacket(SystemMessage.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			player.cancelActiveTrade();
			return;
		}
		
		if (trade.isConfirmed() || partner.getActiveTradeList().isConfirmed())
		{
			player.sendPacket(SystemMessage.CANNOT_ADJUST_ITEMS_AFTER_TRADE_CONFIRMED);
			return;
		}
		
		if (Config.GM_DISABLE_TRANSACTION && player.isGM())
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			player.cancelActiveTrade();
			return;
		}
		
		if (!player.validateItemManipulation(objectId, "trade"))
		{
			player.sendPacket(SystemMessage.NOTHING_HAPPENED);
			return;
		}
		
		TradeItemHolder item = trade.addItem(objectId, count);
		if (item != null)
		{
			// Trade start packet updates tradelist here
			player.sendPacket(new TradeOwnAdd(item));
			player.sendPacket(new TradeUpdate(player));
			trade.getPartner().sendPacket(new TradeOtherAdd(item));
		}
	}
}
