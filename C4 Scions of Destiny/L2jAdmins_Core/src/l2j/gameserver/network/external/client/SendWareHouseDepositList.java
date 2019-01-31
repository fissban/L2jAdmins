package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.illegalaction.enums.IllegalActionType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.itemcontainer.ItemContainer;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ... 31 SendWareHouseDepositList cd (dd)
 * @version $Revision: 1.3.4.5 $ $Date: 2005/04/11 10:06:09 $
 */
public class SendWareHouseDepositList extends AClientPacket
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
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		ItemContainer warehouse = player.getActiveWarehouse();
		if (warehouse == null)
		{
			return;
		}
		
		L2Npc manager = player.getLastTalkNpc();
		if (((manager == null) || !manager.isWarehouse() || !player.isInsideRadius(manager, L2Npc.INTERACTION_DISTANCE, false, false)) && !player.isGM())
		{
			return;
		}
		
		if (Config.GM_DISABLE_TRANSACTION && player.isGM())
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			return;
		}
		
		if (player.isRequestActive())
		{
			player.sendPacket(SystemMessage.ALREADY_TRADING);
			return;
		}
		
		if (player.getActiveEnchantItem() != null)
		{
			IllegalAction.report(player, "Player " + player.getName() + " tried to use enchant Exploit!", IllegalActionType.PUNISH_KICKBAN);
			player.setActiveEnchantItem(null);
			
			player.sendPacket(SystemMessage.ENCHANT_SCROLL_CANCELLED);
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (player.getKarma() > 0))
		{
			return;
		}
		
		// Freight price from config or normal price per item slot (30)
		int fee = count * 30;
		int currentAdena = player.getInventory().getAdena();
		
		int slots = 0;
		
		for (int i = 0; i < count; i++)
		{
			int objectId = items.get(i).getObjectId();
			int count = items.get(i).getCount();
			
			// Check validity of requested item
			ItemInstance item = player.getInventory().checkItemManipulation(objectId, count, "deposit");
			if (item == null)
			{
				LOG.warning("Error depositing a warehouse object for char " + player.getName());
				items.remove(i);
				continue;
			}
			
			// Calculate needed adena and slots
			if (item.getId() == Inventory.ADENA_ID)
			{
				currentAdena -= count;
			}
			
			if (!item.isStackable())
			{
				slots += count;
			}
			else if (warehouse.getItemById(item.getId()) == null)
			{
				slots++;
			}
		}
		
		// Item Max Limit Check
		if (!warehouse.validateCapacity(slots))
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
			return;
		}
		
		// Check if enough adena and charge the fee
		if ((currentAdena < fee) || !player.getInventory().reduceAdena("Warehouse", fee, player.getLastTalkNpc(), false))
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		// Proceed to the transfer
		for (int i = 0; i < count; i++)
		{
			int objectId = items.get(i).getObjectId();
			int count = items.get(i).getCount();
			
			// check for an invalid item
			if ((objectId == 0) && (count == 0))
			{
				continue;
			}
			
			ItemInstance oldItem = player.getInventory().getItemByObjectId(objectId);
			if (oldItem == null)
			{
				LOG.warning("Error depositing a warehouse object for char " + player.getName());
				continue;
			}
			
			if (!oldItem.isAvailable(player, true))
			{
				continue;
			}
			
			ItemInstance newItem = player.getInventory().transferItem("Warehouse", objectId, count, warehouse, player, player.getLastTalkNpc());
			if (newItem == null)
			{
				LOG.warning("Error depositing a warehouse object for char " + player.getName());
				continue;
			}
		}
		
		// Update current load as well
		player.updateCurLoad();
	}
}
