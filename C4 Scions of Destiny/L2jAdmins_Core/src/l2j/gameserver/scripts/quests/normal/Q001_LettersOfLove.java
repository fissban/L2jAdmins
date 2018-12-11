/*
 * Copyright (C) 2014-2020 L2jAdjmins
 * This file is part of L2jAdmins.
 * L2jAdmins is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * L2jAdmins is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 * @author Reynald0
 */
public class Q001_LettersOfLove extends Script
{
	// NPCs
	private static final int ROXXY = 7006;
	private static final int BAULRO = 7033;
	private static final int DARIN = 7048;
	// ITEMs
	private static final int DARING_LETTER = 687;
	private static final int ROXXY_KERCHIEF = 688;
	private static final int DARING_RECEIPT = 1079;
	private static final int BAUL_POTION = 1080;
	// REWARD
	private static final int NECKLACE = 906;
	
	public Q001_LettersOfLove()
	{
		super(1, "Letters of Love");
		addStartNpc(DARIN);
		addTalkId(ROXXY, BAULRO, DARIN);
		registerItems(DARING_LETTER, ROXXY_KERCHIEF, DARING_RECEIPT, BAUL_POTION);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		// DARIN
		if (event.equalsIgnoreCase("7048-06.htm"))
		{
			st.startQuest();
			st.giveItems(DARING_LETTER, 1);
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
				htmltext = (player.getLevel() < 2) ? "7048-01.htm" : "7048-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case DARIN:
						if (cond == 1)
						{
							htmltext = "7048-07.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7048-08.htm";
							st.setCond(3, true);
							st.takeItems(ROXXY_KERCHIEF);
							st.giveItems(DARING_RECEIPT, 1);
						}
						else if (cond == 3)
						{
							htmltext = "7048-09.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7048-10.htm";
							st.giveItems(NECKLACE, 1);
							st.exitQuest(false, true);
						}
						break;
					case ROXXY:
						if (cond == 1)
						{
							htmltext = "7006-01.htm";
							st.setCond(2, true);
							st.takeItems(DARING_LETTER);
							st.giveItems(ROXXY_KERCHIEF, 1);
						}
						else if (cond == 2)
						{
							htmltext = "7006-02.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7006-03.htm";
						}
						break;
					case BAULRO:
						if (cond == 3)
						{
							htmltext = "7033-01.htm";
							st.setCond(4, true);
							st.takeItems(DARING_RECEIPT);
							st.giveItems(BAUL_POTION, 1);
						}
						else if (cond == 4)
						{
							htmltext = "7033-02.htm";
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
