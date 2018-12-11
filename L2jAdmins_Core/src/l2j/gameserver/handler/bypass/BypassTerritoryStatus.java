package l2j.gameserver.handler.bypass;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * @author fissban
 */
public class BypassTerritoryStatus implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"TerritoryStatus"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2Npc))
		{
			return false;
		}
		
		L2Npc npc = (L2Npc) target;
		
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile("data/html/territorystatus.htm");
		html.replace("%objectId%", String.valueOf(npc.getObjectId()));
		html.replace("%npcname%", npc.getName());
		
		if (npc.getIsInCastleTown())
		{
			html.replace("%castlename%", npc.getCastle().getName());
			html.replace("%taxpercent%", "" + npc.getCastle().getTaxPercent());
			
			if (npc.getCastle().getOwnerId() > 0)
			{
				Clan clan = ClanData.getInstance().getClanById(npc.getCastle().getOwnerId());
				html.replace("%clanname%", clan.getName());
				html.replace("%clanleadername%", clan.getLeaderName());
			}
			else
			{
				html.replace("%clanname%", "NPC");
				html.replace("%clanleadername%", "NPC");
			}
		}
		else
		{
			html.replace("%castlename%", "Open");
			html.replace("%taxpercent%", "0");
			
			html.replace("%clanname%", "No");
			html.replace("%clanleadername%", "None");
		}
		
		activeChar.sendPacket(html);
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
