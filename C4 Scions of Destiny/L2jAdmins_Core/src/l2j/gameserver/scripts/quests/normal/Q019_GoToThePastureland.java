package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q019_GoToThePastureland extends Script
{
	// NPCs
	private static final int VLADIMIR = 8302;
	private static final int TUNATUN = 8537;
	// ITEM
	private static final int BEAST_MEAT = 7547;
	// REWARD
	private static final int ADENA = 57;
	
	public Q019_GoToThePastureland()
	{
		super(19, "Go to the Pastureland!");
		addStartNpc(VLADIMIR);
		addTalkId(VLADIMIR, TUNATUN);
		registerItems(BEAST_MEAT);
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
		
		// VLADIMIR
		if (event.equalsIgnoreCase("8302-03.htm"))
		{
			st.startQuest();
			st.giveItems(BEAST_MEAT, 1);
		}
		// TUNATUN
		else if (event.equalsIgnoreCase("8537-02.htm"))
		{
			st.rewardItems(ADENA, 30000);
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
				htmltext = player.getLevel() < 63 ? "8302-01.htm" : "8302-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case VLADIMIR:
						if (cond == 1)
						{
							htmltext = "8302-04.htm";
						}
						break;
					case TUNATUN:
						if (cond == 1)
						{
							htmltext = "8537-01.htm";
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
