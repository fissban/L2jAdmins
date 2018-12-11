package l2j.gameserver.scripts.ai.npc;

import java.util.StringTokenizer;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.TradeControllerData;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.SeedProductionHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.gameserver.network.external.server.BuyList;
import l2j.gameserver.network.external.server.BuyListSeed;
import l2j.gameserver.network.external.server.ExShowCropInfo;
import l2j.gameserver.network.external.server.ExShowManorDefaultInfo;
import l2j.gameserver.network.external.server.ExShowProcureCropDetail;
import l2j.gameserver.network.external.server.ExShowSeedInfo;
import l2j.gameserver.network.external.server.ExShowSellCropList;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class ManorManager extends Script
{
	// Npcs
	private static final int[] NPCS =
	{
		7996,
		7997,
		7998,
		7999,
		8000,
		8058,
		8059,
		8060,
		8402,
		8403
	};
	// Html
	private static final String HTML_PATH = "data/html/manorManager/";
	
	public ManorManager()
	{
		super(-1, "ai/npc");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
		
		for (int npcId : NPCS)
		{
			NpcData.getInstance().getTemplate(npcId).setMerchant(true);
			NpcData.getInstance().getTemplate(npcId).setManor(true);
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (CastleManorManager.getInstance().isDisabled())
		{
			return "data/html/npcdefault.htm";
		}
		
		if (!player.isGM() && (npc.getCastle() != null) && (npc.getCastle().getId() > 0) && (player.getClan() != null) && (npc.getCastle().getOwnerId() == player.getClanId()) && player.isClanLeader())
		{
			return HTML_PATH + "manager-lord.htm";
		}
		
		return HTML_PATH + "manager.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.startsWith("manor_menu_select"))
		{
			if (CastleManorManager.getInstance().isUnderMaintenance())
			{
				player.sendPacket(SystemMessage.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
				return null;
			}
			
			StringTokenizer st = new StringTokenizer(event, " ");
			st.nextToken(); // actual command
			
			String params = event.substring(event.indexOf("?") + 1);
			StringTokenizer str = new StringTokenizer(params, "&");
			int ask = Integer.parseInt(str.nextToken().split("=")[1]);
			int state = Integer.parseInt(str.nextToken().split("=")[1]);
			int time = Integer.parseInt(str.nextToken().split("=")[1]);
			
			int castleId;
			if (state == -1)
			{
				castleId = npc.getCastle().getId();
			}
			else
			{
				// info for requested manor
				castleId = state;
			}
			
			switch (ask)
			{
				case 1: // Seed purchase
					if (castleId != npc.getCastle().getId())
					{
						player.sendPacket(SystemMessage.HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR);
					}
					else
					{
						MerchantTradeList tradeList = new MerchantTradeList(0);
						
						for (SeedProductionHolder s : npc.getCastle().getSeedProduction(CastleManorManager.PERIOD_CURRENT))
						{
							ItemInstance item = ItemData.getInstance().createDummyItem(s.getId());
							item.setPriceToSell(s.getPrice());
							item.setCount(s.getCanProduce());
							if ((item.getCount() > 0) && (item.getPriceToSell() > 0))
							{
								tradeList.addItem(item);
							}
						}
						player.sendPacket(new BuyListSeed(tradeList, castleId, player.getInventory().getAdena()));
					}
					break;
				case 2: // Crop sales
					player.sendPacket(new ExShowSellCropList(player, castleId, npc.getCastle().getCropProcure(CastleManorManager.PERIOD_CURRENT)));
					break;
				case 3: // Current seeds (Manor info)
					if ((time == 1) && !CastleData.getInstance().getCastleById(castleId).isNextPeriodApproved())
					{
						player.sendPacket(new ExShowSeedInfo(castleId, null));
					}
					else
					{
						player.sendPacket(new ExShowSeedInfo(castleId, CastleData.getInstance().getCastleById(castleId).getSeedProduction(time)));
					}
					break;
				case 4: // Current crops (Manor info)
					if ((time == 1) && !CastleData.getInstance().getCastleById(castleId).isNextPeriodApproved())
					{
						player.sendPacket(new ExShowCropInfo(castleId, null));
					}
					else
					{
						player.sendPacket(new ExShowCropInfo(castleId, CastleData.getInstance().getCastleById(castleId).getCropProcure(time)));
					}
					break;
				case 5: // Basic info (Manor info)
					player.sendPacket(new ExShowManorDefaultInfo());
					break;
				case 6: // Buy harvester
					double taxRate = 0;
					player.tempInventoryDisable();
					int value = Integer.parseInt("1" + npc.getId());
					
					MerchantTradeList list = TradeControllerData.getInstance().getBuyList(value);
					
					if (list != null)
					{
						list.getItems().get(0).setCount(1);
						player.sendPacket(new BuyList(list, player.getInventory().getAdena(), taxRate));
					}
					else
					{
						LOG.warning("possible client hacker: " + player.getName() + " attempting to buy from Manor Shop! < Ban him!");
						LOG.warning("buylist id:" + value);
					}
					break;
				
				case 9: // Edit sales (Crop sales)
					player.sendPacket(new ExShowProcureCropDetail(state));
					break;
			}
		}
		else if (event.startsWith("help"))
		{
			StringTokenizer st = new StringTokenizer(event, " ");
			st.nextToken(); // actual command
			return HTML_PATH + "manager-help00" + st.nextToken() + ".htm";
		}
		
		return null;
	}
}
