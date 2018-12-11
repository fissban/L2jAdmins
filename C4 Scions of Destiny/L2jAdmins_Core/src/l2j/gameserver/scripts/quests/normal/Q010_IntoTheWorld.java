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
public class Q010_IntoTheWorld extends Script
{
	// NPCs
	private static final int REED = 7520;
	private static final int BALANKI = 7533;
	private static final int GERALDINE = 7650;
	// ITEM
	private static final int VERY_EXPENSIVE_NECKLACE = 7574;
	// Rewards
	private static final int SCROLL_OF_ESCAPE_GIRAN = 7559;
	private static final int MARK_OF_TRAVELER = 7570;
	
	public Q010_IntoTheWorld()
	{
		super(10, "Into the World");
		addStartNpc(BALANKI);
		addTalkId(BALANKI, REED, GERALDINE);
		registerItems(VERY_EXPENSIVE_NECKLACE);
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
		
		// BALANKI
		if (event.equalsIgnoreCase("7533-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7533-06.htm"))
		{
			st.rewardItems(SCROLL_OF_ESCAPE_GIRAN, 1);
			st.rewardItems(MARK_OF_TRAVELER, 1);
			st.exitQuest(false, true);
		}
		// REED
		else if (event.equalsIgnoreCase("7520-02.htm"))
		{
			st.setCond(2, true);
			st.giveItems(VERY_EXPENSIVE_NECKLACE, 1);
		}
		else if (event.equalsIgnoreCase("7520-05.htm"))
		{
			st.setCond(4, true);
		}
		else if (event.equalsIgnoreCase("7650-02.htm"))
		{
			st.setCond(3, true);
			st.takeItems(VERY_EXPENSIVE_NECKLACE);
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
				if ((player.getRace() != Race.DWARF) || (player.getLevel() < 3) || (player.getLevel() > 10))
				{
					htmltext = "7533-01.htm";
				}
				else
				{
					htmltext = "7533-02.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case BALANKI:
						if (cond == 1)
						{
							htmltext = "7533-04.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7533-05.htm";
						}
						break;
					case REED:
						if (cond == 1)
						{
							htmltext = "7520-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7520-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7520-04.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7520-06.htm";
						}
						break;
					case GERALDINE:
						if (cond == 2)
						{
							htmltext = "7650-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7650-03.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7650-04.htm";
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
