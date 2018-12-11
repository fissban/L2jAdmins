package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q029_ChestCaughtWithABaitOfEarth extends Script
{
	// NPCs
	private static final int ANABEL = 7909;
	private static final int WILLIE = 8574;
	// ITEMs
	private static final int SMALL_PURPLE_TREASURE_CHEST = 6507;
	private static final int SMALL_GLASS_BOX = 7627;
	// REWARD
	private static final int PLATED_LEATHER_GLOVES = 2455;
	
	public Q029_ChestCaughtWithABaitOfEarth()
	{
		super(29, "Chest caught with a bait of earth");
		addStartNpc(WILLIE);
		addTalkId(WILLIE, ANABEL);
		registerItems(SMALL_GLASS_BOX);
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
		
		// WILLIE
		if (event.equalsIgnoreCase("8574-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8574-06.htm"))
		{
			if (st.hasItems(SMALL_PURPLE_TREASURE_CHEST))
			{
				st.setCond(2, true);
				st.takeItems(SMALL_PURPLE_TREASURE_CHEST);
				st.giveItems(SMALL_GLASS_BOX, 1);
			}
			else
			{
				htmltext = "8574-08.htm";
			}
		}
		// ANABEL
		else if (event.equalsIgnoreCase("7909-02.htm"))
		{
			st.rewardItems(PLATED_LEATHER_GLOVES, 1);
			st.exitQuest(false, true);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		ScriptState st2 = player.getScriptState("Q052_WilliesSpecialBait");
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getLevel() >= 48) && st2.isCompleted() ? "8574-02.htm" : "8574-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case WILLIE:
						if (cond == 1)
						{
							htmltext = st.hasItems(SMALL_PURPLE_TREASURE_CHEST) ? "8574-05.htm" : "8574-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8574-07.htm";
						}
						break;
					case ANABEL:
						if (cond == 2)
						{
							htmltext = "7909-01.htm";
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
