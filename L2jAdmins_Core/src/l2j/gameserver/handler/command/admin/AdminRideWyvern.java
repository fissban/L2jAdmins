package l2j.gameserver.handler.command.admin;

import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;

public class AdminRideWyvern implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_ride_wyvern",
		"admin_ride_strider",
		"admin_unride_wyvern",
		"admin_unride_strider",
		"admin_unride",
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		// ----------~ COMMAND ~---------- //
		if (command.startsWith("admin_ride"))
		{
			if (activeChar.isMounted() || (activeChar.getPet() != null))
			{
				activeChar.sendMessage("Already Have a Pet or Mounted.");
				return false;
			}
			
			int PetRideId;
			
			if (command.startsWith("admin_ride_wyvern"))
			{
				PetRideId = 12621;
			}
			else if (command.startsWith("admin_ride_strider"))
			{
				PetRideId = 12526;
			}
			else
			{
				activeChar.sendMessage("Command '" + command + "' not recognized");
				return false;
			}
			
			activeChar.mount(PetRideId, 0, false);
		}
		// ----------~ COMMAND ~---------- //
		else if (command.startsWith("admin_unride"))
		{
			activeChar.dismount();
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
