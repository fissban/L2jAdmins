package l2j.gameserver.handler.command.user;

import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.entity.castle.siege.Siege;
import l2j.gameserver.model.zone.type.SiegeZone;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * @author Tryskell
 */
public class UserSiegeStatus implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			99
		};
	}
	
	private static final String INSIDE_SIEGE_ZONE = "Castle Siege in Progress";
	private static final String OUTSIDE_SIEGE_ZONE = "No Castle Siege Area";
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (!activeChar.isNoble() || !activeChar.isClanLeader())
		{
			activeChar.sendMessage("Only a clan leader that is a Noblesse can view the Siege War Status window during a siege war");
			return false;
		}
		
		for (Siege siege : SiegeManager.getInstance().getSieges())
		{
			if (!siege.isInProgress())
			{
				continue;
			}
			
			final Clan clan = activeChar.getClan();
			if (!siege.isAttacker(clan) && !siege.isDefender(clan))
			{
				continue;
			}
			
			final SiegeZone siegeZone = siege.getCastle().getZone();
			final StringBuilder sb = new StringBuilder();
			for (L2PcInstance member : clan.getOnlineMembers())
			{
				sb.append("<tr><td width=170>");
				sb.append(member.getName());
				sb.append("</td><td width=100>");
				sb.append(siegeZone.isInsideZone(member) ? INSIDE_SIEGE_ZONE : OUTSIDE_SIEGE_ZONE);
				sb.append("</td></tr>");
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/siege/siege_status.htm");
			html.replace("%kill_count%", String.valueOf(clan.getSiegeKills()));
			html.replace("%death_count%", String.valueOf(clan.getSiegeDeaths()));
			html.replace("%member_list%", sb.toString());
			activeChar.sendPacket(html);
			
			return true;
		}
		
		activeChar.sendMessage("Only a clan leader that is a Noblesse can view the Siege War Status window during a siege war.");
		
		return false;
	}
}
