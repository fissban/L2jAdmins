package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q042_HelpTheUncle extends Script
{
	// NPCs
	private static final int SOPHYA = 7735;
	private static final int WATERS = 7828;
	// ITEMs
	private static final int TRIDENT = 291;
	private static final int MAP_PIECE = 7548;
	private static final int YELLOW_MAP = 7549;
	// MOBs
	private static final int MONSTER_EYE_DESTROYER = 68;
	private static final int MONSTER_EYE_GAZER = 266;
	// REWARD
	private static final int PET_TICKET_BUFFALO = 7583;
	
	public Q042_HelpTheUncle()
	{
		super(42, "Help the Uncle!");
		addStartNpc(WATERS);
		addTalkId(WATERS, SOPHYA);
		addKillId(MONSTER_EYE_DESTROYER, MONSTER_EYE_GAZER);
		registerItems(MAP_PIECE, YELLOW_MAP);
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
		
		// WATERS
		if (event.equalsIgnoreCase("7828-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7828-06.htm"))
		{
			if (st.hasItems(TRIDENT))
			{
				st.setCond(2, true);
				st.takeItems(TRIDENT, 1);
			}
			else
			{
				htmltext = "7828-06a.htm";
			}
		}
		else if (event.equalsIgnoreCase("7828-09.htm"))
		{
			st.setCond(4, true);
			st.takeItems(MAP_PIECE);
			st.giveItems(YELLOW_MAP, 1);
		}
		else if (event.equalsIgnoreCase("7828-12.htm"))
		{
			st.rewardItems(PET_TICKET_BUFFALO, 1);
			st.exitQuest(false, true);
		}
		// SOPHYA
		else if (event.equalsIgnoreCase("7735-02.htm"))
		{
			st.setCond(5, true);
			st.takeItems(YELLOW_MAP);
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
				htmltext = player.getLevel() >= 25 ? "7828-02.htm" : "7828-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case WATERS:
						if (cond == 1)
						{
							htmltext = st.hasItems(TRIDENT) ? "7828-05.htm" : "7828-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7828-07.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7828-08.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7828-10.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7828-11.htm";
						}
						break;
					case SOPHYA:
						if (cond == 4)
						{
							htmltext = "7735-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7735-03.htm";
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
		ScriptState st = checkPlayerCondition(killer, npc, "cond", "2");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case MONSTER_EYE_DESTROYER:
			case MONSTER_EYE_GAZER:
				if (st.dropItems(MAP_PIECE, 1, 30, 300000))
				{
					st.setCond(3);
				}
				break;
		}
		return null;
	}
}
