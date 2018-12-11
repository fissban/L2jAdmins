package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q051_OFullesSpecialBait extends Script
{
	// NPC
	private static final int OFULLE = 8572;
	// MOB
	private static final int FETTERED_SOUL = 552;
	// ITEM
	private static final int LOST_BAIT_INGREDIENT = 7622;
	// REWARD
	private static final int ICY_AIR_FISHING_LURE = 7611;
	
	public Q051_OFullesSpecialBait()
	{
		super(51, "O\'Fulle\'s Special Bait");
		addStartNpc(OFULLE);
		addTalkId(OFULLE);
		addKillId(FETTERED_SOUL);
		registerItems(LOST_BAIT_INGREDIENT);
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
		
		// OFULE
		if (event.equalsIgnoreCase("8572-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8572-06.htm"))
		{
			if (st.getItemsCount(LOST_BAIT_INGREDIENT) != 100)
			{
				htmltext = "8572-07.htm";
			}
			else
			{
				st.rewardItems(ICY_AIR_FISHING_LURE, 4);
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
				htmltext = (player.getLevel() >= 36) && (player.getLevel() <= 38) ? "8572-01.htm" : "8572-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case OFULLE:
						if (cond == 1)
						{
							htmltext = "8572-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8572-05.htm";
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
			case FETTERED_SOUL:
				if (st.dropItems(LOST_BAIT_INGREDIENT, 1, 100, 300000))
				{
					st.setCond(2);
				}
				break;
		}
		return null;
	}
}
