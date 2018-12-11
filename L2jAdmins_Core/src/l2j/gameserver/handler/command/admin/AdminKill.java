package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.audit.GMAudit;

/**
 * This class handles following admin commands: - kill = kills target L2Character - kill_monster = kills target non-player - kill <radius> = If radius is specified, then ALL players only in that radius will be killed. - kill_monster <radius> = If radius is specified, then ALL non-players only in
 * that radius will be killed.
 * @version $Revision: 1.2.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminKill implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_kill",
		"admin_kill_monster"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		// Generamos un log con el COMMAND usado por el GM
		GMAudit.auditGMAction(activeChar.getName(), command, (activeChar.getTarget() != null) ? activeChar.getTarget().getName() : "no-target", "");
		
		// ----------~ COMMAND ~---------- //
		if (command.startsWith("admin_kill"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			if (st.hasMoreTokens())
			{
				String firstParam = st.nextToken();
				L2PcInstance plyr = L2World.getInstance().getPlayer(firstParam);
				if (plyr != null)
				{
					if (st.hasMoreTokens())
					{
						try
						{
							int radius = Integer.parseInt(st.nextToken());
							
							for (L2Character knownChar : plyr.getKnownList().getObjectTypeInRadius(L2PcInstance.class, radius))
							{
								if ((knownChar == null) || knownChar.equals(activeChar))
								{
									continue;
								}
								
								kill(activeChar, knownChar);
							}
							
							activeChar.sendMessage("Killed all characters within a " + radius + " unit radius.");
							return true;
						}
						catch (NumberFormatException e)
						{
							activeChar.sendMessage("Invalid radius.");
							return false;
						}
					}
					kill(activeChar, plyr);
				}
				else
				{
					try
					{
						int radius = Integer.parseInt(firstParam);
						
						for (L2Character knownChar : activeChar.getKnownList().getObjectTypeInRadius(L2PcInstance.class, radius))
						{
							if ((knownChar == null) || knownChar.equals(activeChar))
							{
								continue;
							}
							
							kill(activeChar, knownChar);
						}
						
						activeChar.sendMessage("Killed all characters within a " + radius + " unit radius.");
						return true;
					}
					catch (Exception e)
					{
						activeChar.sendMessage("Enter a valid player name or radius.");
						return false;
					}
				}
			}
			else
			{
				L2Object obj = activeChar.getTarget();
				
				if ((obj == null) || !(obj instanceof L2Character) || (obj instanceof L2DoorInstance))
				{
					activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
				}
				else
				{
					kill(activeChar, (L2Character) obj);
				}
			}
		}
		
		return true;
	}
	
	/**
	 * @param activeChar
	 * @param target
	 */
	private static void kill(L2PcInstance activeChar, L2Character target)
	{
		if (target instanceof L2PcInstance)
		{
			target.reduceCurrentHp(target.getStat().getMaxHp() + target.getStat().getMaxCp() + 1, activeChar);
		}
		else
		{
			target.reduceCurrentHp(target.getStat().getMaxHp() + 1, activeChar);
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
