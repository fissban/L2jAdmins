package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q016_TheComingDarkness extends Script
{
	// NPCs
	private static final int EVIL_ALTAR_1 = 8512;
	private static final int EVIL_ALTAR_2 = 8513;
	private static final int EVIL_ALTAR_3 = 8514;
	private static final int EVIL_ALTAR_4 = 8515;
	private static final int EVIL_ALTAR_5 = 8516;
	private static final int HIERARCH = 8517;
	// ITEM
	private static final int CRYSTAL_OF_SEAL = 7167;
	
	public Q016_TheComingDarkness()
	{
		super(16, "The Coming Darkness");
		addStartNpc(HIERARCH);
		addTalkId(HIERARCH, EVIL_ALTAR_1, EVIL_ALTAR_2, EVIL_ALTAR_3, EVIL_ALTAR_4, EVIL_ALTAR_5);
		registerItems(CRYSTAL_OF_SEAL);
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
			st.giveItems(CRYSTAL_OF_SEAL, 5);
		}
		// EVIL_ALTAR_1
		else if (event.equalsIgnoreCase("8512-02.htm"))
		{
			if (st.hasItems(CRYSTAL_OF_SEAL))
			{
				st.setCond(2, true);
				st.takeItems(CRYSTAL_OF_SEAL, 1);
			}
			else
			{
				htmltext = "altar_nocrystal.htm";
			}
		}
		// EVIL_ALTAR_2
		else if (event.equalsIgnoreCase("8513-02.htm"))
		{
			if (st.hasItems(CRYSTAL_OF_SEAL))
			{
				st.setCond(3, true);
				st.takeItems(CRYSTAL_OF_SEAL, 1);
			}
			else
			{
				htmltext = "altar_nocrystal.htm";
			}
		}
		// EVIL_ALTAR_3
		else if (event.equalsIgnoreCase("8514-02.htm"))
		{
			if (st.hasItems(CRYSTAL_OF_SEAL))
			{
				st.setCond(4, true);
				st.takeItems(CRYSTAL_OF_SEAL, 1);
			}
			else
			{
				htmltext = "altar_nocrystal.htm";
			}
		}
		// EVIL_ALTAR_4
		else if (event.equalsIgnoreCase("8515-02.htm"))
		{
			if (st.hasItems(CRYSTAL_OF_SEAL))
			{
				st.setCond(5, true);
				st.takeItems(CRYSTAL_OF_SEAL, 1);
			}
			else
			{
				htmltext = "altar_nocrystal.htm";
			}
		}
		// EVIL_ALTAR_5
		else if (event.equalsIgnoreCase("8516-02.htm"))
		{
			if (st.hasItems(CRYSTAL_OF_SEAL))
			{
				st.setCond(6, true);
				st.takeItems(CRYSTAL_OF_SEAL, 1);
			}
			else
			{
				htmltext = "altar_nocrystal.htm";
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
				htmltext = player.getLevel() < 62 ? "8517-01.htm" : "8517-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case HIERARCH:
						if ((cond >= 1) && (cond < 6))
						{
							if (st.hasItems(CRYSTAL_OF_SEAL))
							{
								htmltext = "8517-05.htm";
							}
							else
							{
								htmltext = "8517-07.htm";
								st.exitQuest(false);
							}
						}
						else if (cond == 6)
						{
							htmltext = "8517-06.htm";
							st.rewardExpAndSp(221958, 0);
							st.exitQuest(false, true);
						}
						break;
					case EVIL_ALTAR_1:
						if (cond == 1)
						{
							htmltext = "8512-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8512-03.htm";
						}
						break;
					case EVIL_ALTAR_2:
						if (cond == 2)
						{
							htmltext = "8513-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8513-03.htm";
						}
						break;
					case EVIL_ALTAR_3:
						if (cond == 3)
						{
							htmltext = "8514-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8514-03.htm";
						}
						break;
					case EVIL_ALTAR_4:
						if (cond == 4)
						{
							htmltext = "8515-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8515-03.htm";
						}
						break;
					case EVIL_ALTAR_5:
						if (cond == 5)
						{
							htmltext = "8516-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "8516-03.htm";
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
