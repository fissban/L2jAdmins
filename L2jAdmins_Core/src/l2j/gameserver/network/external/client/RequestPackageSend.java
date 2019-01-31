package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.illegalaction.enums.IllegalActionType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2WarehouseInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.itemcontainer.ItemContainer;
import l2j.gameserver.model.itemcontainer.inventory.PcFreightManager;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author -Wooden-
 */
public class RequestPackageSend extends AClientPacket
{
	private List<ItemRequest> items = new ArrayList<>();
	private int objectId;
	private int count;
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
		count = readD();
		
		if ((count < 0) || (count > 500))
		{
			count = -1;
			return;
		}
		
		for (int i = 0; i < count; i++)
		{
			int id = readD(); // this is some id sent in PackageSendableList
			int count = readD();
			items.add(new ItemRequest(id, count));
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (count == -1)
		{
			return;
		}
		
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		// why would sb do such a thing?
		if (player.getObjectId() == objectId)
		{
			return;
		}
		
		if (!player.getAccountChars().containsKey(objectId))
		{
			return;
		}
		
		ItemContainer warehouse = player.getActiveWarehouse();
		if (warehouse == null)
		{
			return;
		}
		
		if (player.getActiveEnchantItem() != null)
		{
			IllegalAction.report(player, "Player " + player.getName() + " tried to use enchant Exploit!", IllegalActionType.PUNISH_KICKBAN);
			player.setActiveEnchantItem(null);
			return;
		}
		
		PcFreightManager freight = null;
		if (warehouse instanceof PcFreightManager)
		{
			freight = (PcFreightManager) warehouse;
		}
		
		if (freight == null)
		{
			return;
		}
		
		freight.doQuickRestore(objectId);
		
		if (!(player.getLastTalkNpc() instanceof L2WarehouseInstance))
		{
			return;
		}
		
		L2WarehouseInstance manager = ((L2WarehouseInstance) player.getLastTalkNpc());
		if (((manager == null) || !player.isInsideRadius(manager, L2Npc.INTERACTION_DISTANCE, false, false)) && !player.isGM())
		{
			return;
		}
		
		if (Config.GM_DISABLE_TRANSACTION && (player.isGM()))
		{
			player.sendMessage("Transactions are disabled for your Access Level.");
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (player.getKarma() > 0))
		{
			return;
		}
		
		// Freight price from config or normal price per item slot (30)
		int fee = count * Config.ALT_GAME_FREIGHT_PRICE;
		int currentAdena = player.getInventory().getAdena();
		int slots = 0;
		
		for (ItemRequest i : items)
		{
			int objectId = i.id;
			int count = i.count;
			
			// Check validity of requested item
			ItemInstance item = player.getInventory().checkItemManipulation(objectId, count, "deposit");
			if (item == null)
			{
				LOG.warning("Error depositing a warehouse object for char " + player.getName() + " (validity check)");
				i.id = 0;
				i.count = 0;
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
		
		if (!Config.ALT_GAME_FREIGHTS)
		{
			int region = 1 + MapRegionData.getInstance().getClosestTownNumber(player);
			
			freight.setActiveLocation(region);
		}
		
		// Proceed to the transfer
		for (ItemRequest i : items)
		{
			int objectId = i.id;
			int count = i.count;
			
			// check for an invalid item
			if ((objectId == 0) && (count == 0))
			{
				continue;
			}
			
			ItemInstance oldItem = player.getInventory().getItemByObjectId(objectId);
			if (oldItem == null)
			{
				LOG.warning("Error depositing a warehouse object for char " + player.getName() + " (olditem == null)");
				continue;
			}
			
			if (!oldItem.isAvailable(player, true))
			{
				continue;
			}
			
			ItemInstance newItem = player.getInventory().transferItem("Warehouse", objectId, count, warehouse, player, player.getLastTalkNpc());
			if (newItem == null)
			{
				LOG.warning("Error depositing a warehouse object for char " + player.getName() + " (newitem == null)");
				continue;
			}
		}
		
		// Update current load as well
		player.updateCurLoad();
	}
	
	private class ItemRequest
	{
		public int id;
		public int count;
		
		public ItemRequest(int i, int c)
		{
			id = i;
			count = c;
		}
	}
}
