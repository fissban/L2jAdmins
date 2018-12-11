package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q363_SorrowfulSoundOfFlute extends Script
{
	// NPCs
	private static final int NANARIN = 7956;
	private static final int OPIX = 7595;
	private static final int ALDO = 7057;
	private static final int RANSPO = 7594;
	private static final int HOLVAS = 7058;
	private static final int BARBADO = 7959;
	private static final int POITAN = 7458;
	
	// Item
	private static final int NANARIN_FLUTE = 4319;
	private static final int BLACK_BEER = 4320;
	private static final int CLOTHES = 4318;
	
	// Reward
	private static final int THEME_OF_SOLITUDE = 4420;
	
	public Q363_SorrowfulSoundOfFlute()
	{
		super(363, "Sorrowful Sound of Flute");
		
		registerItems(NANARIN_FLUTE, BLACK_BEER, CLOTHES);
		
		addStartNpc(NANARIN);
		addTalkId(NANARIN, OPIX, ALDO, RANSPO, HOLVAS, BARBADO, POITAN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7956-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7956-05.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(CLOTHES, 1);
		}
		else if (event.equalsIgnoreCase("7956-06.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(NANARIN_FLUTE, 1);
		}
		else if (event.equalsIgnoreCase("7956-07.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(BLACK_BEER, 1);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 15 ? "7956-03.htm" : "7956-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case NANARIN:
						if (cond == 1)
						{
							htmltext = "7956-02.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7956-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7956-08.htm";
						}
						else if (cond == 4)
						{
							if (st.getInt("success") == 1)
							{
								htmltext = "7956-09.htm";
								st.giveItems(THEME_OF_SOLITUDE, 1);
								st.playSound(PlaySoundType.QUEST_FINISH);
							}
							else
							{
								htmltext = "7956-10.htm";
								st.playSound(PlaySoundType.QUEST_GIVEUP);
							}
							st.exitQuest(true);
						}
						break;
					
					case OPIX:
					case POITAN:
					case ALDO:
					case RANSPO:
					case HOLVAS:
						htmltext = npc.getId() + "-01.htm";
						if (cond == 1)
						{
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						break;
					
					case BARBADO:
						if (cond == 3)
						{
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							
							if (st.hasItems(NANARIN_FLUTE))
							{
								htmltext = "7959-02.htm";
								st.set("success", "1");
							}
							else
							{
								htmltext = "7959-01.htm";
							}
							
							st.takeItems(BLACK_BEER, -1);
							st.takeItems(CLOTHES, -1);
							st.takeItems(NANARIN_FLUTE, -1);
						}
						else if (cond == 4)
						{
							htmltext = "7959-03.htm";
						}
						break;
				}
		}
		
		return htmltext;
	}
	
}
