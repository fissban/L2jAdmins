package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.data.AuctionData;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.ClanHallData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.zone.type.ClanHallZone;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class handles all siege commands: Todo: change the class name, and neaten it up
 */
public class AdminSiege implements IAdminCommandHandler
{
	private static String[] adminCommands =
	{
		"admin_siege",
		"admin_add_attacker",
		"admin_add_defender",
		"admin_add_guard",
		"admin_list_siege_clans",
		"admin_clear_siege_list",
		"admin_move_defenders",
		"admin_spawn_doors",
		"admin_endsiege",
		"admin_startsiege",
		"admin_setcastle",
		"admin_clanhall",
		"admin_clanhallset",
		"admin_clanhalldel",
		"admin_clanhallopendoors",
		"admin_clanhallclosedoors",
		"admin_clanhallteleportself"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		command = st.nextToken(); // Get actual command
		
		// Get castle
		Castle castle = null;
		ClanHall clanhall = null;
		if (command.startsWith("admin_clanhall"))
		{
			clanhall = ClanHallData.getClanHallById(Integer.parseInt(st.nextToken()));
		}
		else if (st.hasMoreTokens())
		{
			castle = CastleData.getInstance().getCastleByName(st.nextToken());
		}
		
		// Get castle
		String val = "";
		if (st.hasMoreTokens())
		{
			val = st.nextToken();
		}
		
		if (((castle == null) || (castle.getId() < 0)) && (clanhall == null))
		{
			// No castle specified
			showCastleSelectPage(activeChar);
		}
		else
		{
			L2Object target = activeChar.getTarget();
			L2PcInstance player = null;
			if (target instanceof L2PcInstance)
			{
				player = (L2PcInstance) target;
			}
			
			if (command.equalsIgnoreCase("admin_add_attacker"))
			{
				if (player == null)
				{
					activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
				}
				else
				{
					if (!SiegeManager.getInstance().checkIsRegistered(player.getClan(), castle.getId()))
					{
						castle.getSiege().registerAttacker(player, true);
					}
				}
			}
			else if (command.equalsIgnoreCase("admin_add_defender"))
			{
				if (player == null)
				{
					activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
				}
				else
				{
					if (!SiegeManager.getInstance().checkIsRegistered(player.getClan(), castle.getId()))
					{
						castle.getSiege().registerDefender(player, true);
					}
				}
			}
			else if (command.equalsIgnoreCase("admin_add_guard"))
			{
				if (val != "")
				{
					try
					{
						int npcId = Integer.parseInt(val);
						castle.getSiege().getGuardMngr().addGuard(activeChar, npcId);
					}
					catch (Exception e)
					{
						activeChar.sendMessage("Value entered for Npc Id wasn't an integer");
					}
				}
				else
				{
					activeChar.sendMessage("Missing Npc Id");
				}
			}
			else if (command.equalsIgnoreCase("admin_clear_siege_list"))
			{
				castle.getSiege().clearSiegeClan();
			}
			else if (command.equalsIgnoreCase("admin_endsiege"))
			{
				castle.getSiege().endSiege();
			}
			else if (command.equalsIgnoreCase("admin_list_siege_clans"))
			{
				castle.getSiege().listRegisterClan(activeChar);
				return true;
			}
			else if (command.equalsIgnoreCase("admin_move_defenders"))
			{
				activeChar.sendMessage("Not implemented yet.");
			}
			else if (command.equalsIgnoreCase("admin_setcastle"))
			{
				if ((player == null) || (player.getClan() == null))
				{
					activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
				}
				else
				{
					if (player.getClan().getCastleId() == 0)
					{
						castle.setOwner(player.getClan());
					}
				}
			}
			else if (command.equalsIgnoreCase("admin_clanhallset"))
			{
				if ((player == null) || (player.getClan() == null))
				{
					activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
				}
				else
				{
					if (!player.getClan().hasClanHall())
					{
						if (clanhall.getOwnerId() == 0)
						{
							clanhall.setOwner(player.getClan());
							
							if (AuctionData.getInstance().getAuction(player.getClan().getAuctionBiddedAt()) != null)
							{
								AuctionData.getInstance().getAuction(player.getClan().getAuctionBiddedAt()).cancelBid(player.getClan().getId());
							}
							
							if (AuctionData.getInstance().getAuction(clanhall.getId()) != null)
							{
								if (!AuctionData.getInstance().getAuction(clanhall.getId()).getBidders().isEmpty())
								{
									AuctionData.getInstance().getAuction(clanhall.getId()).removeBids();
								}
								AuctionData.getInstance().getAuction(clanhall.getId()).deleteAuctionFromDB();
							}
						}
					}
				}
			}
			else if (command.equalsIgnoreCase("admin_clanhalldel"))
			{
				if (clanhall.getOwnerId() > 0)
				{
					clanhall.setOwner(null);
				}
			}
			else if (command.equalsIgnoreCase("admin_clanhallopendoors"))
			{
				clanhall.openCloseDoors(true);
			}
			else if (command.equalsIgnoreCase("admin_clanhallclosedoors"))
			{
				clanhall.openCloseDoors(false);
			}
			else if (command.equalsIgnoreCase("admin_clanhallteleportself"))
			{
				if (clanhall != null)
				{
					ClanHallZone zone = clanhall.getZone();
					if (zone != null)
					{
						activeChar.teleToLocation(zone.getSpawnLoc(), true);
					}
				}
			}
			else if (command.equalsIgnoreCase("admin_spawn_doors"))
			{
				castle.spawnDoors();
			}
			else if (command.equalsIgnoreCase("admin_startsiege"))
			{
				castle.getSiege().startSiege();
			}
			
			if (clanhall != null)
			{
				showClanHallPage(activeChar, clanhall);
			}
			else
			{
				showSiegePage(activeChar, castle.getName());
			}
		}
		
		return true;
	}
	
