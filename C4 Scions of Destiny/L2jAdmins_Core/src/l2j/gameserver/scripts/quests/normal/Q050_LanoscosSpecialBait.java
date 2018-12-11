package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q050_LanoscosSpecialBait extends Script
{
	// NPC
	private static final int LANOSCO = 8570;
	// MOB
	private static final int SINGING_WIND = 1026;
	// ITEM
	private static final int ESSENCE_OF_WIND = 7621;
	// REWARD
	private static final int WIND_FISHING_LURE = 7610;
	
	public Q050_LanoscosSpecialBait()
	{
		super(50, "Lanosco\'s Special Bait");
		addStartNpc(LANOSCO);
		addTalkId(LANOSCO);
		addKillId(SINGING_WIND);
		registerItems(ESSENCE_OF_WIND);
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
		
		// LANOSCO
		if (event.equalsIgnoreCase("8570-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8570-06.htm"))
		{
			if (st.getItemsCount(ESSENCE_OF_WIND) != 100)
			{
				htmltext = "8570-07.htm";
			}
			else
			{
				st.rewardItems(WIND_FISHING_LURE, 4);
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
				htmltext = (player.getLevel() >= 27) && (player.getLevel() <= 29) ? "8570-01.htm" : "8570-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case LANOSCO:
						if (cond == 1)
						{
							htmltext = "8570-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8570-05.htm";
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
			case SINGING_WIND:
				if (st.dropItems(ESSENCE_OF_WIND, 1, 100, 300000))
				{
					st.setCond(2);
				}
				break;
		}
		return null;
	}
}
