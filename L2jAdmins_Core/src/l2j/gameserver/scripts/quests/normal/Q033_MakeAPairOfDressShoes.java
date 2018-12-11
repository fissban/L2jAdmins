package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q033_MakeAPairOfDressShoes extends Script
{
	// NPCs
	private static final int IAN = 7164;
	private static final int WOODLEY = 7838;
	private static final int LEIKAR = 8520;
	// ITEMs
	private static final int ADENA = 57;
	private static final int THREAD = 1868;
	private static final int LEATHER = 1882;
	// REWARD
	private static final int DRESS_SHOE_BOX = 7113;
	
	public Q033_MakeAPairOfDressShoes()
	{
		super(33, "Make a pair of dress shoes");
		addStartNpc(WOODLEY);
		addTalkId(WOODLEY, IAN, LEIKAR);
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
		
		// WOODLEY
		if (event.equalsIgnoreCase("7838-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7838-05.htm"))
		{
			st.setCond(3, true);
		}
		else if (event.equalsIgnoreCase("7838-08.htm"))
		{
			if ((st.getItemsCount(LEATHER) >= 200) && (st.getItemsCount(THREAD) >= 600) && (st.getItemsCount(ADENA) >= 200000))
			{
				st.setCond(4, true);
				st.takeItems(LEATHER, 200);
				st.takeItems(THREAD, 600);
				st.takeItems(ADENA, 500000);
			}
			else
			{
				htmltext = "7838-06.htm";
			}
		}
		else if (event.equalsIgnoreCase("7838-11.htm"))
		{
			st.rewardItems(DRESS_SHOE_BOX, 1);
			st.exitQuest(false, true);
		}
		// LEIKAR
		else if (event.equalsIgnoreCase("8520-02.htm"))
		{
			st.setCond(2, true);
		}
		else if (event.equalsIgnoreCase("7164-03.htm"))
		{
			if (st.getItemsCount(ADENA) >= 300000)
			{
				st.setCond(5, true);
				st.takeItems(ADENA, 300000);
			}
			else
			{
				htmltext = "7164-01.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		ScriptState st2 = player.getScriptState("Q037_PleaseMakeMeFormalWear");
		String htmltext = getNoQuestMsg();
		if ((st == null) || (st2 == null))
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getLevel() >= 60) && (st2.getCond() >= 7) ? "7838-02.htm" : "7838-01.htm";
				break;
			
			case STARTED:
				int cond = (st.getCond());
				switch (npc.getId())
				{
					case WOODLEY:
						if (cond == 1)
						{
							htmltext = "7838-03.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7838-04.htm";
						}
						else if (cond == 3)
						{
							if ((st.getItemsCount(LEATHER) >= 200) && (st.getItemsCount(THREAD) >= 600) && (st.getItemsCount(ADENA) >= 200000))
							{
								htmltext = "7838-07.htm";
							}
							else
							{
								htmltext = "7838-06.htm";
							}
						}
						else if (cond == 4)
						{
							htmltext = "7838-09.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7838-10.htm";
						}
						break;
					case LEIKAR:
						if (cond == 1)
						{
							htmltext = "8520-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8520-03.htm";
						}
						break;
					case IAN:
						if (cond == 4)
						{
							htmltext = (st.getItemsCount(ADENA) >= 300000) ? "7164-02.htm" : "7164-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7164-04.htm";
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
