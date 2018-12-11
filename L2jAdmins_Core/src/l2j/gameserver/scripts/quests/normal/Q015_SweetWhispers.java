package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 * @author Reynald0
 */
public class Q015_SweetWhispers extends Script
{
	// NPCs
	private static final int VLADIMIR = 8302;
	private static final int HIERARCH = 8517;
	private static final int MYSTEROIUS_NECROMANCER = 8518;
	
	public Q015_SweetWhispers()
	{
		super(15, "Sweet Whispers");
		addStartNpc(VLADIMIR);
		addTalkId(VLADIMIR, MYSTEROIUS_NECROMANCER, HIERARCH);
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
		}
		else if (event.equalsIgnoreCase("8518-02.htm"))
		{
			st.setCond(2, true);
		}
		else if (event.equalsIgnoreCase("8517-02.htm"))
		{
			st.rewardExpAndSp(60217, 0);
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
				htmltext = player.getLevel() < 60 ? "8302-01.htm" : "8302-02.htm";
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
					case MYSTEROIUS_NECROMANCER:
						if (cond == 1)
						{
							htmltext = "8518-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8518-03.htm";
						}
						break;
					case HIERARCH:
						if (cond == 2)
						{
							htmltext = "8517-01.htm";
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
