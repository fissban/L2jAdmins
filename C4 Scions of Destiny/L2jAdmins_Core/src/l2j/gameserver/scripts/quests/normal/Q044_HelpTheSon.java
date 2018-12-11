package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q044_HelpTheSon extends Script
{
	// NPCs
	private static final int DRIKUS = 7505;
	private static final int LUNDY = 7827;
	// ITEMs
	private static final int WORK_HAMMER = 168;
	private static final int BROKEN_GEMSTONE_FRAGMENT = 7552;
	private static final int GOLD_COLORED_GEMSTONE = 7553;
	// MOBs
	private static final int MAILLE_LIZARDMAN = 919;
	private static final int MAILLE_SCOUT = 920;
	private static final int MAILLE_GUARD = 921;
	// REWARD
	private static final int PET_TICKET_KOOKABURRA = 7585;
	
	public Q044_HelpTheSon()
	{
		super(44, "Help the Son!");
		addStartNpc(LUNDY);
		addTalkId(LUNDY, DRIKUS);
		addKillId(MAILLE_GUARD, MAILLE_SCOUT, MAILLE_LIZARDMAN);
		registerItems(BROKEN_GEMSTONE_FRAGMENT, GOLD_COLORED_GEMSTONE);
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
		
		// LUNDY
		if (event.equalsIgnoreCase("7827-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7827-06.htm"))
		{
			if (st.hasItems(WORK_HAMMER))
			{
				st.setCond(2, true);
				st.takeItems(WORK_HAMMER, 1);
			}
			else
			{
				htmltext = "7827-06a.htm";
			}
		}
		else if (event.equalsIgnoreCase("7827-09.htm"))
		{
			st.setCond(4, true);
			st.takeItems(BROKEN_GEMSTONE_FRAGMENT);
			st.giveItems(GOLD_COLORED_GEMSTONE, 1);
		}
		else if (event.equalsIgnoreCase("7827-12.htm"))
		{
			st.rewardItems(PET_TICKET_KOOKABURRA, 1);
			st.exitQuest(false, true);
		}
		// DRIKUS
		else if (event.equalsIgnoreCase("7505-02.htm"))
		{
			st.setCond(5, true);
			st.takeItems(GOLD_COLORED_GEMSTONE);
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
				htmltext = player.getLevel() >= 24 ? "7827-02.htm" : "7827-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case LUNDY:
						if (cond == 1)
						{
							htmltext = st.hasItems(WORK_HAMMER) ? "7827-05.htm" : "7827-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7827-07.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7827-08.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7827-10.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7827-11.htm";
						}
						break;
					case DRIKUS:
						if (cond == 4)
						{
							htmltext = "7505-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7505-03.htm";
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
			case MAILLE_LIZARDMAN:
			case MAILLE_GUARD:
			case MAILLE_SCOUT:
				if (st.dropItems(BROKEN_GEMSTONE_FRAGMENT, 1, 30, 300000))
				{
					st.setCond(3);
				}
				break;
		}
		return null;
	}
}
