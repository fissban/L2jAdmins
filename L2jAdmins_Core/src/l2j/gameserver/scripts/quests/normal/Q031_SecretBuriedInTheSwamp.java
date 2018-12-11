package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q031_SecretBuriedInTheSwamp extends Script
{
	// NPCs
	private static final int ABERCROMBIE = 8555;
	private static final int FORGOTTEN_MONUMENT_1 = 8661;
	private static final int FORGOTTEN_MONUMENT_2 = 8662;
	private static final int FORGOTTEN_MONUMENT_3 = 8663;
	private static final int FORGOTTEN_MONUMENT_4 = 8664;
	private static final int CORPSE_OF_DWARF = 8665;
	// ITEMs
	private static final int KRORINS_JOURNAL = 7252;
	// REWARD
	private static final int ADENA = 57;
	
	public Q031_SecretBuriedInTheSwamp()
	{
		super(31, "Secret Buried in the Swamp");
		addStartNpc(ABERCROMBIE);
		addTalkId(ABERCROMBIE, CORPSE_OF_DWARF, FORGOTTEN_MONUMENT_1, FORGOTTEN_MONUMENT_2, FORGOTTEN_MONUMENT_3, FORGOTTEN_MONUMENT_4);
		registerItems(KRORINS_JOURNAL);
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
		
		// ABERCROMBIE
		if (event.equalsIgnoreCase("8555-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8555-05.htm"))
		{
			st.setCond(3, true);
			st.takeItems(KRORINS_JOURNAL);
		}
		else if (event.equalsIgnoreCase("8555-08.htm"))
		{
			st.rewardExpAndSp(130000, 0);
			st.rewardItems(ADENA, 40000);
			st.exitQuest(false, true);
		}
		// CORPSE_OF_DWARF
		else if (event.equalsIgnoreCase("8665-02.htm"))
		{
			st.setCond(2, true);
			st.giveItems(KRORINS_JOURNAL, 1);
		}
		// FORGOTTEN_MONUMENT_1
		else if (event.equalsIgnoreCase("8661-02.htm"))
		{
			st.setCond(4, true);
		}
		else if (event.equalsIgnoreCase("8662-02.htm"))
		{
			st.setCond(5, true);
		}
		else if (event.equalsIgnoreCase("8663-02.htm"))
		{
			st.setCond(6, true);
		}
		else if (event.equalsIgnoreCase("8664-02.htm"))
		{
			st.setCond(7, true);
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
				htmltext = ((player.getLevel() >= 66) && (player.getLevel() <= 76)) ? "8555-02.htm" : "8555-01.htm";
				break;
			
			case STARTED:
				int cond = (st.getCond());
				switch (npc.getId())
				{
					case ABERCROMBIE:
						if (cond == 1)
						{
							htmltext = "8555-03.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8555-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8555-06.htm";
						}
						else if (cond == 7)
						{
							htmltext = "8555-07.htm";
						}
						break;
					
					case CORPSE_OF_DWARF:
						if (cond == 1)
						{
							htmltext = "8665-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8665-03.htm";
						}
						break;
					case FORGOTTEN_MONUMENT_1:
						if (cond == 3)
						{
							htmltext = "8661-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8661-03.htm";
						}
						break;
					case FORGOTTEN_MONUMENT_2:
						if (cond == 4)
						{
							htmltext = "8662-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8662-03.htm";
						}
						break;
					case FORGOTTEN_MONUMENT_3:
						if (cond == 5)
						{
							htmltext = "8663-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "8663-03.htm";
						}
						break;
					case FORGOTTEN_MONUMENT_4:
						if (cond == 6)
						{
							htmltext = "8664-01.htm";
						}
						else if (cond == 7)
						{
							htmltext = "8664-03.htm";
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
