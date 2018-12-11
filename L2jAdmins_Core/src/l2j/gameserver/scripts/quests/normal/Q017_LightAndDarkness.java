package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q017_LightAndDarkness extends Script
{
	// NPCs
	private static final int SAINT_ALTAR_1 = 8508;
	private static final int SAINT_ALTAR_2 = 8509;
	private static final int SAINT_ALTAR_3 = 8510;
	private static final int SAINT_ALTAR_4 = 8511;
	private static final int HIERARCH = 8517;
	// ITEM
	private static final int BLOOD_OF_SAINT = 7168;
	
	public Q017_LightAndDarkness()
	{
		super(17, "Light and Darkness");
		addStartNpc(HIERARCH);
		addTalkId(HIERARCH, SAINT_ALTAR_1, SAINT_ALTAR_2, SAINT_ALTAR_3, SAINT_ALTAR_4);
		registerItems(BLOOD_OF_SAINT);
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
		
		// HIERARCH
		if (event.equalsIgnoreCase("8517-04.htm"))
		{
			st.startQuest();
			st.giveItems(BLOOD_OF_SAINT, 4);
		}
		// SAINT_ALTAR_1
		else if (event.equalsIgnoreCase("8508-02.htm"))
		{
			st.setCond(2, true);
			st.takeItems(BLOOD_OF_SAINT, 1);
		}
		// SAINT_ALTAR_2
		else if (event.equalsIgnoreCase("8509-02.htm"))
		{
			st.setCond(3, true);
			st.takeItems(BLOOD_OF_SAINT, 1);
		}
		// SAINT_ALTAR_3
		else if (event.equalsIgnoreCase("8510-02.htm"))
		{
			st.setCond(4, true);
			st.takeItems(BLOOD_OF_SAINT, 1);
		}
		// SAINT_ALTAR_4
		else if (event.equalsIgnoreCase("8511-02.htm"))
		{
			st.setCond(5, true);
			st.takeItems(BLOOD_OF_SAINT, 1);
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
				htmltext = player.getLevel() < 61 ? "8517-01.htm" : "8517-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case HIERARCH:
						if (cond == 1)
						{
							htmltext = "8517-05.htm";
						}
						else if ((cond > 1) && (cond < 5))
						{
							htmltext = "8517-06.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8517-07.htm";
							st.exitQuest(false, true);
							st.rewardExpAndSp(105527, 0);
						}
						break;
					case SAINT_ALTAR_1:
						if (cond == 1)
						{
							htmltext = st.hasAtLeastOneItem(BLOOD_OF_SAINT) ? "8508-01.htm" : "no_blood.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8508-03.htm";
						}
						break;
					case SAINT_ALTAR_2:
						if (cond == 2)
						{
							htmltext = st.hasAtLeastOneItem(BLOOD_OF_SAINT) ? "8509-01.htm" : "no_blood.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8509-03.htm";
						}
						break;
					case SAINT_ALTAR_3:
						if (cond == 3)
						{
							htmltext = st.hasAtLeastOneItem(BLOOD_OF_SAINT) ? "8510-01.htm" : "no_blood.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8510-03.htm";
						}
						break;
					case SAINT_ALTAR_4:
						if (cond == 4)
						{
							htmltext = st.hasAtLeastOneItem(BLOOD_OF_SAINT) ? "8511-01.htm" : "no_blood.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8511-03.htm";
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
