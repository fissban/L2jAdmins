package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.data.DoorData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * Comando para abrir/cerrar puertas<br>
 * Contiene los comandos: <br>
 * <li>close
 * <li>closeall
 * <li>open
 * <li>openall
 */
public class AdminDoorControl implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		/** Commands in alphabetical order */
		"admin_close",
		"admin_closeall",
		"admin_open",
		"admin_openall"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();// actual command
		
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_close"))
		{
			if (st.hasMoreTokens())
			{
				try
				{
					int doorId = Integer.parseInt(st.nextToken());
					if (DoorData.getInstance().getDoor(doorId) != null)
					{
						DoorData.getInstance().getDoor(doorId).closeMe();
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Correct command //close <targetDoor>");
				}
			}
			else
			{
				L2Object target = activeChar.getTarget();
				if (target instanceof L2DoorInstance)
				{
					((L2DoorInstance) target).closeMe();
				}
				else
				{
					activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
				}
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_closeall"))
		{
			for (L2DoorInstance door : DoorData.getInstance().getStaticDoors())
			{
				door.closeMe();
			}
			for (L2DoorInstance door : DoorData.getInstance().getCastleDoors())
			{
				door.closeMe();
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_open"))
		{
			if (st.hasMoreTokens())
			{
				try
				{
					int doorId = Integer.parseInt(st.nextToken());
					
					if (DoorData.getInstance().getDoor(doorId) != null)
					{
						DoorData.getInstance().getDoor(doorId).openMe();
					}
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Correct command //open <targetDoor>");
				}
			}
			else
			{
				L2Object target = activeChar.getTarget();
				if (target instanceof L2DoorInstance)
				{
					((L2DoorInstance) target).openMe();
				}
				else
				{
					activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
				}
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_openall"))
		{
			for (L2DoorInstance door : DoorData.getInstance().getStaticDoors())
			{
				door.openMe();
			}
			for (L2DoorInstance door : DoorData.getInstance().getCastleDoors())
			{
				door.openMe();
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
