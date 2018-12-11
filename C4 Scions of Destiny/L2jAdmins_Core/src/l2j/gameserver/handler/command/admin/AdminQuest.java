package l2j.gameserver.handler.command.admin;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import l2j.gameserver.data.ScriptsData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.ScripTask;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptEventType;

/**
 * @author fissban
 */
public class AdminQuest implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_show_quests",
		"admin_quest_info"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (activeChar.getTarget() == null)
		{
			activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
			return false;
		}
		if (!(activeChar.getTarget() instanceof L2Npc))
		{
			activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
			return false;
		}
		
		L2Npc npc = L2Npc.class.cast(activeChar.getTarget());
		StringBuilder sb = new StringBuilder();
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		
		StringTokenizer st = new StringTokenizer(command, " ");
		command = st.nextToken();// command
		
		switch (command)
		{
			// ----------~ COMMAND ~---------- //
			case "admin_show_quests":
				
				html.setFile("data/html/admin/npc-quests.htm");
				Set<String> questset = new HashSet<>();
				for (Entry<ScriptEventType, List<Script>> quests : npc.getTemplate().getEventScripts().entrySet())
				{
					for (Script quest : quests.getValue())
					{
						if (questset.contains(quest.getName()))
						{
							continue;
						}
						questset.add(quest.getName());
						sb.append("<tr><td><a action=\"bypass -h admin_quest_info " + quest.getName() + "\">" + quest.getName() + "</a></td></tr>");
					}
				}
				html.replace("%quests%", sb.toString());
				html.replace("%tmplid%", Integer.toString(npc.getTemplate().getId()));
				html.replace("%questName%", "");
				activeChar.sendPacket(html);
				questset.clear();
				
				break;
			// ----------~ COMMAND ~---------- //
			case "admin_quest_info":
				
				if (!st.hasMoreTokens())
				{
					return false;
				}
				
				String questName = st.nextToken();
				
				Script quest = ScriptsData.get(questName);
				if (quest == null)
				{
					return false;
				}
				
				html.setFile("data/html/admin/npc-quests.htm");
				String events = "", npcs = "", items = "", timers = "";
				
				for (ScriptEventType type1 : npc.getTemplate().getEventScripts().keySet())
				{
					events += ", " + type1.toString();
				}
				events = events.substring(2);
				
				if (quest.getQuestInvolvedNpcs().size() < 100)
				{
					for (int npcId : quest.getQuestInvolvedNpcs())
					{
						npcs += ", " + npcId;
					}
					npcs = npcs.substring(2);
				}
				
				if (quest.getRegisterItemsIds() != null)
				{
					for (int itemId : quest.getRegisterItemsIds())
					{
						items += ", " + itemId;
					}
					items = items.substring(2);
				}
				
				for (List<ScripTask> list : quest.getQuestTimers().values())
				{
					for (ScripTask timer : list)
					{
						timers += "<tr><td><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">" + timer.getName() + ":</font> Active: " + timer.isActive() + " Repeatable: " + timer.isRepeating() + " Player: " + timer.getPlayer() + " Npc: " + timer.getNpc()
							+ "</td></tr></table></td></tr>";
					}
				}
				
				sb.append("<tr><td><table width=270 border=0><tr><td width=270><font color=\"LEVEL\">ID:</font> " + quest.getId() + "</td></tr></table></td></tr>");
				sb.append("<tr><td><table width=270 border=0><tr><td width=270><font color=\"LEVEL\">Name:</font> " + quest.getName() + "</td></tr></table></td></tr>");
				sb.append("<tr><td><table width=270 border=0><tr><td width=270><font color=\"LEVEL\">Descr:</font> " + quest.getDescr() + "</td></tr></table></td></tr>");
				sb.append("<tr><td><table width=270 border=0><tr><td width=270><font color=\"LEVEL\">Path:</font> " + quest.getClass().getName().substring(0, quest.getClass().getName().lastIndexOf('.')).replaceAll("\\.", "/") + "</td></tr></table></td></tr>");
				sb.append("<tr><td><table width=270 border=0><tr><td width=270><font color=\"LEVEL\">Events:</font> " + events + "</td></tr></table></td></tr>");
				if (!npcs.isEmpty())
				{
					sb.append("<tr><td><table width=270 border=0><tr><td width=270><font color=\"LEVEL\">NPCs:</font> " + npcs + "</td></tr></table></td></tr>");
				}
				if (!items.isEmpty())
				{
					sb.append("<tr><td><table width=270 border=0><tr><td width=270><font color=\"LEVEL\">Items:</font> " + items + "</td></tr></table></td></tr>");
				}
				if (!timers.isEmpty())
				{
					sb.append("<tr><td><table width=270 border=0><tr><td width=270><font color=\"LEVEL\">Timers:</font></td></tr></table></td></tr>");
					sb.append(timers);
				}
				html.replace("%quests%", sb.toString());
				html.replace("%tmplid%", Integer.toString(npc.getId()));
				html.replace("%questName%", "<table bgcolor=131210><tr><td width=\"270\" align=\"center\"><a action=\"bypass -h admin_reload quest\">Reload Quests</a></td></table>");
				activeChar.sendPacket(html);
				
				break;
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
