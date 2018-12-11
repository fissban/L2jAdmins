package l2j.gameserver.scripts.ai.npc.building.castle;

import java.util.StringTokenizer;

import l2j.gameserver.data.NpcData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ConditionInteractNpcType;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban, zarie
 */
public class CastleMercenaryManager extends Script
{
	private static final int[] NPCS =
	{
		12316, // Greenspan
		12317, // Sanfor
		12318, // Morrison
		12319, // Arvid
		12320, // Eldon
		12321, // Rodd
		12613, // Solinus
		12790,// Rowell
	};
	// Html
	private static final String HTML_PATH = "data/html/castle/mercenaryManager/";
	
	public CastleMercenaryManager()
	{
		super(-1, "ai/npc/castles");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
		
		for (int npcId : NPCS)
		{
			NpcData.getInstance().getTemplate(npcId).setMerchant(true);
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String fileName = "";
		switch (validateCondition(npc, player))
		{
			case ALL_FALSE:
				fileName = HTML_PATH + "mercmanager-no.htm";
				break;
			
			case BUSY_BECAUSE_OF_SIEGE:
				fileName = HTML_PATH + "mercmanager-busy.htm";
				break;
			
			case CASTLE_OWNER:
				fileName = HTML_PATH + "mercmanager.htm";
				break;
		}
		
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(fileName);
		html.replace("%npcId%", String.valueOf(npc.getId()));
		html.replace("%npcname%", npc.getName());
		player.sendPacket(html);
		return null;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (validateCondition(npc, player))
		{
			case ALL_FALSE:
			case BUSY_BECAUSE_OF_SIEGE:
				return null;
			
			case CASTLE_OWNER:
				StringTokenizer st = new StringTokenizer(event, " ");
				String actualCommand = st.nextToken(); // Get actual command
				
				if (actualCommand.equalsIgnoreCase("hire"))
				{
					if (!st.hasMoreTokens())
					{
						return null;
					}
					
					npc.showBuyWindow(player, Integer.parseInt(st.nextToken()));
				}
		}
		
		return null;
	}
	
	private static ConditionInteractNpcType validateCondition(L2Npc npc, L2PcInstance player)
	{
		if ((npc.getCastle() != null) && (player.getClan() != null))
		{
			if (npc.getCastle().getSiege().isInProgress())
			{
				return ConditionInteractNpcType.BUSY_BECAUSE_OF_SIEGE;
			}
			
			if ((npc.getCastle().getOwnerId() == player.getClanId()) && (player.isClanLeader()))
			{
				return ConditionInteractNpcType.CASTLE_OWNER;
			}
		}
		return ConditionInteractNpcType.ALL_FALSE;
	}
}
