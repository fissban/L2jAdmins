package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q053_LinnaeusSpecialBait extends Script
{
	// NPC
	private static final int LINNAEUS = 8577;
	// MOB
	private static final int CRIMSON_DRAKE = 670;
	// ITEM
	private static final int CRIMSON_DRAKE_HEART = 7624;
	// REWARD
	private static final int FLAMING_FISHING_LURE = 7613;
	
	public Q053_LinnaeusSpecialBait()
	{
		super(53, "Linnaeus\' Special Bait");
		addStartNpc(LINNAEUS);
		addTalkId(LINNAEUS);
		addKillId(CRIMSON_DRAKE);
		registerItems(CRIMSON_DRAKE_HEART);
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
		
		// LINNAEUS
		if (event.equalsIgnoreCase("8577-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8577-06.htm"))
		{
			if (st.getItemsCount(CRIMSON_DRAKE_HEART) != 100)
			{
				htmltext = "8577-07.htm";
			}
			else
			{
				st.rewardItems(FLAMING_FISHING_LURE, 4);
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
				htmltext = (player.getLevel() >= 60) && (player.getLevel() <= 62) ? "8577-01.htm" : "8577-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case LINNAEUS:
						if (cond == 1)
						{
							htmltext = "8577-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8577-05.htm";
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
			case CRIMSON_DRAKE:
				if (st.dropItems(CRIMSON_DRAKE_HEART, 1, 100, 300000))
				{
					st.setCond(2);
				}
				break;
		}
		return null;
	}
}