	public void showCastleSelectPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<table width=260><tr>");
		sb.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td width=180><center>Siege Castle ClanHall Menu</center></td>");
		sb.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("</tr></table>");
		sb.append("<center>");
		sb.append("<br>Please select<br1>");
		sb.append("<table width=320><tr>");
		sb.append("<td>Castles:<br></td><td>ClanHalls:<br></td><td></td></tr><tr>");
		sb.append("<td>");
		
		for (Castle castle : CastleData.getInstance().getCastles())
		{
			if (castle != null)
			{
				sb.append("<a action=\"bypass -h admin_siege " + castle.getName() + "\">" + castle.getName() + "</a><br1>");
			}
		}
		sb.append("</td><td>");
		int id = 0;
		for (ClanHall clanhall : ClanHallData.getClanHalls())
		{
			id++;
			if (id > 15)
			{
				sb.append("</td><td>");
				id = 0;
			}
			if (clanhall != null)
			{
				sb.append("<a action=\"bypass -h admin_clanhall " + clanhall.getId() + "\">" + clanhall.getName() + "</a><br1>");
			}
		}
		sb.append("</td></tr></table>");
		sb.append("</center>");
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		activeChar.sendPacket(html);
	}
	
	public void showSiegePage(L2PcInstance activeChar, String castleName)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<table width=260><tr>");
		sb.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td width=180><center>Siege Menu</center></td>");
		sb.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_siege\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("</tr></table>");
		sb.append("<center>");
		sb.append("<br><br><br>Castle: " + castleName + "<br><br>");
		sb.append("<table>");
		sb.append("<tr><td><button value=\"Add Attacker\" action=\"bypass -h admin_add_attacker " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td><button value=\"Add Defender\" action=\"bypass -h admin_add_defender " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		sb.append("<tr><td><button value=\"List Clans\" action=\"bypass -h admin_list_siege_clans " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td><button value=\"Clear List\" action=\"bypass -h admin_clear_siege_list " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table>");
		sb.append("<tr><td><button value=\"Move Defenders\" action=\"bypass -h admin_move_defenders " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td><button value=\"Spawn Doors\" action=\"bypass -h admin_spawn_doors " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table>");
		sb.append("<tr><td><button value=\"Start Siege\" action=\"bypass -h admin_startsiege " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td><button value=\"End Siege\" action=\"bypass -h admin_endsiege " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table>");
		sb.append("<tr><td><button value=\"Give Castle\" action=\"bypass -h admin_setcastle " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table>");
		sb.append("<tr><td>NpcId: <edit var=\"value\" width=40>");
		sb.append("<td><button value=\"Add Guard\" action=\"bypass -h admin_add_guard " + castleName + " $value\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		sb.append("</table>");
		sb.append("</center>");
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		activeChar.sendPacket(html);
	}
	
	public void showClanHallPage(L2PcInstance activeChar, ClanHall clanhall)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<table width=260><tr>");
		sb.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td width=180><center>Siege Menu</center></td>");
		sb.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_siege\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("</tr></table>");
		sb.append("<center>");
		sb.append("<br><br><br>ClanHall: " + clanhall.getName() + "<br>");
		
		Clan owner = ClanData.getInstance().getClanById(clanhall.getOwnerId());
		if (owner == null)
		{
			sb.append("ClanHall Owner: none<br><br>");
		}
		else
		{
			sb.append("ClanHall Owner: " + owner.getName() + "<br><br>");
		}
		
		// replyMSG.append("<table>");
		// replyMSG.append("<tr><td><button value=\" Owner\" action=\"bypass -h admin_clanhallset " + clanhall.getId() + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		// replyMSG.append("</table>");
		sb.append("<br>");
		// replyMSG.append("<td><button value=\"Add Defender\" action=\"bypass -h admin_add_defender " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		// replyMSG.append("<tr><td><button value=\"List Clans\" action=\"bypass -h admin_list_siege_clans " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		// replyMSG.append("<td><button value=\"Clear List\" action=\"bypass -h admin_clear_siege_list " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		// replyMSG.append("<table>");
		// replyMSG.append("<tr><td><button value=\"Move Defenders\" action=\"bypass -h admin_move_defenders " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		// replyMSG.append("<td><button value=\"Spawn Doors\" action=\"bypass -h admin_spawn_doors " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		// replyMSG.append("</table>");
		sb.append("<br>");
		// replyMSG.append("<table>");
		// replyMSG.append("<tr><td><button value=\"Start Siege\" action=\"bypass -h admin_startsiege " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		// replyMSG.append("<td><button value=\"End Siege\" action=\"bypass -h admin_endsiege " + castleName + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		// replyMSG.append("</table>");
		sb.append("<table>");
		sb.append("<tr><td><button value=\"Open Doors\" action=\"bypass -h admin_clanhallopendoors " + clanhall.getId() + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td><button value=\"Close Doors\" action=\"bypass -h admin_clanhallclosedoors " + clanhall.getId() + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table>");
		sb.append("<tr><td><button value=\"Give ClanHall\" action=\"bypass -h admin_clanhallset " + clanhall.getId() + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		sb.append("<td><button value=\"Take ClanHall\" action=\"bypass -h admin_clanhalldel " + clanhall.getId() + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table><tr>");
		// replyMSG.append("<tr><td>NpcId: <edit var=\"value\" width=40>");
		sb.append("<td><button value=\"Teleport self\" action=\"bypass -h admin_clanhallteleportself " + clanhall.getId() + " \" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		sb.append("</table>");
		sb.append("</center>");
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return adminCommands;
	}
}
