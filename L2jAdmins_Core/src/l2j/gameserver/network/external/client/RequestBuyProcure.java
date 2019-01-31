package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.ManorData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.CropProcureHolder;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SystemMessage;

public class RequestBuyProcure extends AClientPacket
{
	public int listId;
	private int count;
	private int[] items;
	public List<CropProcureHolder> procureList = new ArrayList<>();
	
	@Override
	protected void readImpl()
	{
		listId = readD();
		count = readD();
		if (count > 500) // protect server
		{
			count = 0;
			return;
		}
		
		items = new int[count * 2];
		for (int i = 0; i < count; i++)
		{
			@SuppressWarnings("unused")
			long servise = readD();
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
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getKarma() > 0))
		{
			return;
		}
		
		L2Object target = player.getTarget();
		
		if (count < 1)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Check for buylist validity and calculates summary values
		int slots = 0;
		int weight = 0;
		
		if (target == null)
		{
			return;
		}
		
		L2Npc manor = null;
		
		if ((target instanceof L2Npc) && ((L2Npc) target).isManor())
		{
			manor = (L2Npc) target;
		}
		else
		{
			return;
		}
		
		for (int i = 0; i < count; i++)
		{
			int itemId = items[(i * 2) + 0];
			int count = items[(i * 2) + 1];
			
			if (count > Integer.MAX_VALUE)
			{
				IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Integer.MAX_VALUE + " items at the same time.");
				sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
				return;
			}
			
			Item template = ItemData.getInstance().getTemplate(ManorData.getInstance().getRewardItem(itemId, manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward()));
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
		procureList = manor.getCastle().getCropProcure(CastleManorManager.PERIOD_CURRENT);
		
		for (int i = 0; i < count; i++)
		{
			int itemId = items[(i * 2) + 0];
			int count = items[(i * 2) + 1];
			if (count < 0)
			{
				count = 0;
			}
			
			int rewardItemId = ManorData.getInstance().getRewardItem(itemId, manor.getCastle().getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward());
			
			int rewardItemCount = 1;
			
			rewardItemCount = count / rewardItemCount;
			
			// Add item to Inventory and adjust update packet
			player.getInventory().addItem("Manor", rewardItemId, rewardItemCount, player, manor);
			player.getInventory().destroyItemByItemId("Manor", itemId, count, player, manor);
			
			// Send Char Buy Messages
			SystemMessage sm = new SystemMessage(SystemMessage.EARNED_S2_S1_S);
			sm.addItemName(rewardItemId);
			sm.addNumber(rewardItemCount);
			player.sendPacket(sm);
			sm = null;
		}
		
		// Update current load as well
		player.updateCurLoad();
	}
}
