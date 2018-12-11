package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q101_SwordOfSolidarity extends Script
{
	// NPC
	private static final int ROIEN = 7008;
	private static final int ALTRAN = 7283;
	// ITEMs
	private static final int BROKEN_SWORD_HANDLE = 739;
	private static final int BROKEN_BLADE_BOTTOM = 740;
	private static final int BROKEN_BLADE_TOP = 741;
	private static final int ROIEN_LETTER = 796;
	private static final int DIRECTION_TO_RUINS = 937;
	// MOBs
	private static final int TUNATH_ORC_MARKSMAN = 361;
	private static final int TUNATH_ORC_WARRIOR = 362;
	// REWARDs
	private static final int SWORD_OF_SOLIDARITY = 738;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	
	public Q101_SwordOfSolidarity()
	{
		super(101, "Sword of Solidarity");
		addStartNpc(ROIEN);
		addTalkId(ROIEN, ALTRAN);
		addKillId(TUNATH_ORC_WARRIOR, TUNATH_ORC_MARKSMAN);
		registerItems(ROIEN_LETTER, DIRECTION_TO_RUINS, BROKEN_BLADE_BOTTOM, BROKEN_BLADE_TOP, BROKEN_SWORD_HANDLE);
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
		
		// ROIEN
		if (event.equalsIgnoreCase("7008-04.htm"))
		{
			st.startQuest();
			st.giveItems(ROIEN_LETTER, 1);
		}
		// ALTRAN
		else if (event.equalsIgnoreCase("7283-02.htm"))
		{
			st.setCond(2, true);
			st.takeItems(ROIEN_LETTER);
			st.giveItems(DIRECTION_TO_RUINS, 1);
		}
		else if (event.equalsIgnoreCase("7283-07.htm"))
		{
			st.giveItems(SOULSHOT_FOR_BEGINNERS, 7000);
			st.giveItems(SWORD_OF_SOLIDARITY, 1);
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
				if (player.getRace() != Race.HUMAN)
				{
					htmltext = "7008-01.htm";
				}
				else if ((player.getLevel() < 9) && (player.getLevel() > 16))
				{
					htmltext = "7008-08.htm";
				}
				else
				{
					htmltext = "7008-02.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case ROIEN:
						if (cond == 1)
						{
							htmltext = "7008-05.htm";
						}
						else if (cond == 2)
						{
							if (st.hasItems(BROKEN_BLADE_BOTTOM, BROKEN_BLADE_TOP))
							{
								htmltext = "7008-12.htm";
							}
							else if (st.hasItems(BROKEN_BLADE_BOTTOM) || st.hasItems(BROKEN_BLADE_TOP))
							{
								htmltext = "7008-11.htm";
							}
							else
							{
								htmltext = "7008-10.htm";
							}
						}
						else if (cond == 4)
						{
							htmltext = "7008-06.htm";
							st.setCond(5, true);
							st.giveItems(BROKEN_SWORD_HANDLE, 1);
						}
						else if (cond == 5)
						{
							htmltext = "7008-07.htm";
						}
						break;
					case ALTRAN:
						if (cond == 1)
						{
							htmltext = "7283-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = st.hasItems(BROKEN_BLADE_BOTTOM) || st.hasItems(BROKEN_BLADE_TOP) ? "7283-08.htm" : "7283-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7283-04.htm";
							st.setCond(4, true);
							st.takeItems(BROKEN_BLADE_BOTTOM);
							st.takeItems(BROKEN_BLADE_TOP);
							st.takeItems(DIRECTION_TO_RUINS);
						}
						else if (cond == 4)
						{
							htmltext = "7283-05.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7283-06.htm";
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
			case TUNATH_ORC_WARRIOR:
			case TUNATH_ORC_MARKSMAN:
				if (st.dropItems(BROKEN_BLADE_BOTTOM, 1, 1, 300000) && st.dropItems(BROKEN_BLADE_TOP, 1, 1, 300000))
				{
					st.setCond(3);
				}
				break;
		}
		return null;
	}
}
