package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;

/**
 * Original code in python
 * @author fissban
 */
public class Bitz extends Script
{
	// NPC
	private static final int GRAND_MASTER_BITZ = 7026;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Bitz/";
	
	public Bitz()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(GRAND_MASTER_BITZ);
		addTalkId(GRAND_MASTER_BITZ);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case HUMAN_FIGHTER:
				return HTML_PATCH + "7026-01.htm";
			case WARRIOR:
			case KNIGHT:
			case ROGUE:
				return HTML_PATCH + "7026-08.htm";
			case WARLORD:
			case PALADIN:
			case TREASURE_HUNTER:
			case ADVENTURER:
			case HELL_KNIGHT:
			case DREADNOUGHT:
				return HTML_PATCH + "7026-09.htm";
			case GLADIATOR:
			case DARK_AVENGER:
			case HAWKEYE:
			case DUELIST:
			case PHOENIX_KNIGHT:
			case SAGITTARIUS:
				return HTML_PATCH + "7026-09.htm";
			default:
				return HTML_PATCH + "7026-10.htm";
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "7026-01.htm":
			case "7026-02.htm":
			case "7026-03.htm":
			case "7026-04.htm":
			case "7026-05.htm":
			case "7026-06.htm":
			case "7026-07.htm":
				return HTML_PATCH + event;
			default:
				return getNoQuestMsg();
		}
	}
}
