package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;

/**
 * Original code in python
 * @author CaFi
 */
public class Bronk extends Script
{
	// NPC
	private static final int HEAD_BLACKSMITH_BRONK = 7525;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Bronk/";
	
	public Bronk()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HEAD_BLACKSMITH_BRONK);
		addTalkId(HEAD_BLACKSMITH_BRONK);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case DWARF_FIGHTER:
				return HTML_PATCH + "7525-01.htm";
			case ARTISAN:
				return HTML_PATCH + "7525-05.htm";
			case WARSMITH:
				return HTML_PATCH + "7525-06.htm";
			case SCAVENGER:
			default:
				return HTML_PATCH + "7525-07.htm";
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "7525-01.htm":
			case "7525-02.htm":
			case "7525-03.htm":
			case "7525-04.htm":
				return HTML_PATCH + event;
		}
		
		return getNoQuestMsg();
	}
}
