package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.privatestore.PcStoreType;
import l2j.gameserver.model.trade.CharacterTradeList;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.PrivateStoreManageListSell;
import l2j.gameserver.network.external.server.PrivateStoreMsgSell;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.task.continuous.AttackStanceTaskManager;

/**
 * This class ...
 * @version $Revision: 1.2.2.1.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class SetPrivateStoreListSell extends AClientPacket
{
	private int count;
	private boolean packageSale;
	private List<Item> items; // count * 3
	
	@Override
	protected void readImpl()
	{
		packageSale = (readD() == 1);
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
			int cnt = readD();
			int price = readD();
			
			if ((itemId < 1) || (cnt < 1) || (price < 0))
			{
				items = null;
				return;
			}
			items.add(new Item(itemId, cnt, price));
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
		
		if (AttackStanceTaskManager.getInstance().isInAttackStance(player) || player.isCastingNow())
		{
			player.sendPacket(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			player.sendPacket(new PrivateStoreManageListSell(player));
			return;
		}
		
		if (Config.GM_DISABLE_TRANSACTION && player.isGM())
		{
			player.sendMessage("Transactions are disable for your Access Level");
			return;
		}
		
		if (player.isInsideZone(ZoneType.NO_STORE))
		{
			player.sendPacket(SystemMessage.NO_PRIVATE_STORE_HERE);
			return;
		}
		
		if (items == null)
		{
			return;
		}
		
		// Check maximum number of allowed slots for pvt shops
		if (items.size() > player.getPrivateSellStoreLimit())
		{
			player.sendPacket(SystemMessage.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
			player.sendPacket(new PrivateStoreManageListSell(player));
			return;
		}
		
		CharacterTradeList tradeList = player.getPrivateStore().getSellList();
		tradeList.clear();
		tradeList.setPackaged(packageSale);
		
		int totalCost = player.getInventory().getAdena();
		for (Item i : items)
		{
			if (!i.addToTradeList(tradeList))
			{
				player.sendPacket(SystemMessage.EXCEEDED_THE_MAXIMUM);
				player.sendPacket(new PrivateStoreManageListSell(player));
				return;
			}
			
			totalCost += i.getPrice();
			if (totalCost > Integer.MAX_VALUE)
			{
				player.sendPacket(SystemMessage.EXCEEDED_THE_MAXIMUM);
				player.sendPacket(new PrivateStoreManageListSell(player));
				return;
			}
		}
		
		player.sitDown();
		
		if (packageSale)
		{
			player.getPrivateStore().setStoreType(PcStoreType.PACKAGE_SELL);
		}
		else
		{
			player.getPrivateStore().setStoreType(PcStoreType.SELL);
		}
		
		player.broadcastUserInfo();
		player.broadcastPacket(new PrivateStoreMsgSell(player));
	}
	
	private static class Item
	{
		private final int itemId, count, price;
		
		public Item(int id, int num, int pri)
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
			
			list.addItem(itemId, count, price);
			return true;
		}
		
		public long getPrice()
		{
			return count * price;
		}
	}
}
