package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q027_ChestCaughtWithABaitOfWind extends Script
{
	// NPCs
	private static final int SHALING = 8434;
	private static final int LANOSCO = 8570;
	// ITEMs
	private static final int BIG_BLUE_TREASURE_CHEST = 6500;
	private static final int STRANGE_GOLEM_BLUESPRINT = 7625;
	// REWARD
	private static final int BLACK_PEARL_RING = 880;
	
	public Q027_ChestCaughtWithABaitOfWind()
	{
		super(27, "Chest caught with a bait of wind");
		addStartNpc(LANOSCO);
		addTalkId(LANOSCO, SHALING);
		registerItems(STRANGE_GOLEM_BLUESPRINT);
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
		
		// LANOSCO
		if (event.equalsIgnoreCase("8570-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8570-06.htm"))
		{
			if (st.hasItems(BIG_BLUE_TREASURE_CHEST))
			{
				st.setCond(2, true);
				st.takeItems(BIG_BLUE_TREASURE_CHEST);
				st.giveItems(STRANGE_GOLEM_BLUESPRINT, 1);
			}
			else
			{
				htmltext = "8570-08.htm";
			}
		}
		// SHALING
		else if (event.equalsIgnoreCase("8434-02.htm"))
		{
			st.rewardItems(BLACK_PEARL_RING, 1);
			st.exitQuest(false, true);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		ScriptState st2 = player.getScriptState("Q050_LanoscosSpecialBait");
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getLevel() >= 27) && st2.isCompleted() ? "8570-02.htm" : "8570-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case LANOSCO:
						if (cond == 1)
						{
							htmltext = st.hasItems(BIG_BLUE_TREASURE_CHEST) ? "8570-05.htm" : "8570-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8570-07.htm";
						}
						break;
					case SHALING:
						if (cond == 2)
						{
							htmltext = "8434-01.htm";
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
