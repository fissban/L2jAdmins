package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.data.HtmData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.MultisellData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.NpcWalkerRoutesData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.TeleportLocationData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author fissban
 */
public class AdminReload implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_reload"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();// admin_reload
		
		if (!st.hasMoreTokens())
		{
			return false;
		}
		
		String type = st.nextToken();
		
		// ----------~ COMMAND ~---------- //
		if (type.equals("multisell"))
		{
			MultisellData.getInstance().reload();
			activeChar.sendMessage("Multisell reloaded.");
		}
		// ----------~ COMMAND ~---------- //
		else if (type.startsWith("teleport"))
		{
			TeleportLocationData.getInstance().reload();
			activeChar.sendMessage("Teleport location table reloaded.");
		}
		// ----------~ COMMAND ~---------- //
		else if (type.startsWith("skill"))
		{
			SkillData.getInstance().reload();
			activeChar.sendMessage("Skills reloaded.");
		}
		// ----------~ COMMAND ~---------- //
		else if (type.startsWith("npc"))
		{
			NpcData.getInstance().reload();
			activeChar.sendMessage("Npcs reloaded.");
		}
		// ----------~ COMMAND ~---------- //
		else if (type.startsWith("htm"))
		{
			HtmData.getInstance().reload();
			activeChar.sendMessage("Cache[HTML]: " + HtmData.getInstance().getMemoryUsage() + " megabytes on " + HtmData.getInstance().getLoadedFiles() + " files loaded.");
		}
		// ----------~ COMMAND ~---------- //
		else if (type.startsWith("item"))
		{
			ItemData.getInstance().reload();
			activeChar.sendMessage("Item templates reloaded.");
		}
		// ----------~ COMMAND ~---------- //
		else if (type.startsWith("config"))
		{
			Config.load();
			activeChar.sendMessage("All config settings have been reload");
		}
		// ----------~ COMMAND ~---------- //
		else if (type.startsWith("npcwalkers"))
		{
			NpcWalkerRoutesData.getInstance().load();
			activeChar.sendMessage("All NPC walker routes have been reloaded.");
		}
		// ----------~ COMMAND ~---------- //
		else if (type.startsWith("quest"))
		{
			// ScriptManager.getInstance().reloadAllQuest();
			activeChar.sendMessage("Cant reload quest");
		}
		// ----------~ COMMAND ~---------- //
		else if (type.startsWith("ai"))
		{
			// ScriptManager.getInstance().reloadAllScript();
			activeChar.sendMessage("Cant reload ai");
		}
		// ----------~ COMMAND ~---------- //
		else if (type.startsWith("handler"))
		{
			activeChar.sendMessage("Cant reload handlers");
		}
		else
		{
			activeChar.sendMessage("Usage:  //reload <multisell|skill|npc|htm|item|teleports|config|instancemanager|quest|handler|ai>");
			return false;
		}
		
		AdminHelpPage.showHelpPage(activeChar, "menuReload.htm");
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
