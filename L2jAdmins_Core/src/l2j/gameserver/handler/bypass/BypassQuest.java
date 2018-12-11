package l2j.gameserver.handler.bypass;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.ScriptsData;
import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptEventType;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 */
public class BypassQuest implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"Quest"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!activeChar.validateBypass(command))
		{
			return false;
		}
		
		if (!(target instanceof L2Npc))
		{
			return false;
		}
		
		String quest = command.substring(5).trim();
		
		if (quest.length() == 0)
		{
			showQuestWindow(activeChar, (L2Npc) target);
		}
		else
		{
			int questNameEnd = quest.indexOf(" ");
			if (questNameEnd == -1)
			{
				showQuestWindow(activeChar, (L2Npc) target, quest);
			}
			else
			{
				processQuestEvent(activeChar, quest.substring(0, questNameEnd), quest.substring(questNameEnd).trim());
			}
		}
		return true;
	}
	
	/**
	 * Collect awaiting quests/start points and display a QuestChooseWindow (if several available) or QuestWindow.<br>
	 * @param player The L2PcInstance that talk with the L2NpcInstance
	 * @param npc
	 */
	private static void showQuestWindow(L2PcInstance player, L2Npc npc)
	{
		// collect awaiting quests and start points
		List<Script> options = new ArrayList<>();
		
		// Create a QuestState table that will contain all QuestState to modify
		List<ScriptState> awaits = new ArrayList<>();
		
		// Go through the QuestState of the L2PcInstance quests
		final List<Script> quests = NpcData.getInstance().getTemplate(npc.getTemplate().getId()).getEventScript(ScriptEventType.ON_TALK);
		if (quests != null)
		{
			for (Script quest : quests)
			{
				if (quest != null)
				{
					// Copy the current L2PcInstance QuestState in the QuestState table
					if (player.getScriptState(quest.getName()) != null)
					{
						awaits.add(player.getScriptState(quest.getName()));
					}
				}
			}
		}
		
		// Quests are limited between 1 and 999 because those are the quests that are supported by the client.
		if (!awaits.isEmpty())
		{
			for (ScriptState x : awaits)
			{
				if (!options.contains(x.getQuest()))
				{
					if ((x.getQuest().getId() > 0) && (x.getQuest().getId() < 1000))
					{
						options.add(x.getQuest());
					}
				}
			}
		}
		
		List<Script> starts = npc.getTemplate().getEventScript(ScriptEventType.QUEST_START);
		
		if (starts != null)
		{
			for (Script x : starts)
			{
				if (!options.contains(x))
				{
					if ((x.getId() > 0) && (x.getId() < 1000))
					{
						options.add(x);
					}
				}
			}
		}
		
		// Display a QuestChooseWindow (if several quests are available) or QuestWindow
		if (options.size() > 1)
		{
			showQuestChooseWindow(player, options, npc);
		}
		else if (options.size() == 1)
		{
			showQuestWindow(player, npc, options.get(0).getName());
		}
		else
		{
			showQuestWindow(player, npc, "");
		}
	}
	
	/**
	 * Open a quest window on client with the text of the L2Npc<br>
	 * <b><u>Actions</u>:</b><br>
	 * <ul>
	 * <li>Get the text of the quest state in the folder data/scripts/quests/questId/stateId.htm</li>
	 * <li>Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance</li>
	 * <li>Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet</li>
	 * </ul>
	 * @param player  the L2PcInstance that talk with the {@code npc}
	 * @param npc     the L2NpcInstance that chats with the {@code player}
	 * @param questId the Id of the quest to display the message
	 */
	private static void showQuestWindow(L2PcInstance player, L2Npc npc, String questId)
	{
		Script quest = ScriptsData.get(questId);
		
		if (quest == null)
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(npc.getObjectId());
			npcReply.setHtml(Script.getNoQuestMsg());
			player.sendPacket(npcReply);
			
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (quest.isRealQuest() && ((player.getWeightPenalty() >= 3) || ((player.getInventoryLimit() * 0.8) <= player.getInventory().getSize())))
		{
			player.sendPacket(SystemMessage.INVENTORY_LESS_THAN_80_PERCENT);
			return;
		}
		
		ScriptState qs = player.getScriptState(quest.getName());
		if (qs == null)
		{
			if (quest.isRealQuest() && (player.getAllActiveQuests().size() >= 25))
			{
				NpcHtmlMessage npcReply = new NpcHtmlMessage(npc.getObjectId());
				npcReply.setHtml(Script.getTooMuchQuestsMsg());
				player.sendPacket(npcReply);
				
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			List<Script> qlst = npc.getTemplate().getEventScript(ScriptEventType.QUEST_START);
			if ((qlst != null) && qlst.contains(quest))
			{
				qs = quest.newState(player);
			}
		}
		
		if (qs != null)
		{
			quest.notifyTalk(npc, qs.getPlayer());
		}
	}
	
	/**
	 * Open a choose quest window on client with all quests available of the L2Npc.<br>
	 * <B><U> Actions</U> :</B><br>
	 * <li>Send a Server->Client NpcHtmlMessage containing the text of the L2NpcInstance to the L2PcInstance</li><br>
	 * @param player The L2PcInstance that talk with the L2NpcInstance
	 * @param quests The table containing quests of the L2NpcInstance
	 * @param npc
	 */
	private static void showQuestChooseWindow(L2PcInstance player, List<Script> quests, L2Npc npc)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		
		for (Script q : quests)
		{
			final ScriptState questState = player.getScriptState(q.getName());
			if ((questState == null) || questState.isCreated())
			{
				sb.append("<a action=\"bypass -h Quest " + q.getName() + "\">[" + q.getDescr() + "]</a><br>");
			}
			else if (questState.isStarted())
			{
				sb.append("<a action=\"bypass -h Quest " + q.getName() + "\">[" + q.getDescr() + " (In Progress)]</a><br>");
			}
			else if (questState.isCompleted())
			{
				sb.append("<a action=\"bypass -h Quest " + q.getName() + "\">[" + q.getDescr() + " (Done)]</a><br>");
			}
		}
		
		sb.append("</body></html>");
		// Send a Server->Client packet NpcHtmlMessage to the L2PcInstance in order to display the message of the L2NpcInstance
		player.sendPacket(new NpcHtmlMessage(npc.getObjectId(), sb.toString()));
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public ScriptState processQuestEvent(L2PcInstance activeChar, String questName, String event)
	{
		ScriptState retval = null;
		if (event == null)
		{
			event = "";
		}
		
		ScriptState questState = activeChar.getScriptState(questName);
		final Script quest = ScriptsData.get(questName);
		if ((questState == null) && (event.isEmpty()))
		{
			return retval;
		}
		
		if (questState == null)
		{
			if (quest == null)
			{
				return retval;
			}
			questState = quest.newState(activeChar);
		}
		
		if (questState != null)
		{
			if (activeChar.getLastQuestNpcObject() > 0)
			{
				final L2Object object = L2World.getInstance().getObject(activeChar.getLastQuestNpcObject());
				
				if ((object instanceof L2Npc) && activeChar.isInsideRadius(object, L2Npc.INTERACTION_DISTANCE, false, false))
				{
					final L2Npc npc = (L2Npc) object;
					List<Script> quests = npc.getTemplate().getEventScript(ScriptEventType.ON_TALK);
					if (quests != null)
					{
						for (Script onTalk : quests)
						{
							if ((onTalk == null) || !onTalk.equals(quest))
							{
								continue;
							}
							
							quest.notifyEvent(event, npc, activeChar);
							break;
						}
					}
				}
			}
		}
		
		return retval;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
