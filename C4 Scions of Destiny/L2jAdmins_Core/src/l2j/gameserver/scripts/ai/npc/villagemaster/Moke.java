package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original code in python
 * @author CaFi
 */
public class Moke extends Script
{
	// NPC
	private static final int WAREHOUSE_CHIEF_MOKE = 7498;
	// ITEM
	private static final int RING_OF_RAVEN_ID = 1642;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Moke/";
	
	public Moke()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(WAREHOUSE_CHIEF_MOKE);
		addTalkId(WAREHOUSE_CHIEF_MOKE);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case DWARF_FIGHTER:
				return HTML_PATCH + "7498-01.htm";
			case SCAVENGER:
			case ARTISAN:
				return HTML_PATCH + "7498-09.htm";
			case BOUNTY_HUNTER:
			case WARSMITH:
				return HTML_PATCH + "7498-10.htm";
			default:
				return HTML_PATCH + "7498-11.htm";
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return null;
		}
		
		switch (event)
		{
			case "7498-01.htm":
			case "7498-02.htm":
			case "7498-03.htm":
			case "7498-04.htm":
				return HTML_PATCH + event;
			
			case "class_change_54":
				if (player.getClassId() == ClassId.DWARF_FIGHTER)
				{
					boolean checkMarks = !st.hasItems(RING_OF_RAVEN_ID);
					
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7498-05.htm";
						}
						return HTML_PATCH + "7498-06.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7498-07.htm";
					}
					
					st.takeItems(RING_OF_RAVEN_ID);
					player.setClassId(54);
					player.setBaseClass(54);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7498-08.htm";
				}
				break;
			
		}
		return getNoQuestMsg();
	}
}
