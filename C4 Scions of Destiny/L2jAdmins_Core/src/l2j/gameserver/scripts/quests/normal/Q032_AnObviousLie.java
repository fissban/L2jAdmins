package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q032_AnObviousLie extends Script
{
	// NPCs
	private static final int GENTLER = 7094;
	private static final int MAXIMILIAN = 7120;
	private static final int MIKI_THE_CAT = 8706;
	// MOB
	private static final int ALLIGATOR = 135;
	// ITEMs
	private static final int SUEDE = 1866;
	private static final int THREAD = 1868;
	private static final int SPIRIT_ORES = 3031;
	private static final int MAP = 7165;
	private static final int MEDICINAL_HERB = 7166;
	// REWARDs
	private static final int CAT_EAR = 6843;
	private static final int RACCOON_EAR = 7680;
	private static final int RABBIT_EAR = 7683;
	
	public Q032_AnObviousLie()
	{
		super(32, "An Obvious Lie");
		addStartNpc(MAXIMILIAN);
		addTalkId(MAXIMILIAN, GENTLER, MIKI_THE_CAT);
		addKillId(ALLIGATOR);
		registerItems(MEDICINAL_HERB, MAP);
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
		
		// MAXIMILIAM
		if (event.equalsIgnoreCase("7120-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7094-02.htm"))
		{
			st.setCond(2, true);
			st.giveItems(MAP, 1);
		}
		else if (event.equalsIgnoreCase("7094-05.htm"))
		{
			st.setCond(5, true);
			st.takeItems(MEDICINAL_HERB);
		}
		else if (event.equalsIgnoreCase("7094-08.htm"))
		{
			if (st.getItemsCount(SPIRIT_ORES) >= 500)
			{
				st.setCond(6, true);
				st.takeItems(SPIRIT_ORES, 500);
			}
			else
			{
				htmltext = "7094-06.htm";
			}
		}
		else if (event.equalsIgnoreCase("7094-11.htm"))
		{
			st.setCond(8, true);
		}
		else if (event.equalsIgnoreCase("7094-14.htm"))
		{
			if ((st.getItemsCount(THREAD) >= 1000) && (st.getItemsCount(SUEDE) >= 500))
			{
				st.takeItems(THREAD, 1000);
				st.takeItems(SUEDE, 500);
			}
			else
			{
				htmltext = "7094-12.htm";
			}
		}
		// MIKI_THE_CAT
		else if (event.equalsIgnoreCase("8706-02.htm"))
		{
			st.setCond(3, true);
			st.takeItems(MAP);
		}
		else if (event.equalsIgnoreCase("8706-05.htm"))
		{
			st.setCond(7, true);
		}
		else if (event.equalsIgnoreCase("cat") || event.equalsIgnoreCase("racoon") || event.equalsIgnoreCase("rabbit"))
		{
			if (event.equalsIgnoreCase("cat"))
			{
				st.rewardItems(CAT_EAR, 1);
			}
			else if (event.equalsIgnoreCase("racoon"))
			{
				st.rewardItems(RACCOON_EAR, 1);
			}
			else if (event.equalsIgnoreCase("rabbit"))
			{
				st.rewardItems(RABBIT_EAR, 1);
			}
			
			htmltext = "7094-15.htm";
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
				htmltext = (player.getLevel() >= 45) ? "7120-02.htm" : "7120-01.htm";
				break;
			
			case STARTED:
				int cond = (st.getCond());
				switch (npc.getId())
				{
					case MAXIMILIAN:
						if (cond == 1)
						{
							htmltext = "7120-03.htm";
						}
						break;
					case GENTLER:
						if (cond == 1)
						{
							htmltext = "7094-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7094-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7094-03a.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7094-04.htm";
						}
						else if (cond == 5)
						{
							htmltext = (st.getItemsCount(SPIRIT_ORES) >= 500) ? "7094-07.htm" : "7094-06.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7094-09.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7094-10.htm";
						}
						else if (cond == 8)
						{
							htmltext = ((st.getItemsCount(THREAD) >= 1000) && (st.getItemsCount(SUEDE) >= 500)) ? "7094-13.htm" : "7094-12.htm";
						}
						break;
					case MIKI_THE_CAT:
						if (cond == 2)
						{
							htmltext = "8706-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8706-03.htm";
						}
						else if (cond == 6)
						{
							htmltext = "8706-04.htm";
						}
						else if (cond == 7)
						{
							htmltext = "8706-06.htm";
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
		ScriptState st = checkPlayerCondition(killer, npc, "cond", "3");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case ALLIGATOR:
				if (st.dropItems(MEDICINAL_HERB, 1, 20, 300000))
				{
					st.setCond(4);
				}
				break;
		}
		return null;
	}
}
