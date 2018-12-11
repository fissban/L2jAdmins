package main.util;

import java.util.Collection;
import java.util.List;

import main.holders.objects.PlayerHolder;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.clan.ClanMemberInstance;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;

/**
 * @author fissban
 */
public class UtilMessage
{
	// XXX CreatureSay ----------------------------------------------------------------------------------------------------------
	
	public static void sendCreatureMsg(L2Character player, SayType say2, String name, String text)
	{
		player.sendPacket(new CreatureSay(say2, name, text));
	}
	
	public static void sendCreatureMsg(PlayerHolder ph, SayType say2, String name, String text)
	{
		ph.getInstance().sendPacket(new CreatureSay(say2, name, text));
	}
	
	public static void sendAllyMembersMsg(int ally, SayType say2, String name, String text)
	{
		for (Clan clan : ClanData.getInstance().getClanAllies(ally))
		{
			sendClanMembersMsg(clan, say2, name, text);
		}
	}
	
	public static void sendClanMembersMsg(Clan clan, SayType say2, String name, String text)
	{
		for (ClanMemberInstance member : clan.getMembers())
		{
			if (member != null && member.isOnline())
			{
				sendCreatureMsg(member.getPlayerInstance(), say2, name, text);
			}
		}
	}
	
	// XXX Announcements ----------------------------------------------------------------------------------------------------
	
	/**
	 * Send Message normal announcement<b>(SayType.ANNOUNCEMENT)</b>
	 * @param text
	 * @param list
	 */
	public static void sendAnnounceMsg(String text, Collection<L2PcInstance> list)
	{
		list.forEach(pc -> sendCreatureMsg(pc, SayType.ANNOUNCEMENT, "Server", text));
	}
	
	/**
	 * Send Message normal announcement<b>(SayType.ANNOUNCEMENT)</b>
	 * @param text
	 * @param list
	 */
	public static void sendAnnounceMsg(String text, PlayerHolder ph)
	{
		sendCreatureMsg(ph.getInstance(), SayType.ANNOUNCEMENT, "Server", text);
	}
	
	/**
	 * Send Message normal announcement<b>(SayType.ANNOUNCEMENT)</b>
	 * @param text
	 * @param list
	 */
	public static void sendAnnounceMsg(String text, List<PlayerHolder> list)
	{
		list.forEach(pc -> sendCreatureMsg(pc, SayType.ANNOUNCEMENT, "Server", text));
	}
}
