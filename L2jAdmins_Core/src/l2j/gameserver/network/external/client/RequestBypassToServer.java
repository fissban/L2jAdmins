package l2j.gameserver.network.external.client;

import java.util.logging.Level;

import l2j.Config;
import l2j.gameserver.data.AdminCommandData;
import l2j.gameserver.floodprotector.FloodProtector;
import l2j.gameserver.floodprotector.enums.FloodProtectorType;
import l2j.gameserver.handler.BypassHandler;
import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.handler.CommandAdminHandler;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.handler.CommunityHandler;
import l2j.gameserver.handler.community.AbstractCommunityHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.util.audit.GMAudit;
import main.EngineModsManager;

/**
 * This class ...
 * @version $Revision: 1.12.4.5 $ $Date: 2005/04/11 10:06:11 $
 */
public class RequestBypassToServer extends AClientPacket
{
	private String command;
	
	@Override
	protected void readImpl()
	{
		command = readS();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (!FloodProtector.getInstance().tryPerformAction(activeChar, FloodProtectorType.BYPASS))
		{
			return;
		}
		
		try
		{
			if (command.startsWith("admin_"))
			{
				if (EngineModsManager.onVoiced(activeChar, command))
				{
					return;
				}
				
				if (activeChar.getAccessLevel() < AdminCommandData.getInstance().getAccesCommandAdmin(command.split(" ")[0]))
				{
					activeChar.sendMessage("you do not have permissions to use this command");
					return;
				}
				
				IAdminCommandHandler ach = CommandAdminHandler.getHandler(command);
				if (ach != null)
				{
					if (Config.GMAUDIT)
					{
						GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", command.split(" ")[0], (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target"), "");
					}
					ach.useAdminCommand(command, activeChar);
				}
				else
				{
					LOG.warning("No handler registered for bypass '" + command + "'");
				}
			}
			else if (command.equals("come_here") && (activeChar.isGM()))
			{
				comeHere(activeChar);
			}
			else if (command.startsWith("npc_"))
			{
				if (!activeChar.validateBypass(command))
				{
					return;
				}
				
				int endOfId = command.indexOf('_', 5);
				String id;
				if (endOfId > 0)
				{
					id = command.substring(4, endOfId);
				}
				else
				{
					id = command.substring(4);
				}
				
				try
				{
					L2Object object = L2World.getInstance().getObject(Integer.parseInt(id));
					if ((object != null) && (object instanceof L2Npc) && (endOfId > 0) && activeChar.isInsideRadius(object, L2Npc.INTERACTION_DISTANCE, false, false))
					{
						((L2Npc) object).onBypassFeedback(activeChar, command.substring(endOfId + 1));
					}
					
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch (NumberFormatException nfe)
				{
					//
				}
			}
			else if (command.startsWith("bbs_") || command.startsWith("_bbs") || command.startsWith("_maillist_0_1_0_") || command.startsWith("_friendlist_0_") || command.startsWith("bbs_add_fav"))
			{
				if (EngineModsManager.onCommunityBoard(activeChar, command))
				{
					return;
				}
				
				AbstractCommunityHandler ach = CommunityHandler.getHandler(command);
				if (ach != null)
				{
					ach.parseCmd(command, activeChar);
				}
				else
				{
					LOG.warning("No handler registered for community '" + command + "'");
				}
			}
			else if (command.startsWith("manor_menu_select?"))
			{
				// These bypass are within a retail game window.
				ScriptState qs = activeChar.getScriptState("CastleChamberlain");
				if (qs != null)
				{
					String bypass = command.replace("manor_menu_select?", "manor_menu_select ?");
					qs.getQuest().notifyEvent(bypass, activeChar.getLastTalkNpc(), activeChar);
				}
			}
			else if (command.startsWith("Engine"))
			{
				EngineModsManager.onEvent(activeChar, command.replace("Engine ", ""));
			}
			else
			{
				if (!activeChar.validateBypass(command))
				{
					return;
				}
				
				IBypassHandler ach = BypassHandler.getHandler(command);
				if (ach != null)
				{
					ach.useBypass(command, activeChar, (L2Character) activeChar.getTarget());
				}
				else
				{
					LOG.warning("No handler registered for bypass '" + command + "'");
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, getClient() + " sent bad RequestBypassToServer: \"" + command + "\"", e);
			
			if (activeChar.isGM())
			{
				StringBuilder sb = new StringBuilder(200);
				sb.append("<html><body>");
				sb.append("Bypass error: " + e + "<br1>");
				sb.append("Bypass command: " + command + "<br1>");
				sb.append("StackTrace:<br1>");
				for (StackTraceElement ste : e.getStackTrace())
				{
					sb.append(ste.toString() + "<br1>");
				}
				sb.append("</body></html>");
				// html
				activeChar.sendPacket(new NpcHtmlMessage(0, sb.toString()));
			}
		}
	}
	
	/**
	 * @param activeChar
	 */
	private void comeHere(L2PcInstance activeChar)
	{
		L2Object obj = activeChar.getTarget();
		if (obj instanceof L2Npc)
		{
			L2Npc temp = (L2Npc) obj;
			temp.setTarget(activeChar);
			temp.getAI().setIntention(CtrlIntentionType.MOVE_TO, new LocationHolder(activeChar.getX(), activeChar.getY(), activeChar.getZ(), 0));
		}
	}
}
