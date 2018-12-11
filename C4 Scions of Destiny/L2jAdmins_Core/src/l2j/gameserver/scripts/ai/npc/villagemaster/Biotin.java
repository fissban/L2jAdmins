package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;

/**
 * Original code in python
 * @author fissban
 */
public class Biotin extends Script
{
	// NPC
	private static final int HIGH_PRIEST_BIOTIN = 7031;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Biotin/";
	
	public Biotin()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HIGH_PRIEST_BIOTIN);
		addTalkId(HIGH_PRIEST_BIOTIN);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case WIZARD:
			case CLERIC:
				return HTML_PATCH + "7031-06.htm";
			case SORCERER:
			case NECROMANCER:
			case WARLOCK:
			case BISHOP:
			case PROPHET:
				return HTML_PATCH + "7031-07.htm";
			default:
				return HTML_PATCH + "7031-08.htm";
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "7031-01.htm":
			case "7031-02.htm":
			case "7031-03.htm":
			case "7031-04.htm":
			case "7031-05.htm":
				return HTML_PATCH + event;
		}
		return getNoQuestMsg();
	}
}
