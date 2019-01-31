package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.itemcontainer.ItemContainer;
import l2j.gameserver.model.itemcontainer.warehouse.ClanWarehouse;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ... 32 SendWareHouseWithDrawList cd (dd) WootenGil rox :P
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/29 23:15:16 $
 */
public class SendWareHouseWithDrawList extends AClientPacket
{
	private int count;
	private List<ItemHolder> items;
	
	@Override
	protected void readImpl()
	{
		count = readD();
		if ((count < 0) || (count > Config.MAX_ITEM_IN_PACKET))
		{
			return;
		}
		
		items = new ArrayList<>(count);
		
		for (int i = 0; i < count; i++)
		{
			int objectId = readD();
			int count = readD();
			
			if ((objectId < 1) || (count < 0))
			{
				items.clear();
				return;
			}
			
			items.add(new ItemHolder(0, objectId, count));
		}
	}
	
	@SuppressWarnings("null")
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.isRequestActive())
		{
			player.sendPacket(SystemMessage.ALREADY_TRADING);
			return;
		}
		
		ItemContainer warehouse = player.getActiveWarehouse();
		if (warehouse == null)
		{
			return;
		}
		L2Npc manager = player.getLastTalkNpc();
		
		if (((manager == null) || !player.isInsideRadius(manager, L2Npc.INTERACTION_DISTANCE, false, false)) && !player.isGM())
		{
			return;
		}
		
		if (!manager.isWarehouse())
		{
			player.sendPacket(new SystemMessage(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
		}
		
		if (Config.GM_DISABLE_TRANSACTION && player.isGM())
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (player.getKarma() > 0))
		{
			return;
		}
		
		if ((warehouse instanceof ClanWarehouse) && !player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessage.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT));
			return;
		}
		
		int weight = 0;
		int slots = 0;
		
		for (ItemHolder ih : items)
		{
			// Calculate needed slots
			ItemInstance item = warehouse.getItemByObjectId(ih.getObjectId());
			if (item == null)
			{
				continue;
			}
			weight += weight * item.getItem().getWeight();
			if (!item.isStackable())
			{
				slots += ih.getCount();
			}
			else if (player.getInventory().getItemById(item.getId()) == null)
			{
				slots++;
			}
		}
		
		// Item Max Limit Check
		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(new SystemMessage(SystemMessage.SLOTS_FULL));
			return;
		}
		
		// Weight limit Check
		if (!player.getInventory().validateWeight(weight))
		{
			sendPacket(new SystemMessage(SystemMessage.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		// Proceed to the transfer
		for (int i = 0; i < count; i++)
		{
			int objectId = items.get(i).getObjectId();
			int count = items.get(i).getCount();
			
			ItemInstance oldItem = warehouse.getItemByObjectId(objectId);
			if ((oldItem == null) || (oldItem.getCount() < count))
			{
				player.sendMessage("Cannot withdraw requested item(s).");
			}
			ItemInstance newItem = warehouse.transferItem("Warehouse", objectId, count, player.getInventory(), player, player.getLastTalkNpc());
			if (newItem == null)
			{
				LOG.warning("Error withdrawing a warehouse object for char " + player.getName());
				continue;
			}
		}
		
		// Update current load as well
		player.updateCurLoad();
	}
}
