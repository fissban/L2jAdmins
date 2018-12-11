package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q052_WilliesSpecialBait extends Script
{
	// NPC
	private static final int WILLIE = 8574;
	// MOB
	private static final int TARLK_BASILISK = 573;
	// ITEM
	private static final int TARLK_EYE = 7623;
	// REWARD
	private static final int EARTH_FISHING_LURE = 7612;
	
	public Q052_WilliesSpecialBait()
	{
		super(52, "Willie\'s Special Bait");
		addStartNpc(WILLIE);
		addTalkId(WILLIE);
		addKillId(TARLK_BASILISK);
		registerItems(TARLK_EYE);
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
			if (st.getItemsCount(TARLK_EYE) != 100)
			{
				htmltext = "8574-07.htm";
			}
			else
			{
				st.rewardItems(EARTH_FISHING_LURE, 4);
				st.exitQuest(false, true);
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
				htmltext = (player.getLevel() >= 48) && (player.getLevel() <= 50) ? "8574-01.htm" : "8574-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case WILLIE:
						if (cond == 1)
						{
							htmltext = "8574-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8574-05.htm";
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
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		ScriptState st = checkPlayerCondition(killer, npc, "cond", "1");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case TARLK_BASILISK:
				if (st.dropItems(TARLK_EYE, 1, 100, 300000))
				{
					st.setCond(2);
				}
				break;
		}
		return null;
	}
}
