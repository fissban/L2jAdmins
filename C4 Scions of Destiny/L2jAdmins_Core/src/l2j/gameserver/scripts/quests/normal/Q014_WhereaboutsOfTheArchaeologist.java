package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 * @author Reynald0
 */
public class Q014_WhereaboutsOfTheArchaeologist extends Script
{
	// NPCs
	private static final int LIESEL = 8263;
	private static final int GHOST_OF_ADVENTURER = 8538;
	// ITEM
	private static final int LETTER = 7253;
	// REWARD
	private static final int ADENA = 57;
	
	public Q014_WhereaboutsOfTheArchaeologist()
	{
		super(14, "Whereabouts of the Archaeologist");
		addStartNpc(LIESEL);
		addTalkId(LIESEL, GHOST_OF_ADVENTURER);
		registerItems(LETTER);
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
		
		// LIESEL
		if (event.equalsIgnoreCase("8263-03.htm"))
		{
			st.startQuest();
			st.giveItems(LETTER, 1);
		}
		// GHOST_OF_ADVENTURER
		else if (event.equalsIgnoreCase("8538-02.htm"))
		{
			st.rewardItems(ADENA, 113228);
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
				htmltext = player.getLevel() < 74 ? "8263-01.htm" : "8263-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case LIESEL:
						if (cond == 1)
						{
							htmltext = "8263-03.htm";
						}
						break;
					case GHOST_OF_ADVENTURER:
						if (cond == 1)
						{
							htmltext = "8538-01.htm";
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
