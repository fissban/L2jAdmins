package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q034_InSearchOfCloth extends Script
{
	// NPCs
	private static final int RADIA = 7088;
	private static final int RALFORD = 7165;
	private static final int VARAN = 7294;
	// MOBs
	private static final int TRISALIM_SPIDER = 560;
	private static final int TRISALIM_TARANTULA = 561;
	// ITEMs
	private static final int SUEDE = 1866;
	private static final int THREAD = 1868;
	private static final int SIGNET_RING = 7164;
	private static final int SPINNERET = 7528;
	// REWARD
	private static final int MYSTERIOUS_CLOTH = 7076;
	
	public Q034_InSearchOfCloth()
	{
		super(34, "In Search of Cloth");
		addStartNpc(RADIA);
		addTalkId(RADIA, RALFORD, VARAN);
		addKillId(TRISALIM_SPIDER, TRISALIM_TARANTULA);
		registerItems(SPINNERET);
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
		
		// RADIA
		if (event.equalsIgnoreCase("7088-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7088-07.htm"))
		{
			st.setCond(3, true);
		}
		else if (event.equalsIgnoreCase("7088-12.htm"))
		{
			if ((st.getItemsCount(SUEDE) >= 3000) && (st.getItemsCount(THREAD) >= 5000))
			{
				st.takeItems(SUEDE, 3000);
				st.takeItems(THREAD, 5000);
				st.rewardItems(MYSTERIOUS_CLOTH, 1);
				st.exitQuest(false, true);
			}
			else
			{
				htmltext = "7088-11.htm";
			}
		}
		// VARAN
		else if (event.equalsIgnoreCase("7294-02.htm"))
		{
			st.setCond(2, true);
		}
		else if (event.equalsIgnoreCase("7165-02.htm"))
		{
			st.setCond(4, true);
		}
		else if (event.equalsIgnoreCase("7165-05.htm"))
		{
			st.takeItems(SPINNERET);
			st.setCond(6, true);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		ScriptState st2 = player.getScriptState("Q037_PleaseMakeMeFormalWear");
		String htmltext = getNoQuestMsg();
		if ((st == null) || (st2 == null))
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getLevel() >= 60)
				{
					htmltext = st.hasItems(SIGNET_RING) && (st2.getCond() >= 6) ? "7088-03.htm" : "7088-02.htm";
				}
				else
				{
					htmltext = "7088-01.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case RADIA:
						if (cond == 1)
						{
							htmltext = "7088-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7088-06.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7088-08.htm";
						}
						else if (cond == 6)
						{
							htmltext = (st.getItemsCount(SUEDE) >= 3000) && (st.getItemsCount(THREAD) >= 5000) ? "7088-10.htm" : "7088-09.htm";
						}
						break;
					case VARAN:
						if (cond == 1)
						{
							htmltext = "7294-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7294-03.htm";
						}
						break;
					case RALFORD:
						if (cond == 3)
						{
							htmltext = "7165-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = st.hasAtLeastOneItem(SPINNERET) ? "7165-03.htm" : "7165-02.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7165-04.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7165-06.htm";
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
		ScriptState st = checkPlayerCondition(killer, npc, "cond", "4");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case TRISALIM_TARANTULA:
			case TRISALIM_SPIDER:
				if (st.dropItems(SPINNERET, 1, 10, 300000))
				{
					st.setCond(5);
				}
				break;
		}
		return null;
	}
}
