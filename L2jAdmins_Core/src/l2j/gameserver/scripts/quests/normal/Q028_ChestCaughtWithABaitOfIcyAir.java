package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q028_ChestCaughtWithABaitOfIcyAir extends Script
{
	// NPCs
	private static final int KIKI = 8442;
	private static final int O_FULLE = 8572;
	// ITEMs
	private static final int BIG_YELLOW_TREASURE_CHEST = 6503;
	private static final int KIKI_LETTER = 7626;
	// REWARD
	private static final int ELVEN_RING = 881;
	
	public Q028_ChestCaughtWithABaitOfIcyAir()
	{
		super(28, "Chest caught with a bait of icy air");
		addStartNpc(O_FULLE);
		addTalkId(O_FULLE, KIKI);
		registerItems(KIKI_LETTER);
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
		
		// O_FULLE
		if (event.equalsIgnoreCase("8572-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8572-06.htm"))
		{
			if (st.hasItems(BIG_YELLOW_TREASURE_CHEST))
			{
				st.setCond(2, true);
				st.takeItems(BIG_YELLOW_TREASURE_CHEST);
				st.giveItems(KIKI_LETTER, 1);
			}
			else
			{
				htmltext = "8572-08.htm";
			}
		}
		// KIKI
		else if (event.equalsIgnoreCase("8442-02.htm"))
		{
			st.rewardItems(ELVEN_RING, 1);
			st.exitQuest(false, true);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		ScriptState st2 = player.getScriptState("Q051_O_FULLEesSpecialBait");
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getLevel() >= 36) && st2.isCompleted() ? "8572-02.htm" : "8572-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case O_FULLE:
						if (cond == 1)
						{
							htmltext = st.hasItems(BIG_YELLOW_TREASURE_CHEST) ? "8572-05.htm" : "8572-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8572-07.htm";
						}
						break;
					case KIKI:
						if (cond == 2)
						{
							htmltext = "8442-01.htm";
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
