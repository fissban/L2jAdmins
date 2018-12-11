package l2j.gameserver.scripts.ai.npc.building;

import java.util.StringTokenizer;

import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.DoorData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ConditionInteractNpcType;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.clan.enums.ClanPrivilegesType;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban, zarie
 */
public class Doorman extends Script
{
	private static final int[] NPCS =
	{
		7488,
		7489,
		7490,
		7492,
		7493,
		7772,
		7773,
		7775,
		7778,
		7779,
		7781,
		7783,
		7785,
		7787,
		7789,
		7791,
		7799,
		7801,
		8151,
		8153,
		8155,
		8157,
		8159,
		8161,
		8352,
		8353,
		8354,
		8355,
		8447,
		8448,
		8449,
		8450,
		8451,
		8676,
		8680,
		12155,
		12156,
		12157,
		12159,
		12160,
		12162,
		12163,
		12164,
		12166,
		12167,
		12243,
		12244,
		12245,
		12247,
		12248,
		12292,
		12293,
		12294,
		12295,
		12296,
		12298,
		12299,
		12322,
		12323,
		12603,
		12604,
		12605,
		12607,
		12608,
		12821,
		12822,
		12823,
		12825,
		12826,
		12827,
		12828,
		12829,
		12830,
		12838,
		12839,
		12840,
		12842,
		12843,
	};
	// html
	private static final String HTML_PATH = "data/html/doorman/";
	
	public Doorman()
	{
		super(-1, "ai/npc/buildings");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ConditionInteractNpcType condition = validateCondition(npc, player);
		
		switch (condition)
		{
			case ALL_FALSE:
			case BUSY_BECAUSE_OF_SIEGE:
				return null;
			
			case CASTLE_OWNER:
				NpcHtmlMessage htmlCastleOwner = new NpcHtmlMessage(npc.getObjectId());
				
				StringTokenizer st = new StringTokenizer(event, " ");
				
				switch (st.nextToken())
				{
					case "Chat":
						htmlCastleOwner.setFile(HTML_PATH + npc.getId() + ".htm");
						player.sendPacket(htmlCastleOwner);
						break;
					
					case "open_doors":
						if (!player.hasClanPrivilege(ClanPrivilegesType.CS_OPEN_DOOR))
						{
							htmlCastleOwner.setFile(HTML_PATH + "door-no-authorized.htm");
							break;
						}
						
						switch (getId())
						{
							case 8680:// Fleming Van Issen
							case 8676:// Gregory Athebaldt
								DoorData.getInstance().getDoor(24170001).openMe();
								DoorData.getInstance().getDoor(24170001).closeMeTask();
								break;
							
							default:
								st.nextToken(); // Bypass first value since its castleid/hallid
								
								while (st.hasMoreTokens())
								{
									npc.getCastle().openCloseDoor(Integer.parseInt(st.nextToken()), true);
								}
								break;
						}
						
						htmlCastleOwner.setFile(HTML_PATH + "door-opened.htm");
						break;
					
					case "close_doors":
						if (!player.hasClanPrivilege(ClanPrivilegesType.CS_OPEN_DOOR))
						{
							htmlCastleOwner.setFile(HTML_PATH + "door-no-authorized.htm");
							break;
						}
						
						switch (getId())
						{
							case 8680:// Fleming Van Issen
							case 8676:// Gregory Athebaldt
								DoorData.getInstance().getDoor(24170001).closeMe();
								break;
							
							default:
								st.nextToken(); // Bypass first value since its castleid/hallid
								
								while (st.hasMoreTokens())
								{
									npc.getCastle().openCloseDoor(Integer.parseInt(st.nextToken()), false);
								}
								break;
						}
						
						htmlCastleOwner.setFile(HTML_PATH + "door-closed.htm");
						break;
				}
				
				player.sendPacket(htmlCastleOwner);
				break;
			
			case HALL_OWNER:
				NpcHtmlMessage htmlHallOwner = new NpcHtmlMessage(npc.getObjectId());
				
				switch (event)
				{
					case "Chat":
						htmlHallOwner.setFile(HTML_PATH + npc.getId() + ".htm");
						break;
					
					case "open_doors":
						if (!player.hasClanPrivilege(ClanPrivilegesType.CH_OPEN_DOOR))
						{
							htmlHallOwner.setFile(HTML_PATH + "door-no-authorized.htm");
						}
						
						npc.getClanHall().openCloseDoors(true);
						htmlHallOwner.setFile(HTML_PATH + "door-opened.htm");
						break;
					
					case "close_doors":
						if (!player.hasClanPrivilege(ClanPrivilegesType.CH_OPEN_DOOR))
						{
							htmlHallOwner.setFile(HTML_PATH + "door-no-authorized.htm");
						}
						
						npc.getClanHall().openCloseDoors(false);
						htmlHallOwner.setFile(HTML_PATH + "door-closed.htm");
						break;
				}
				
				player.sendPacket(htmlHallOwner);
				break;
		}
		
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		
		ConditionInteractNpcType condition = validateCondition(npc, player);
		
		switch (condition)
		{
			case BUSY_BECAUSE_OF_SIEGE:
				html.setFile(HTML_PATH + "busy.htm");
				break;
			
			case CASTLE_OWNER:
				html.setFile(HTML_PATH + npc.getId() + ".htm");
				break;
			
			case HALL_OWNER:
				html.setFile(HTML_PATH + "clanHall-owner.htm");
				html.replace("%playerName%", player.getName());
				break;
			
			case ALL_FALSE:
				// Clan hall
				if (npc.getClanHall() != null)
				{
					Clan clanNpcOwner = ClanData.getInstance().getClanById(npc.getClanHall().getOwnerId());
					if (npc.getClanHall().getOwnerId() <= 0)
					{
						// The clan hall has no owner
						html.setFile(HTML_PATH + "clanHall-no-owner.htm");
						html.replace("%playerName%", player.getName());
						html.replace("%clanHallName%", npc.getClanHall().getName());
					}
					else
					{
						Clan clanPlayer = player.getClan();
						
						if (clanPlayer == null)
						{
							// The character has no Clan
							html.setFile(HTML_PATH + "clanHall-leader.htm");
							html.replace("%leaderName%", clanNpcOwner.getLeader().getName());
							html.replace("%ownerName%", clanNpcOwner.getName());
						}
						else
						{
							// Clan members own clan hall
							if (clanPlayer == clanNpcOwner)
							{
								html.setFile(HTML_PATH + "clanHall-owner.htm");
								html.replace("%playerName%", player.getName());
							}
							else
							{
								// Your clan does not own the clan hall
								html.setFile(HTML_PATH + "clanHall-leader.htm");
								html.replace("%leaderName%", clanNpcOwner.getLeader().getName());
								html.replace("%ownerName%", clanNpcOwner.getName());
							}
						}
					}
				}
				// Castle
				else
				{
					
					html.setFile(HTML_PATH + npc.getId() + "-no.htm");
				}
				
				break;
		}
		
		player.sendPacket(html);
		return null;
	}
	
	private static ConditionInteractNpcType validateCondition(L2Npc npc, L2PcInstance player)
	{
		if (player.getClan() != null)
		{
			// Prepare doormen for clan hall
			if (npc.getClanHall() != null)
			{
				
				if (player.getClanId() == npc.getClanHall().getOwnerId())
				{
					return ConditionInteractNpcType.HALL_OWNER;
				}
				
				return ConditionInteractNpcType.ALL_FALSE;
			}
			if ((npc.getCastle() != null) && (npc.getCastle().getId() > 0))
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
		}
		
		return ConditionInteractNpcType.ALL_FALSE;
	}
}
