package l2j.gameserver.handler.bypass;

import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationTeleportHolder;
import l2j.gameserver.network.external.server.ActionFailed;

/**
 * @author mauronob
 */
public class BypassTeleport implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"teleport",
		"teleportss",
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2Npc))
		{
			return false;
		}
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		if (actualCommand.equalsIgnoreCase("teleport"))
		{
			doTeleport(activeChar, target, new LocationTeleportHolder(0, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), false));
		}
		
		// El teleport del sevensigns reduce ancient adena
		if (actualCommand.equalsIgnoreCase("teleportss"))
		{
			doTeleportSS(activeChar, target, new LocationTeleportHolder(0, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), false));
		}
		
		return true;
	}
	
	private void doTeleport(L2PcInstance player, L2Character target, LocationTeleportHolder list)
	{
		if (Config.ALT_GAME_FREE_TELEPORT || player.getInventory().reduceAdena("Teleport", list.getPrice(), target, true))
		{
			player.teleToLocation(list.getX(), list.getY(), list.getZ(), true);
			return;
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private void doTeleportSS(L2PcInstance player, L2Character target, LocationTeleportHolder list)
	{
		if (Config.ALT_GAME_FREE_TELEPORT || player.getInventory().reduceAncientAdena("Teleport", list.getPrice(), target, true))
		{
			player.teleToLocation(list.getX(), list.getY(), list.getZ(), true);
			return;
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
