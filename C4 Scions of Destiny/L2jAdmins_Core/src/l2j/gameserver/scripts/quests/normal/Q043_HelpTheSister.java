package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q043_HelpTheSister extends Script
{
	// NPCs
	private static final int GALLADUCCI = 7097;
	private static final int COOPER = 7829;
	// ITEMs
	private static final int CRAFTED_DAGGER = 220;
	private static final int MAP_PIECE = 7550;
	private static final int IVORY_COLORED_MAP = 7551;
	// MOBs
	private static final int SPECTER = 171;
	private static final int SORROW_MAIDEN = 197;
	// REWARD
	private static final int PET_TICKET_COUGAR = 7584;
	
	public Q043_HelpTheSister()
	{
		super(43, "Help the Sister!");
		addStartNpc(COOPER);
		addTalkId(COOPER, GALLADUCCI);
		addKillId(SPECTER, SORROW_MAIDEN);
		registerItems(MAP_PIECE, PET_TICKET_COUGAR);
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
		
		// COOPER
		if (event.equalsIgnoreCase("7829-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7829-06.htm"))
		{
			if (st.hasItems(CRAFTED_DAGGER))
			{
				st.setCond(2, true);
				st.takeItems(CRAFTED_DAGGER, 1);
			}
			else
			{
				htmltext = "7829-06a.htm";
			}
		}
		else if (event.equalsIgnoreCase("7829-09.htm"))
		{
			st.setCond(4, true);
			st.takeItems(MAP_PIECE);
			st.giveItems(IVORY_COLORED_MAP, 1);
		}
		else if (event.equalsIgnoreCase("7829-12.htm"))
		{
			st.rewardItems(PET_TICKET_COUGAR, 1);
			st.exitQuest(false, true);
		}
		// GALLADUCCI
		else if (event.equalsIgnoreCase("7097-02.htm"))
		{
			st.setCond(5, true);
			st.takeItems(IVORY_COLORED_MAP);
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
				htmltext = player.getLevel() >= 26 ? "7829-02.htm" : "7829-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case COOPER:
						if (cond == 1)
						{
							htmltext = st.hasItems(CRAFTED_DAGGER) ? "7829-05.htm" : "7829-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7829-07.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7829-08.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7829-10.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7829-11.htm";
						}
						break;
					case GALLADUCCI:
						if (cond == 4)
						{
							htmltext = "7097-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7097-03.htm";
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
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		ScriptState st = checkPlayerCondition(killer, npc, "cond", "2");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case SORROW_MAIDEN:
			case SPECTER:
				if (st.dropItems(MAP_PIECE, 1, 30, 300000))
				{
					st.setCond(3);
				}
				break;
		}
		return null;
	}
}
