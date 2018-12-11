package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original script in python
 * @author fissban
 */
public class Gesto extends Script
{
	// NPC's
	private static final int WAREHOUSE_CHIEF_GESTO = 7511;
	private static final int WAREHOUSE_CHIEF_CROOP = 7676;
	private static final int WAREHOUSE_CHIEF_BRAXT = 7685;
	private static final int WAREHOUSE_CHIEF_KLUMP = 7845;
	private static final int WAREHOUSE_CHIEF_NATOOLS = 7894;
	// ITEM's
	private static final int MARK_OF_SEARCHER_ID = 2809;
	private static final int MARK_OF_GUILDSMAN_ID = 3119;
	private static final int MARK_OF_PROSPERITY_ID = 3238;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Gesto/";
	
	public Gesto()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(WAREHOUSE_CHIEF_GESTO, WAREHOUSE_CHIEF_CROOP, WAREHOUSE_CHIEF_BRAXT, WAREHOUSE_CHIEF_KLUMP, WAREHOUSE_CHIEF_NATOOLS);
		addTalkId(WAREHOUSE_CHIEF_GESTO, WAREHOUSE_CHIEF_CROOP, WAREHOUSE_CHIEF_BRAXT, WAREHOUSE_CHIEF_KLUMP, WAREHOUSE_CHIEF_NATOOLS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case SCAVENGER:
				return HTML_PATCH + "7511-01.htm";
			case DWARF_FIGHTER:
				return HTML_PATCH + "7511-09.htm";
			case BOUNTY_HUNTER:
			case WARSMITH:
				return HTML_PATCH + "7511-10.htm";
			default:
				return HTML_PATCH + "7511-11.htm";
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
			case "7511-01.htm":
			case "7511-02.htm":
			case "7511-03.htm":
			case "7511-04.htm":
				return HTML_PATCH + event;
			
			case "class_change_55":
				if (player.getClassId() == ClassId.SCAVENGER)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SEARCHER_ID, MARK_OF_GUILDSMAN_ID, MARK_OF_PROSPERITY_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7511-05.htm";
						}
						return HTML_PATCH + "7511-06.htm";
					}
					
					if (checkMarks)
					{
						return HTML_PATCH + "7511-07.htm";
					}
					
					st.takeItems(MARK_OF_SEARCHER_ID, MARK_OF_GUILDSMAN_ID, MARK_OF_PROSPERITY_ID);
					player.setClassId(55);
					player.setBaseClass(55);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7511-08.htm";
				}
				
		}
		return getNoQuestMsg();
	}
}
