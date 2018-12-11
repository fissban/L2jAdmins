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
public class Q006_StepIntoTheFuture extends Script
{
	// NPCs
	private static final int ROXXY = 7006;
	private static final int BAULRO = 7033;
	private static final int SIR_COLLIN = 7311;
	// ITEMs
	private static final int BAULRO_LETTER = 7571;
	// REWARDs
	private static final int SCROLL_OF_ESCAPE_GIRAN = 7559;
	private static final int MARK_OF_TRAVELER = 7570;
	
	public Q006_StepIntoTheFuture()
	{
		super(6, "Step into the Future");
		addStartNpc(ROXXY);
		addTalkId(ROXXY, BAULRO, SIR_COLLIN);
		registerItems(BAULRO_LETTER);
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
		
		// ROXXY
		if (event.equalsIgnoreCase("7006-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7006-06.htm"))
		{
			st.rewardItems(SCROLL_OF_ESCAPE_GIRAN, 1);
			st.rewardItems(MARK_OF_TRAVELER, 1);
			st.exitQuest(false, true);
		}
		// BAULRO
		else if (event.equalsIgnoreCase("7033-02.htm"))
		{
			st.setCond(2, true);
			st.giveItems(BAULRO_LETTER, 1);
		}
		// SIR_COLLIN
		else if (event.equalsIgnoreCase("7311-02.htm"))
		{
			if (st.hasItems(BAULRO_LETTER))
			{
				st.setCond(3, true);
				st.takeItems(BAULRO_LETTER);
			}
			else
			{
				htmltext = "7311-04.htm";
			}
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
				if ((player.getRace() != Race.HUMAN) || (player.getLevel() < 3) || (player.getLevel() > 10))
				{
					htmltext = "7006-01.htm";
				}
				else
				{
					htmltext = "7006-02.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case ROXXY:
						if (cond == 1)
						{
							htmltext = "7006-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7006-05.htm";
						}
						break;
					case BAULRO:
						if (cond == 1)
						{
							htmltext = "7033-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7033-03.htm";
						}
						break;
					case SIR_COLLIN:
						if (cond == 2)
						{
							htmltext = "7311-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7311-03.htm";
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
