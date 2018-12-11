package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author CubicVirtuoso
 * @author fissban
 * @author Reynald0
 */
public class Q008_AnAdventureBegins extends Script
{
	// NPCs
	private static final int JASMINE = 7134;
	private static final int HARNE = 7144;
	private static final int ROSELYN = 7355;
	// ITEM
	private static final int ROSELYNS_NOTE = 7573;
	// REWARDs
	private static final int SCROLL_OF_ESCAPE_GIRAN = 7559;
	private static final int MARK_OF_TRAVELER = 7570;
	
	public Q008_AnAdventureBegins()
	{
		super(8, "An Adventure Begins");
		addStartNpc(JASMINE);
		addTalkId(JASMINE, ROSELYN, HARNE);
		registerItems(ROSELYNS_NOTE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = event;
		if (st == null)
		{
			return htmltext;
		}
		
		// JASMINE
		if (event.equalsIgnoreCase("7134-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7134-06.htm"))
		{
			st.rewardItems(SCROLL_OF_ESCAPE_GIRAN, 1);
			st.rewardItems(MARK_OF_TRAVELER, 1);
			st.exitQuest(false, true);
		}
		// ROSELYN
		else if (event.equalsIgnoreCase("7355-02.htm"))
		{
			st.setCond(2, true);
			st.giveItems(ROSELYNS_NOTE, 1);
		}
		// HARNE
		else if (event.equalsIgnoreCase("7144-02.htm"))
		{
			if (st.hasItems(ROSELYNS_NOTE))
			{
				st.setCond(3, true);
				st.takeItems(ROSELYNS_NOTE, 1);
			}
			else
			{
				htmltext = "7144-03.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if ((player.getRace() != Race.DARK_ELF) || (player.getLevel() < 3) || (player.getLevel() > 10))
				{
					htmltext = "7134-01.htm";
				}
				else
				{
					htmltext = "7134-02.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case JASMINE:
						if (cond == 1)
						{
							htmltext = "7134-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7134-05.htm";
						}
						break;
					case ROSELYN:
						if (cond == 1)
						{
							htmltext = "7355-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7355-03.htm";
						}
						break;
					case HARNE:
						if (cond == 2)
						{
							htmltext = "7144-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7144-04.htm";
						}
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
}
