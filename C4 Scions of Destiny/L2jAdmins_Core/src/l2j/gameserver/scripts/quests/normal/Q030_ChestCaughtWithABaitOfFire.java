package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q030_ChestCaughtWithABaitOfFire extends Script
{
	
	// NPCs
	private static final int RUKAL = 7629;
	private static final int LINNAEUS = 8577;
	// ITEMs
	private static final int RED_TREASURE_BOX = 6511;
	private static final int RUKAL_MUSICAL = 7628;
	// REWARD
	private static final int PROTECTION_NECKLACE = 916;
	
	public Q030_ChestCaughtWithABaitOfFire()
	{
		super(30, "Chest caught with a bait of fire");
		addStartNpc(LINNAEUS);
		addTalkId(LINNAEUS, RUKAL);
		registerItems(RUKAL_MUSICAL);
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
			if (st.hasItems(RED_TREASURE_BOX))
			{
				st.setCond(2, true);
				st.takeItems(RED_TREASURE_BOX);
				st.giveItems(RUKAL_MUSICAL, 1);
			}
			else
			{
				htmltext = "8577-08.htm";
			}
		}
		// RUKAL
		else if (event.equalsIgnoreCase("7629-02.htm"))
		{
			st.rewardItems(PROTECTION_NECKLACE, 1);
			st.exitQuest(false, true);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		ScriptState st2 = player.getScriptState("Q053_LinnaeusSpecialBait");
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getLevel() >= 60) && st2.isCompleted() ? "8577-02.htm" : "8577-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case LINNAEUS:
						if (cond == 1)
						{
							htmltext = st.hasItems(RED_TREASURE_BOX) ? "8577-05.htm" : "8577-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8577-07.htm";
						}
						break;
					case RUKAL:
						if (cond == 2)
						{
							htmltext = "7629-01.htm";
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
