package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;

/**
 * Original code in python
 * @author CaFi
 */
public class Reed extends Script
{
	// NPC
	private static final int WAREHOUSE_CHIEF_REED = 7520;
	// HTL
	private static final String HTML_PATCH = "data/html/villageMaster/Reed/";
	
	public Reed()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(WAREHOUSE_CHIEF_REED);
		addTalkId(WAREHOUSE_CHIEF_REED);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case DWARF_FIGHTER:
				return HTML_PATCH + "7520-01.htm";
			case SCAVENGER:
			case ARTISAN:
				return HTML_PATCH + "7520-05.htm";
			case BOUNTY_HUNTER:
			case WARSMITH:
				return HTML_PATCH + "7520-06.htm";
			default:
				return HTML_PATCH + "7520-07.htm";
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "7520-01.htm":
			case "7520-02.htm":
			case "7520-03.htm":
			case "7520-04.htm":
				return HTML_PATCH + event;
		}
		return getNoQuestMsg();
	}
}
