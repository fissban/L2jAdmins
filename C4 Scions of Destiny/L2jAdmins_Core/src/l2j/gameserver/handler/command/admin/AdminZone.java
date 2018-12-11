package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.enums.ZoneType;

public class AdminZone implements IAdminCommandHandler
{
	public static final String[] ADMINCOMMAND =
	{
		"admin_zone_check"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		command = st.nextToken(); // Get actual command
		
		if (command.equalsIgnoreCase("admin_zone_check"))
		{
			if (activeChar.isInsideZone(ZoneType.PVP))
			{
				activeChar.sendMessage("This is a PvP zone.");
			}
			if (activeChar.isInsideZone(ZoneType.NOLANDING))
			{
				activeChar.sendMessage("This is a non-landing zone.");
			}
			if (activeChar.isInsideZone(ZoneType.PEACE))
			{
				activeChar.sendMessage("This is a Peace zone.");
			}
			if (activeChar.isInsideZone(ZoneType.SIEGE))
			{
				activeChar.sendMessage("This is a Siege zone.");
			}
			if (activeChar.isInsideZone(ZoneType.MOTHERTREE))
			{
				activeChar.sendMessage("This is a Mother Tree zone.");
			}
			if (activeChar.isInsideZone(ZoneType.CLANHALL))
			{
				activeChar.sendMessage("This is a Clan Hall zone.");
			}
			if (activeChar.isInsideZone(ZoneType.WATER))
			{
				activeChar.sendMessage("This is a Water zone.");
			}
			if (activeChar.isInsideZone(ZoneType.JAIL))
			{
				activeChar.sendMessage("This is a Jail zone.");
			}
			if (activeChar.isInsideZone(ZoneType.MONSTERTRACK))
			{
				activeChar.sendMessage("This is a Monster Track zone.");
			}
			if (activeChar.isInsideZone(ZoneType.NOHQ))
			{
				activeChar.sendMessage("This is a Castle zone.");
			}
			if (activeChar.isInsideZone(ZoneType.UNUSED))
			{
				activeChar.sendMessage("This zone is not used.");
			}
			if (activeChar.isInsideZone(ZoneType.BOSS))
			{
				activeChar.sendMessage("This is a Boss zone.");
			}
			if (activeChar.isInsideZone(ZoneType.EFFECT))
			{
				activeChar.sendMessage("This is an Effect zone.");
			}
			
			activeChar.sendMessage("Closest Town: " + MapRegionData.getInstance().getClosestTownName(activeChar));
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
