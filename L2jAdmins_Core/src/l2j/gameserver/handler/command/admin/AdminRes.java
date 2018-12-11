package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.task.continuous.DecayTaskManager;
import l2j.gameserver.util.audit.GMAudit;

/**
 * This class handles following admin commands: - res = resurrects target L2Character
 * @version $Revision: 1.2.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminRes implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_res",
		"admin_res_monster"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		String target = (activeChar.getTarget() != null) ? activeChar.getTarget().getName() : "no-target";
		GMAudit.auditGMAction(activeChar.getName(), command, target, "");
		
		StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();// actual command
		
		// ----------~ COMMAND ~---------- //
		if (command.startsWith("admin_res "))
		{
			handleRes(activeChar, st.nextToken());
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_res"))
		{
			handleRes(activeChar);
		}
		// ----------~ COMMAND ~---------- //
		else if (command.startsWith("admin_res_monster "))
		{
			handleNonPlayerRes(activeChar, st.nextToken());
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_res_monster"))
		{
			handleNonPlayerRes(activeChar);
		}
		
		return true;
	}
	
	private static void handleRes(L2PcInstance activeChar)
	{
		handleRes(activeChar, null);
	}
	
	private static void handleRes(L2PcInstance activeChar, String resParam)
	{
		L2Object obj = activeChar.getTarget();
		
		if (resParam != null)
		{
			// Check if a player name was specified as a param.
			L2PcInstance plyr = L2World.getInstance().getPlayer(resParam);
			
			if (plyr != null)
			{
				obj = plyr;
			}
			else
			{
				// Otherwise, check if the param was a radius.
				try
				{
					int radius = Integer.parseInt(resParam);
					
					for (L2PcInstance knownPlayer : activeChar.getKnownList().getObjectTypeInRadius(L2PcInstance.class, radius))
					{
						doResurrect(knownPlayer);
					}
					
					activeChar.sendMessage("Resurrected all players within a " + radius + " unit radius.");
					return;
				}
				catch (NumberFormatException e)
				{
					activeChar.sendMessage("Enter a valid player name or radius.");
					return;
				}
			}
		}
		
		if (obj == null)
		{
			obj = activeChar;
		}
		
		doResurrect((L2Character) obj);
	}
	
	/**
	 * @param activeChar
	 */
	private void handleNonPlayerRes(L2PcInstance activeChar)
	{
		handleNonPlayerRes(activeChar, "");
	}
	
	/**
	 * @param activeChar
	 * @param radiusStr
	 */
	private static void handleNonPlayerRes(L2PcInstance activeChar, String radiusStr)
	{
		L2Object obj = activeChar.getTarget();
		
		try
		{
			int radius = 0;
			
			if (!radiusStr.isEmpty())
			{
				radius = Integer.parseInt(radiusStr);
				
				for (L2Character knownChar : activeChar.getKnownList().getObjectTypeInRadius(L2PcInstance.class, radius))
				{
					if (!(knownChar instanceof L2PcInstance))
					{
						doResurrect(knownChar);
					}
				}
				
				activeChar.sendMessage("Resurrected all non-players within a " + radius + " unit radius.");
			}
		}
		catch (NumberFormatException e)
		{
			activeChar.sendMessage("Enter a valid radius.");
			return;
		}
		
		if ((obj == null) || (obj instanceof L2PcInstance))
		{
			activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
			return;
		}
		
		doResurrect((L2Character) obj);
	}
	
	/**
	 * @param targetChar
	 */
	private static void doResurrect(L2Character targetChar)
	{
		if (!targetChar.isDead())
		{
			return;
		}
		
		// If the target is a player, then restore the XP lost on death.
		if (targetChar instanceof L2PcInstance)
		{
			((L2PcInstance) targetChar).restoreExp(100.0);
		}
		else
		{
			DecayTaskManager.getInstance().cancelDecayTask(targetChar);
		}
		
		targetChar.doRevive();
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
