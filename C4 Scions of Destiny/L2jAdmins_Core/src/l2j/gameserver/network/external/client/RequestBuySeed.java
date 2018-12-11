package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.holder.SeedProductionHolder;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * Format: cdd[dd] c // id (0xC4) d // manor id d // seeds to buy [ d // seed id d // count ]
 * @author l3x
 */
public class RequestBuySeed extends AClientPacket
{
	private int count;
	private int manorId;
	private int[] items; // size count * 2
	
	@Override
	protected void readImpl()
	{
		manorId = readD();
		count = readD();
		
		if (count > 500) // check values
		{
			count = 0;
			return;
		}
		
		items = new int[count * 2];
		
		for (int i = 0; i < count; i++)
		{
			int itemId = readD();
			items[(i * 2) + 0] = itemId;
			long cnt = readD();
			if ((cnt > Integer.MAX_VALUE) || (cnt < 1))
			{
				count = 0;
				items = null;
				return;
			}
			items[(i * 2) + 1] = (int) cnt;
		}
	}
	
	@Override
	public void runImpl()
	{
		long totalPrice = 0;
		int slots = 0;
		int totalWeight = 0;
		
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (count < 1)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		L2Object target = player.getTarget();
		
		if (target == null)
		{
			return;
		}
		
		if (!(target instanceof L2Npc) && ((L2Npc) target).isManor())
		{
			return;
		}
		
		Castle castle = CastleData.getInstance().getCastleById(manorId);
		
		for (int i = 0; i < count; i++)
		{
			int seedId = items[(i * 2) + 0];
			int count = items[(i * 2) + 1];
			int price = 0;
			int residual = 0;
			
			SeedProductionHolder seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
			price = seed.getPrice();
			residual = seed.getCanProduce();
			
			if (price <= 0)
			{
				return;
			}
			
			if (residual < count)
			{
				return;
			}
			
			totalPrice += count * price;
			
			Item template = ItemData.getInstance().getTemplate(seedId);
			totalWeight += count * template.getWeight();
			if (!template.isStackable())
			{
				slots += count;
			}
			else if (player.getInventory().getItemById(seedId) == null)
			{
				slots++;
			}
		}
		
		if (totalPrice > Integer.MAX_VALUE)
		{
			IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " adena worth of goods.");
			return;
		}
		
		if (!player.getInventory().validateWeight(totalWeight))
		{
			sendPacket(new SystemMessage(SystemMessage.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		if (!player.getInventory().validateCapacity(slots))
		{
			sendPacket(new SystemMessage(SystemMessage.SLOTS_FULL));
			return;
		}
		
		// Charge buyer
		if ((totalPrice < 0) || !player.getInventory().reduceAdena("Buy", (int) totalPrice, target, false))
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_NOT_ENOUGH_ADENA));
			return;
		}
		
		// Adding to treasury for Manor Castle
		castle.addToTreasuryNoTax((int) totalPrice);
		
		// Proceed the purchase
		InventoryUpdate playerIU = new InventoryUpdate();
		for (int i = 0; i < count; i++)
		{
			int seedId = items[(i * 2) + 0];
			int count = items[(i * 2) + 1];
			if (count < 0)
			{
				count = 0;
			}
			
			// Update Castle Seeds Amount
			SeedProductionHolder seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
			seed.setCanProduce(seed.getCanProduce() - count);
			if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
			{
				CastleData.getInstance().getCastleById(manorId).updateSeed(seed.getId(), seed.getCanProduce(), CastleManorManager.PERIOD_CURRENT);
			}
			
			// Add item to Inventory and adjust update packet
			ItemInstance item = player.getInventory().addItem("Buy", seedId, count, player, target);
			
			if (item.getCount() > count)
			{
				playerIU.addModifiedItem(item);
			}
			else
			{
				playerIU.addNewItem(item);
			}
			
			// Send Char Buy Messages
			SystemMessage sm = null;
			sm = new SystemMessage(SystemMessage.EARNED_S2_S1_S);
			sm.addItemName(seedId);
			sm.addNumber(count);
			player.sendPacket(sm);
		}
		
		// Send update packets
		player.sendPacket(playerIU);
		
		// Update current load as well
		player.updateCurLoad();
	}
}
