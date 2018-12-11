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
public class Q002_WhatWomenWant extends Script
{
	// NPCs
	private static final int MIRABEL = 7146;
	private static final int HERBIEL = 7150;
	private static final int GREENIS = 7157;
	private static final int ARUJIEN = 7223;
	// ITEMs
	private static final int POETRY_BOOK = 689;
	private static final int GREENIS_LETTER = 693;
	private static final int ARUJIEN_LETTER_1 = 1092;
	private static final int ARUJIEN_LETTER_2 = 1093;
	private static final int ARUJIEN_LETTER_3 = 1094;
	// REWARDs
	private static final int ADENA = 57;
	private static final int BEGINNERS_POTION = 1073;
	
	public Q002_WhatWomenWant()
	{
		super(2, "What Women Want");
		addStartNpc(ARUJIEN);
		addTalkId(MIRABEL, HERBIEL, GREENIS, ARUJIEN);
		registerItems(ARUJIEN_LETTER_1, ARUJIEN_LETTER_2, ARUJIEN_LETTER_3, POETRY_BOOK, GREENIS_LETTER);
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
		
		// ARUJIEN
		if (event.equalsIgnoreCase("7223-04.htm"))
		{
			st.startQuest();
			st.giveItems(ARUJIEN_LETTER_1, 1);
		}
		else if (event.equalsIgnoreCase("7223-08.htm"))
		{
			st.setCond(4, true);
			st.takeItems(ARUJIEN_LETTER_3);
			st.giveItems(POETRY_BOOK, 1);
		}
		else if (event.equalsIgnoreCase("7223-09.htm"))
		{
			st.rewardItems(ADENA, 450);
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
				if ((player.getRace() != Race.ELF) && (player.getRace() != Race.HUMAN))
				{
					htmltext = "7223-00.htm";
				}
				else if ((player.getLevel() < 2) || (player.getLevel() > 5))
				{
					htmltext = "7223-01.htm";
				}
				else
				{
					htmltext = "7223-02.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case ARUJIEN:
						if (cond == 1)
						{
							htmltext = "7223-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7223-06.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7223-07.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7223-11.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7223-09.htm";
							st.takeItems(GREENIS_LETTER);
							st.giveItems(BEGINNERS_POTION, 5);
							st.exitQuest(false, true);
						}
						break;
					
					case MIRABEL:
						if (cond == 1)
						{
							htmltext = "7146-01.htm";
							st.setCond(2, true);
							st.takeItems(ARUJIEN_LETTER_1);
							st.giveItems(ARUJIEN_LETTER_2, 1);
						}
						else if (cond == 2)
						{
							htmltext = "7146-02.htm";
						}
						break;
					
					case HERBIEL:
						if (cond == 2)
						{
							htmltext = "7150-01.htm";
							st.setCond(3, true);
							st.takeItems(ARUJIEN_LETTER_2);
							st.giveItems(ARUJIEN_LETTER_3, 1);
						}
						else if (cond == 3)
						{
							htmltext = "7150-02.htm";
						}
						break;
					
					case GREENIS:
						if (cond < 4)
						{
							htmltext = "7157-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7157-02.htm";
							st.setCond(5, true);
							st.takeItems(POETRY_BOOK);
							st.giveItems(GREENIS_LETTER, 1);
						}
						else if (cond == 5)
						{
							htmltext = "7157-03.htm";
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
