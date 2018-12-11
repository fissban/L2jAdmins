package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 * @author Reynald0
 */
public class Q007_ATripBegins extends Script
{
	// NPCs
	private static final int MIRABEL = 7146;
	private static final int ARIEL = 7148;
	private static final int ASTERIOS = 7154;
	// ITEM
	private static final int ARIELS_RECOMMENDATION = 7572;
	// REWARDs
	private static final int SCROLL_OF_ESCAPE_GIRAN = 7559;
	private static final int MARK_OF_TRAVELER = 7570;
	
	public Q007_ATripBegins()
	{
		super(7, "A Trip Begins");
		addStartNpc(MIRABEL);
		addTalkId(MIRABEL, ARIEL, ASTERIOS);
		registerItems(ARIELS_RECOMMENDATION);
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
		
		// MIRABEL
		if (event.equalsIgnoreCase("7146-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7146-06.htm"))
		{
			st.rewardItems(SCROLL_OF_ESCAPE_GIRAN, 1);
			st.rewardItems(MARK_OF_TRAVELER, 1);
			st.exitQuest(false, true);
		}
		// ARIEL
		else if (event.equalsIgnoreCase("7148-02.htm"))
		{
			st.setCond(2, true);
			st.giveItems(ARIELS_RECOMMENDATION, 1);
		}
		// ASTERIOS
		else if (event.equalsIgnoreCase("7154-02.htm"))
		{
			if (st.hasItems(ARIELS_RECOMMENDATION))
			{
				st.setCond(3, true);
				st.takeItems(ARIELS_RECOMMENDATION);
			}
			else
			{
				htmltext = "7154-04.htm";
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
				if ((player.getRace() != Race.ELF) || (player.getLevel() < 3) || (player.getLevel() > 10))
				{
					htmltext = "7146-01.htm";
				}
				else
				{
					htmltext = "7146-02.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case MIRABEL:
						if (cond == 1)
						{
							htmltext = "7146-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7146-05.htm";
						}
						break;
					case ARIEL:
						if (cond == 1)
						{
							htmltext = "7148-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7148-03.htm";
						}
						break;
					case ASTERIOS:
						if (cond == 2)
						{
							htmltext = "7154-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7154-03.htm";
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
