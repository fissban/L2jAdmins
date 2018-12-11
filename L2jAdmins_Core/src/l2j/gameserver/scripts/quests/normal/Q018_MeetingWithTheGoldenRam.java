package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q018_MeetingWithTheGoldenRam extends Script
{
	// NPCs
	private static final int DONAL = 8314;
	private static final int DAISY = 8315;
	private static final int ABERCROMBIE = 8555;
	// ITEM
	private static final int BOX = 7245;
	// REWARD
	private static final int ADENA = 57;
	
	public Q018_MeetingWithTheGoldenRam()
	{
		super(18, "Meeting with the GoldenRam");
		addStartNpc(DONAL);
		addTalkId(DONAL, DAISY, ABERCROMBIE);
		registerItems(BOX);
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
		
		// DONAL
		if (event.equalsIgnoreCase("8314-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8315-02.htm"))
		{
			st.setCond(2, true);
			st.giveItems(BOX, 1);
		}
		// ABERCROMBIE
		else if (event.equalsIgnoreCase("8555-02.htm"))
		{
			st.rewardItems(ADENA, 15000);
			st.rewardExpAndSp(50000, 0);
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
				htmltext = (player.getLevel() < 66 ? "8314-01.htm" : "8314-02.htm");
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case DONAL:
						if (cond == 1)
						{
							htmltext = "8314-04.htm";
						}
						break;
					case DAISY:
						if (cond == 1)
						{
							htmltext = "8315-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8315-03.htm";
						}
						break;
					case ABERCROMBIE:
						if (cond == 2)
						{
							htmltext = "8555-01.htm";
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
