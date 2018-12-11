package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;

/**
 * Original code in python
 * @author CaFi & fissban
 */
public class Asterios extends Script
{
	// NPC
	private static final int HIERARCH_ASTERIOS = 7154;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Asterios/";
	
	public Asterios()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HIERARCH_ASTERIOS);
		addTalkId(HIERARCH_ASTERIOS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.getRace() != Race.ELF)
		{
			return HTML_PATCH + "7154-11.htm";
		}
		
		switch (player.getClassId())
		{
			case ELF_FIGHTER:
				return HTML_PATCH + "7154-01.htm";
			case ELF_MAGE:
				return HTML_PATCH + "7154-02.htm";
			case ELF_WIZARD:
			case ORACLE:
			case ELF_KNIGHT:
			case SCOUT:
				return HTML_PATCH + "7154-12.htm";
			default:
				return HTML_PATCH + "7154-13.htm";
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "7154-01.htm":
			case "7154-02.htm":
			case "7154-03.htm":
			case "7154-04.htm":
			case "7154-05.htm":
			case "7154-06.htm":
			case "7154-07.htm":
			case "7154-08.htm":
			case "7154-09.htm":
			case "7154-10.htm":
				return HTML_PATCH + event;
		}
		
		return getNoQuestMsg();
	}
}
