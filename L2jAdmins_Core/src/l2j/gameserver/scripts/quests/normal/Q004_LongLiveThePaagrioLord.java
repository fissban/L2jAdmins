package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 * @author Reynald0
 */
public class Q004_LongLiveThePaagrioLord extends Script
{
	// NPCs
	private static final int KUNAI = 7559;
	private static final int USKA = 7560;
	private static final int GROOKIN = 7562;
	private static final int VARKEES = 7566;
	private static final int NAKUSIN = 7578;
	private static final int TATARU = 7585;
	private static final int GANTAKI = 7587;
	// ITEMs
	private static final int HONEY_KHANDAR = 1541;
	private static final int BEAR_FUR_CLOAK = 1542;
	private static final int BLOODY_AXE = 1543;
	private static final int ANCESTOR_SKULL = 1544;
	private static final int SPIDER_DUST = 1545;
	private static final int DEEP_SEA_ORB = 1546;
	// REWARD
	private static final int CLUB = 4;
	
	public Q004_LongLiveThePaagrioLord()
	{
		super(4, "Long Live the Paagrio Lord");
		addStartNpc(NAKUSIN);
		addTalkId(NAKUSIN, KUNAI, USKA, GROOKIN, VARKEES, TATARU, GANTAKI);
		registerItems(HONEY_KHANDAR, BEAR_FUR_CLOAK, BLOODY_AXE, ANCESTOR_SKULL, SPIDER_DUST, DEEP_SEA_ORB);
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
		
		// NAKUSIN
		if (event.equalsIgnoreCase("7578-03.htm"))
		{
			st.startQuest();
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "7578-00.htm";
				}
				else if ((player.getLevel() < 2) || (player.getLevel() > 5))
				{
					htmltext = "7578-01.htm";
				}
				else
				{
					htmltext = "7578-02.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case NAKUSIN:
						htmltext = "7578-04.htm";
						if (cond == 2)
						{
							st.rewardItems(CLUB, 1);
							st.exitQuest(false, true);
							htmltext = "7578-06.htm";
						}
						break;
					case VARKEES:
						if (st.hasItems(HONEY_KHANDAR))
						{
							htmltext = "7566-02.htm";
						}
						else
						{
							htmltext = "7566-01.htm";
							st.giveItems(HONEY_KHANDAR, 1);
						}
						break;
					case TATARU:
						if (st.hasItems(BEAR_FUR_CLOAK))
						{
							htmltext = "7585-02.htm";
						}
						else
						{
							htmltext = "7585-01.htm";
							st.giveItems(BEAR_FUR_CLOAK, 1);
						}
						break;
					case USKA:
						if (st.hasItems(ANCESTOR_SKULL))
						{
							htmltext = "7560-02.htm";
						}
						else
						{
							htmltext = "7560-01.htm";
							st.giveItems(ANCESTOR_SKULL, 1);
						}
						break;
					case GROOKIN:
						if (st.hasItems(BLOODY_AXE))
						{
							htmltext = "7562-02.htm";
						}
						else
						{
							htmltext = "7562-01.htm";
							st.giveItems(BLOODY_AXE, 1);
						}
						break;
					case GANTAKI:
						if (st.hasItems(DEEP_SEA_ORB))
						{
							htmltext = "7587-02.htm";
						}
						else
						{
							htmltext = "7587-01.htm";
							st.giveItems(DEEP_SEA_ORB, 1);
						}
						break;
					case KUNAI:
						if (st.hasItems(SPIDER_DUST))
						{
							htmltext = "7559-02.htm";
						}
						else
						{
							htmltext = "7559-01.htm";
							st.giveItems(SPIDER_DUST, 1);
						}
						break;
				}
				
				if (st.hasItems(HONEY_KHANDAR, BEAR_FUR_CLOAK, BLOODY_AXE, ANCESTOR_SKULL, SPIDER_DUST, DEEP_SEA_ORB))
				{
					st.setCond(2, true);
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
}
