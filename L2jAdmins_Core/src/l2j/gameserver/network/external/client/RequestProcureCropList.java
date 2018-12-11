package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.ManorData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.CropProcureHolder;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * Format: (ch) d [dddd] d: size [ d d d d ]
 * @author l3x
 */
public class RequestProcureCropList extends AClientPacket
{
	private int size;
	private int[] items; // count*4
	
	@Override
	protected void readImpl()
	{
		size = readD();
		if (size > 500)
		{
			size = 0;
			return;
		}
		
		items = new int[size * 4];
		for (int i = 0; i < size; i++)
		{
			int objId = readD();
			items[(i * 4) + 0] = objId;
			int itemId = readD();
			items[(i * 4) + 1] = itemId;
			int manorId = readD();
			items[(i * 4) + 2] = manorId;
			long count = readD();
			if (count > Integer.MAX_VALUE)
			{
				count = Integer.MAX_VALUE;
			}
			items[(i * 4) + 3] = (int) count;
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
		
		L2Object target = player.getTarget();
		
		if (!((target instanceof L2Npc) && ((L2Npc) target).isManor()))
		{
			return;
		}
		
		if (!player.isInsideRadius(target, L2Npc.INTERACTION_DISTANCE, false, false))
		{
			return;
		}
		
		if (size < 1)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		L2Npc manorManager = (L2Npc) target;
		
		int currentManorId = manorManager.getCastle().getId();
		
		// Calculate summary values
		int slots = 0;
		int weight = 0;
		
		for (int i = 0; i < size; i++)
		{
			int itemId = items[(i * 4) + 1];
			int manorId = items[(i * 4) + 2];
			int count = items[(i * 4) + 3];
			
			if ((itemId == 0) || (manorId == 0) || (count == 0))
			{
				continue;
			}
			if (count < 1)
			{
				continue;
			}
			
			if (count > Integer.MAX_VALUE)
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " items at the same time.");
				SystemMessage sm = new SystemMessage(SystemMessage.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				sendPacket(sm);
				return;
			}
			
			try
			{
				CropProcureHolder crop = CastleData.getInstance().getCastleById(manorId).getCrop(itemId, CastleManorManager.PERIOD_CURRENT);
				int rewardItemId = ManorData.getInstance().getRewardItem(itemId, crop.getReward());
				Item template = ItemData.getInstance().getTemplate(rewardItemId);
				weight += count * template.getWeight();
				
				if (!template.isStackable())
				{
					slots += count;
				}
				else if (player.getInventory().getItemById(itemId) == null)
				{
					slots++;
				}
			}
			catch (NullPointerException e)
			{
				continue;
			}
		}
		
		if (!player.getInventory().validateWeight(weight))
		{
			sendPacket(new SystemMessage(SystemMessage.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(new SystemMessage(SystemMessage.SLOTS_FULL));
			return;
		}
		
		// Proceed the purchase
		InventoryUpdate playerIU = new InventoryUpdate();
		
		for (int i = 0; i < size; i++)
		{
			int objId = items[(i * 4) + 0];
			int cropId = items[(i * 4) + 1];
			int manorId = items[(i * 4) + 2];
			int count = items[(i * 4) + 3];
			
			if ((objId == 0) || (cropId == 0) || (manorId == 0) || (count == 0))
			{
				continue;
			}
			
			if (count < 1)
			{
				continue;
			}
			
			CropProcureHolder crop = null;
			
			try
			{
				crop = CastleData.getInstance().getCastleById(manorId).getCrop(cropId, CastleManorManager.PERIOD_CURRENT);
			}
			catch (NullPointerException e)
			{
				continue;
			}
			
			if ((crop == null) || (crop.getId() == 0) || (crop.getPrice() == 0))
			{
				continue;
			}
			
			int fee = 0; // fee for selling to other manors
			
			int rewardItem = ManorData.getInstance().getRewardItem(cropId, crop.getReward());
			
			if (count > crop.getAmount())
			{
				continue;
			}
			
			int sellPrice = (count * ManorData.getInstance().getCropBasicPrice(cropId));
			int rewardPrice = ItemData.getInstance().getTemplate(rewardItem).getReferencePrice();
			if (rewardPrice == 0)
			{
				continue;
			}
			
			int rewardItemCount = sellPrice / rewardPrice;
			if (rewardItemCount < 1)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.FAILED_IN_TRADING_S2_OF_CROP_S1);
				sm.addItemName(cropId);
				sm.addNumber(count);
				player.sendPacket(sm);
				continue;
			}
			
			if (manorId != currentManorId)
			{
				fee = (sellPrice * 5) / 100; // 5% fee for selling to other manor
			}
			
			if (player.getInventory().getAdena() < fee)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.FAILED_IN_TRADING_S2_OF_CROP_S1);
				sm.addItemName(cropId);
				sm.addNumber(count);
				player.sendPacket(sm);
				sm = new SystemMessage(SystemMessage.YOU_NOT_ENOUGH_ADENA);
				player.sendPacket(sm);
				continue;
			}
			
			if (player.getInventory().getItemByObjectId(objId) == null)
			{
				continue;
			}
			
			// check if player have correct items count
			ItemInstance item = player.getInventory().getItemByObjectId(objId);
			if (item.getCount() < count)
			{
				continue;
			}
			
			ItemInstance itemDel = player.getInventory().destroyItem("Manor", objId, count, player, manorManager);
			if (itemDel == null)
			{
				continue;
			}
			
			if (fee > 0)
			{
				player.getInventory().reduceAdena("Manor", fee, player, manorManager);
			}
			crop.setAmount(crop.getAmount() - count);
			if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
			{
				CastleData.getInstance().getCastleById(manorId).updateCrop(crop.getId(), crop.getAmount(), CastleManorManager.PERIOD_CURRENT);
			}
			
			ItemInstance itemAdd = player.getInventory().addItem("Manor", rewardItem, rewardItemCount, player, manorManager);
			
			if (itemAdd == null)
			{
				continue;
			}
			
			playerIU.addRemovedItem(itemDel);
			if (itemAdd.getCount() > rewardItemCount)
			{
				playerIU.addModifiedItem(itemAdd);
			}
			else
			{
				playerIU.addNewItem(itemAdd);
			}
			
			// Send System Messages
			SystemMessage sm = new SystemMessage(SystemMessage.TRADED_S2_OF_CROP_S1);
			sm.addItemName(cropId);
			sm.addNumber(count);
			player.sendPacket(sm);
			
			if (fee > 0)
			{
				sm = new SystemMessage(SystemMessage.S1_ADENA_HAS_BEEN_WITHDRAWN_TO_PAY_FOR_PURCHASING_FEES);
				sm.addNumber(fee);
				player.sendPacket(sm);
			}
			
			sm = new SystemMessage(SystemMessage.S2_S1_DISAPPEARED);
			sm.addItemName(cropId);
			sm.addNumber(count);
			player.sendPacket(sm);
			
			if (fee > 0)
			{
				sm = new SystemMessage(SystemMessage.S1_DISAPPEARED_ADENA);
				sm.addNumber(fee);
				player.sendPacket(sm);
			}
			
			sm = new SystemMessage(SystemMessage.EARNED_S2_S1_S);
			sm.addItemName(rewardItem);
			sm.addNumber(rewardItemCount);
			player.sendPacket(sm);
		}
		
		// Send update packets
		player.sendPacket(playerIU);
		
		// Update current load as well
		player.updateCurLoad();
	}
}
