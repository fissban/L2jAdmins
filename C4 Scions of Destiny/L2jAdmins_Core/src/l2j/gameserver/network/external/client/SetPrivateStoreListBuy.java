package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.privatestore.PcStoreType;
import l2j.gameserver.model.trade.CharacterTradeList;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.PrivateStoreManageListBuy;
import l2j.gameserver.network.external.server.PrivateStoreMsgBuy;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.task.continuous.AttackStanceTaskManager;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class SetPrivateStoreListBuy extends AClientPacket
{
	private int count;
	private List<ItemStoreBuy> items;
	
	@Override
	protected void readImpl()
	{
		count = readD();
		if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET))
		{
			count = 0;
			items = null;
			return;
		}
		
		items = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
		{
			int itemId = readD();
			readH(); // TODO analyse this
			readH(); // TODO analyse this
			int cnt = readD();
			int price = readD();
			
			if ((itemId < 1) || (cnt < 1) || (price < 0))
			{
				items = null;
				return;
			}
			items.add(new ItemStoreBuy(itemId, cnt, price));
		}
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
			player.sendMessage("Transactions are disable for your Access Level");
			return;
		}
		
		if (AttackStanceTaskManager.getInstance().isInAttackStance(player) || player.isCastingNow())
		{
			player.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			player.sendPacket(new PrivateStoreManageListBuy(player));
			return;
		}
		
		if (player.isInsideZone(ZoneType.NO_STORE))
		{
			player.sendPacket(SystemMessage.NO_PRIVATE_STORE_HERE);
			return;
		}
		
		CharacterTradeList tradeList = player.getPrivateStore().getBuyList();
		tradeList.clear();
		
		if (items == null)
		{
			return;
		}
		
		// Check maximum number of allowed slots for pvt shops
		if (items.size() > player.getPrivateBuyStoreLimit())
		{
			player.sendPacket(SystemMessage.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
			player.sendPacket(new PrivateStoreManageListBuy(player));
			return;
		}
		
		int totalCost = 0;
		for (ItemStoreBuy i : items)
		{
			if (!i.addToTradeList(tradeList))
			{
				player.sendPacket(SystemMessage.EXCEEDED_THE_MAXIMUM);
				player.sendPacket(new PrivateStoreManageListBuy(player));
				return;
			}
			
			totalCost += i.getCost();
			if (totalCost > Integer.MAX_VALUE)
			{
				player.sendPacket(SystemMessage.EXCEEDED_THE_MAXIMUM);
				player.sendPacket(new PrivateStoreManageListBuy(player));
				return;
			}
		}
		
		// Check for available funds
		if (totalCost > player.getInventory().getAdena())
		{
			player.sendPacket(SystemMessage.THE_PURCHASE_PRICE_IS_HIGHER_THAN_MONEY);
			player.sendPacket(new PrivateStoreManageListBuy(player));
			return;
		}
		
		player.sitDown();
		player.getPrivateStore().setStoreType(PcStoreType.BUY);
		player.broadcastUserInfo();
		player.broadcastPacket(new PrivateStoreMsgBuy(player));
	}
	
	private static class ItemStoreBuy
	{
		private final int itemId, count, price;
		
		public ItemStoreBuy(int id, int num, int pri)
		{
			itemId = id;
			count = num;
			price = pri;
		}
		
		public boolean addToTradeList(CharacterTradeList list)
		{
			if ((Integer.MAX_VALUE / count) < price)
			{
				return false;
			}
			
			list.addItemByItemId(itemId, count, price);
			return true;
		}
		
		public long getCost()
		{
			return count * price;
		}
	}
}
