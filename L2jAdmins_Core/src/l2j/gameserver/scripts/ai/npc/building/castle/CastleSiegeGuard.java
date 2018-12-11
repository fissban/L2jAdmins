package l2j.gameserver.scripts.ai.npc.building.castle;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class CastleSiegeGuard extends Script
{
	private static final int[] NPCS =
	{
		12122, // Gibbson
		12153, // Holmes
		12241, // Sherwood
		12253, // Tyron
		12259, // Ruffor
		12601, // Raybell
		12792,// Daven
	};
	
	public CastleSiegeGuard()
	{
		super(-1, "ai/npc/castle/");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (!npc.getCastle().getSiege().isInProgress())
		{
			npc.getCastle().getSiege().listRegisterClan(player);
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setFile("data/html/building/castle/busy.htm");
			player.sendPacket(html);
		}
		
		return null;
	}
}
