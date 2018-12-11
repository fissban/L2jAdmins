package l2j.gameserver.scripts.ai.npc.building.castle;

import java.util.StringTokenizer;

import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.instancemanager.sevensigns.enums.SealType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ConditionInteractNpcType;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;

/**
 * Thx Acis
 * @author fissban
 */
public class CastleWyvernManager extends Script
{
	// Npc
	private static final int[] NPCS =
	{
		8162,
		8163,
		8164,
		8165,
		8166,
		8167,
	};
	// Misc
	private static final int NEED_CRYSTALS = 10;
	private static final int REQUIERED_LEVEL = 55;
	// Html
	private static final String HTML_PATH = "data/html/castle/wyvernmanager/";
	
	public CastleWyvernManager()
	{
		super(-1, "ai/npc/castles");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		switch (validateCondition(npc, player))
		{
			case ALL_FALSE:
				sendHtm(npc, player, "0a");
				break;
			
			case CASTLE_OWNER:
				if (player.isFlying() || (player.getPet() != null))
				{
					sendHtm(npc, player, "4");
				}
				else if (player.isClanLeader())
				{
					sendHtm(npc, player, "0");
				}
				else
				{
					sendHtm(npc, player, "2");
				}
				break;
		}
		
		return null;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		StringTokenizer st = new StringTokenizer(event, " ");
		
		switch (st.nextToken())
		{
			case "RideWyvern":
				String val = "2";
				if (player.isClanLeader())
				{
					// Verify if Dusk own Seal of Strife (if true, CLs can't mount wyvern).
					if (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE) == CabalType.DUSK)
					{
						val = "3";
					}
					// If player is mounted on a strider
					else if (player.isMounted() && ((player.getMountNpcId() == 12526) || (player.getMountNpcId() == 12527) || (player.getMountNpcId() == 12528)))
					{
						// Check for strider level
						if (player.getMountLevel() < REQUIERED_LEVEL)
						{
							val = "6";
						}
						// Check for items consumption
						else if (player.getInventory().destroyItemByItemId("Wyvern", 1460, NEED_CRYSTALS, player, true))
						{
							player.dismount();
							
							if (player.mount(12621, 0, true))
							{
								val = "4";
							}
						}
						else
						{
							val = "5";
						}
					}
					else
					{
						player.sendPacket(SystemMessage.YOU_MAY_ONLY_RIDE_WYVERN_WHILE_RIDING_STRIDER);
						val = "1";
					}
				}
				
				sendHtm(npc, player, val);
				break;
			case "Chat":
				if (st.hasMoreTokens())
				{
					sendHtm(npc, player, st.nextToken());
				}
				else
				{
					// Default send you to error HTM.
					sendHtm(npc, player, "1");
				}
				
				break;
			
		}
		
		return null;
	}
	
	private static void sendHtm(L2Npc npc, L2PcInstance player, String val)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(HTML_PATH + "wyvernmanager-" + val + ".htm");
		html.replace("%npcname%", npc.getName());
		html.replace("%wyvern_level%", REQUIERED_LEVEL);
		html.replace("%needed_crystals%", NEED_CRYSTALS);
		player.sendPacket(html);
	}
	
	private static ConditionInteractNpcType validateCondition(L2Npc npc, L2PcInstance player)
	{
		if ((npc.getCastle() != null) && (player.getClan() != null))
		{
			if (npc.getCastle().getSiege().isInProgress())
			{
				return ConditionInteractNpcType.BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
			}
			else if (npc.getCastle().getOwnerId() == player.getClanId())
			{
				return ConditionInteractNpcType.CASTLE_OWNER; // Owner
			}
		}
		
		return ConditionInteractNpcType.ALL_FALSE;
	}
}
