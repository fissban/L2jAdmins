package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;

/**
 * Original code in python
 * @author CaFi
 */
public class Thifiell extends Script
{
	// NPC
	private static final int TETRARCH_THIFIELL = 7358;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Thifiell/";
	
	public Thifiell()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(TETRARCH_THIFIELL);
		addTalkId(TETRARCH_THIFIELL);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case DARK_ELF_FIGHTER:
				return HTML_PATCH + "7358-01.htm";
			case DARK_ELF_MAGE:
				return HTML_PATCH + "7358-02.htm";
			case DARK_ELF_WIZARD:
			case SHILLIEN_ORACLE:
			case PALUS_KNIGHT:
			case ASSASSIN:
				return HTML_PATCH + "7358-12.htm";
			default:
				return HTML_PATCH + "7358-13.htm";
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "7358-01.htm":
			case "7358-02.htm":
			case "7358-03.htm":
			case "7358-04.htm":
			case "7358-05.htm":
			case "7358-06.htm":
			case "7358-07.htm":
			case "7358-08.htm":
			case "7358-09.htm":
			case "7358-10.htm":
				return HTML_PATCH + event;
		}
		return getNoQuestMsg();
	}
}
