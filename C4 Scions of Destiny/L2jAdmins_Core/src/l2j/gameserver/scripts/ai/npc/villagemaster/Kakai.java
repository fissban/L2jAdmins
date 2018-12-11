package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;

/**
 * Original code in python
 * @author CaFi
 */
public class Kakai extends Script
{
	// NPC
	private static final int KAKAI_LORD_OF_FLAME = 7565;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Kakai/";
	
	public Kakai()
	{
		super(-1, "ai/npc/villageMaster");
		
		addStartNpc(KAKAI_LORD_OF_FLAME);
		addTalkId(KAKAI_LORD_OF_FLAME);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case ORC_FIGHTER:
				return HTML_PATCH + "7565-01.htm";
			case ORC_MAGE:
				return HTML_PATCH + "7565-06.htm";
			case RAIDER:
			case MONK:
			case SHAMAN:
				return HTML_PATCH + "7565-09.htm";
			case DESTROYER:
			case TYRANT:
			case OVERLORD:
			case WARCRYER:
				return HTML_PATCH + "7565-10.htm";
			default:
				return HTML_PATCH + "7565-11.htm";
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "7565-01.htm":
			case "7565-02.htm":
			case "7565-03.htm":
			case "7565-04.htm":
			case "7565-05.htm":
			case "7565-06.htm":
			case "7565-07.htm":
			case "7565-08.htm":
				return HTML_PATCH + event;
		}
		
		return getNoQuestMsg();
	}
}
