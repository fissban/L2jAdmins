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
public class Q009_IntoTheCityOfHumans extends Script
{
	// NPCs
	private static final int TANAPI = 7571;
	private static final int TAMIL = 7576;
	private static final int PETUKAI = 7583;
	// REWARDs
	private static final int SCROLL_OF_ESCAPE_GIRAN = 7559;
	private static final int MARK_OF_TRAVELER = 7570;
	
	public Q009_IntoTheCityOfHumans()
	{
		super(9, "Into the City of Humans");
		addStartNpc(PETUKAI);
		addTalkId(PETUKAI, TANAPI, TAMIL);
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
		
		// PETUKAI
		if (event.equalsIgnoreCase("7583-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7571-02.htm"))
		{
			st.setCond(2, true);
		}
		else if (event.equalsIgnoreCase("7576-02.htm"))
		{
			st.rewardItems(SCROLL_OF_ESCAPE_GIRAN, 1);
			st.rewardItems(MARK_OF_TRAVELER, 1);
			st.exitQuest(false, true);
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
				if ((player.getRace() != Race.ORC) || (player.getLevel() < 3) || (player.getLevel() > 10))
				{
					htmltext = "7583-01.htm";
				}
				else
				{
					htmltext = "7583-02.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case PETUKAI:
						if (cond == 1)
						{
							htmltext = "7583-04.htm";
						}
						break;
					case TANAPI:
						if (cond == 1)
						{
							htmltext = "7571-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7571-03.htm";
						}
						break;
					case TAMIL:
						if (cond == 2)
						{
							htmltext = "7576-01.htm";
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
