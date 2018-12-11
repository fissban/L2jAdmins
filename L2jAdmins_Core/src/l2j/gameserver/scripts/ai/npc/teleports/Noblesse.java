package l2j.gameserver.scripts.ai.npc.teleports;

import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.data.TeleportLocationData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationTeleportHolder;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class Noblesse extends Script
{
	// Npc
	private static final int[] NPCS =
	{
		7006,
		7059,
		7080,
		7134,
		7146,
		7177,
		7233,
		7256,
		7320,
		7540,
		7576,
		7836,
		7848,
		7878,
		7899,
		8275,
		8320,
	};
	// Html
	private static final String HTML_PATH = "data/html/teleporter/noble/";
	
	public Noblesse()
	{
		super(-1, "ai/npc/teleports");
		
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		StringTokenizer st = new StringTokenizer(event, " ");
		String bypass = st.nextToken();
		
		switch (bypass)
		{
			case "tele":
			{
				if (player.isNoble() || player.isGM())
				{
					return HTML_PATH + "noble.htm";
				}
				return HTML_PATH + "noble-no.htm";
			}
			case "HuntingGroundsAdena":
			case "HuntingGrounds":
			{
				if (player.isNoble() || player.isGM())
				{
					return HTML_PATH + "noble" + bypass + npc.getId() + ".htm";
				}
				break;
			}
			case "SevenSignsAdena":
			case "SevenSigns":
			case "AnotherTownAdena":
			case "AnotherTown":
			{
				if (player.isNoble() || player.isGM())
				{
					return HTML_PATH + "noble" + bypass + ".htm";
				}
				break;
			}
			case "gotoNoble": // teleport
			{
				if (!st.hasMoreTokens())
				{
					break;
				}
				
				int id1 = Integer.parseInt(st.nextToken());
				return doTeleportNoble(npc, player, id1, 6651);// Nobles gate pass
			}
			case "gotoNobleAdena":// teleport
			{
				if (!st.hasMoreTokens())
				{
					break;
				}
				
				int id2 = Integer.parseInt(st.nextToken());
				return doTeleportNoble(npc, player, id2, Inventory.ADENA_ID);
			}
		}
		
		return HTML_PATH + "noble-no.htm";
	}
	
	private static String doTeleportNoble(L2Npc npc, L2PcInstance player, int listId, int itemId)
	{
		if (!player.isNoble())
		{
			// Illegal action, possible bypassing!
			return null;
		}
		
		if ((player.getKarma() > 0) && !Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK) // karma
		{
			player.sendMessage("Go away, you're not welcome here.");
			return null;
		}
		
		LocationTeleportHolder list = TeleportLocationData.getInstance().getTemplate(listId);
		
		if (!list.isForNoble())
		{
			return null;
		}
		
		if (Config.ALT_GAME_FREE_TELEPORT || player.getInventory().destroyItemByItemId("Noble Teleport", itemId, list.getPrice(), npc, true))
		{
			player.teleToLocation(list.getX(), list.getY(), list.getZ(), true);
		}
		else
		{
			return HTML_PATH + "noble-noItems.htm";
		}
		
		return null;
	}
}
