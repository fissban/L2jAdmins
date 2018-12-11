package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.SpawnData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.audit.GMAudit;

public class AdminTeleport implements IAdminCommandHandler
{
	// TODO redise�ar por completo esto
	private static final Logger LOG = Logger.getLogger(AdminTeleport.class.getName());
	
	private static final String[] ADMINCOMMAND =
	{
		"admin_teleport_to_character",
		"admin_teleportto",
		"admin_teleport_character",
		"admin_recall",
		"admin_walk",
		"admin_move_to",
		// "admin_explore",
		"admin_recall_npc",
		"admin_teleto",
		"admin_failed"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String event = st.nextToken();// actual command
		
		// Generamos un log con el COMMAND usado por el GM
		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"), "");
		
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_recall_npc"))
		{
			recallNPC(activeChar);
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_teleport_to_character"))
		{
			// teleportToCharacter(activeChar, activeChar.getTarget());
		}
		// ----------~ COMMAND ~---------- //
		// else if (event.equals("admin_explore") && Config.ACTIVATE_POSITION_RECORDER)
		// {
		// activeChar.setExploring(!activeChar.isExploring());
		// activeChar.explore();
		// }
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_walk"))
		{
			try
			{
				final int x = Integer.parseInt(st.nextToken());
				final int y = Integer.parseInt(st.nextToken());
				final int z = Integer.parseInt(st.nextToken());
				
				final LocationHolder pos = new LocationHolder(x, y, z, 0);
				activeChar.getAI().setIntention(CtrlIntentionType.MOVE_TO, pos);
			}
			catch (final Exception e)
			{
				//
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_move_to"))
		{
			try
			{
				final int x = Integer.parseInt(st.nextToken());
				final int y = Integer.parseInt(st.nextToken());
				final int z = Integer.parseInt(st.nextToken());
				teleportTo(activeChar, x, y, z);
			}
			catch (final Exception e)
			{
				// Case of empty co-ordinates
				activeChar.sendMessage("Wrong or no Co-ordinates given.");
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_teleport_character"))
		{
			try
			{
				final int x = Integer.parseInt(st.nextToken());
				final int y = Integer.parseInt(st.nextToken());
				final int z = Integer.parseInt(st.nextToken());
				teleportCharacter(activeChar, x, y, z);
			}
			catch (final Exception e)
			{
				// Case of empty co-ordinates
				activeChar.sendMessage("Wrong or no Co-ordinates given.");
				
				showTeleportCharWindow(activeChar); // back to character teleport
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_teleportto"))
		{
			try
			{
				final L2PcInstance player = L2World.getInstance().getPlayer(st.nextToken());
				teleportToCharacter(activeChar, player);
			}
			catch (final Exception e)
			{
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_recall"))
		{
			try
			{
				final L2PcInstance player = L2World.getInstance().getPlayer(st.nextToken());
				if (player == null)
				{
					activeChar.sendPacket(SystemMessage.TARGET_IS_NOT_FOUND_IN_THE_GAME);
				}
				teleportCharacter(player, activeChar.getX(), activeChar.getY(), activeChar.getZ());
			}
			catch (final Exception e)
			{
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_failed"))
		{
			activeChar.sendMessage("Trying ActionFailed...");
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		}
		
		return true;
	}
	
	/**
	 * @param activeChar
	 * @param x
	 * @param y
	 * @param z
	 */
	private static void teleportTo(L2PcInstance activeChar, int x, int y, int z)
	{
		activeChar.getAI().setIntention(CtrlIntentionType.IDLE);
		activeChar.teleToLocation(x, y, z, false);
		
		activeChar.sendMessage("You have been teleported to " + x + " " + y + " " + z);
	}
	
	/**
	 * @param activeChar
	 */
	private static void showTeleportCharWindow(L2PcInstance activeChar)
	{
		final L2PcInstance target = AdminHelpTarget.getPlayer(activeChar);
		
		if (target == null)
		{
			return;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		
		final StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("Teleport Character");
		sb.append("The character you will teleport is " + target.getName() + ".");
		sb.append("<br>");
		
		sb.append("Co-ordinate x");
		sb.append("<edit var=\"char_cord_x\" width=110>");
		sb.append("Co-ordinate y");
		sb.append("<edit var=\"char_cord_y\" width=110>");
		sb.append("Co-ordinate z");
		sb.append("<edit var=\"char_cord_z\" width=110>");
		sb.append("<button value=\"Teleport\" action=\"bypass -h admin_teleport_character $char_cord_x $char_cord_y $char_cord_z\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("<button value=\"Teleport near you\" action=\"bypass -h admin_teleport_character " + activeChar.getX() + " " + activeChar.getY() + " " + activeChar.getZ() + "\" width=115 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\">");
		sb.append("<center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></center>");
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		activeChar.sendPacket(html);
	}
	
	/**
	 * @param player
	 * @param x
	 * @param y
	 * @param z
	 */
	private static void teleportCharacter(L2PcInstance player, int x, int y, int z)
	{
		// Common character information
		player.sendMessage("Admin is teleporting you.");
		
		player.getAI().setIntention(CtrlIntentionType.IDLE);
		player.teleToLocation(x, y, z, true);
	}
	
	/**
	 * @param activeChar
	 * @param target
	 */
	private static void teleportToCharacter(L2PcInstance activeChar, L2PcInstance target)
	{
		if (target == null)
		{
			return;
		}
		
		if (activeChar.getObjectId() == target.getObjectId())
		{
			activeChar.sendMessage("You cannot self teleport.");
		}
		else
		{
			final int x = target.getX();
			final int y = target.getY();
			final int z = target.getZ();
			
			activeChar.getAI().setIntention(CtrlIntentionType.IDLE);
			activeChar.teleToLocation(x, y, z, true);
			
			activeChar.sendMessage("You have teleported to character " + target.getName() + ".");
		}
	}
	
	/**
	 * @param activeChar
	 */
	private static void recallNPC(L2PcInstance activeChar)
	{
		final L2Object obj = activeChar.getTarget();
		if ((obj != null) && (obj instanceof L2Npc))
		{
			var npc = (L2Npc) obj;
			
			var id = npc.getTemplate().getId();
			var template = NpcData.getInstance().getTemplate(id);
			if (template == null)
			{
				activeChar.sendMessage("Incorrect monster template.");
				LOG.warning("ERROR: NPC " + npc.getObjectId() + " has a 'null' template.");
				return;
			}
			
			var spawn = npc.getSpawn();
			
			if (spawn == null)
			{
				activeChar.sendMessage("Incorrect monster spawn.");
				LOG.warning("ERROR: NPC " + npc.getObjectId() + " has a 'null' spawn.");
				return;
			}
			
			var respawnDelay = spawn.getRespawnDelay();
			
			npc.deleteMe();
			spawn.stopRespawn();
			SpawnData.getInstance().deleteSpawn(spawn, true);
			
			try
			{
				// Generate new spawn
				spawn = new Spawn(template);
				spawn.setX(activeChar.getX());
				spawn.setY(activeChar.getY());
				spawn.setZ(activeChar.getZ());
				spawn.setAmount(1);
				spawn.setHeading(activeChar.getHeading());
				spawn.setRespawnDelay(respawnDelay);
				SpawnData.getInstance().addNewSpawn(spawn, true);
				spawn.init();
				
				activeChar.sendMessage("Created " + template.getName() + " on " + npc.getObjectId() + ".");
				
				if (Config.DEBUG)
				{
					LOG.fine("Spawn at X=" + spawn.getX() + " Y=" + spawn.getY() + " Z=" + spawn.getZ());
					LOG.warning("GM: " + activeChar.getName() + "(" + activeChar.getObjectId() + ") moved NPC " + npc.getObjectId());
				}
			}
			catch (final Exception e)
			{
				activeChar.sendPacket(SystemMessage.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			}
		}
		else
		{
			activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
