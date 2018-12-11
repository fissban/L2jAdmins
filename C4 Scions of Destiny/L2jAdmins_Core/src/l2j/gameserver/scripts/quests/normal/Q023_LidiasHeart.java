package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q023_LidiasHeart extends Script
{
	// NPCs
	private static final int INNOCENTIN = 8328;
	private static final int VIOLET = 8386;
	private static final int TOMBSTONE = 8523;
	private static final int GHOST_VON_HELLMAN = 8524;
	private static final int BROKEN_BOOK_SHELF = 8526;
	private static final int BOX = 8530;
	// ITEMs
	private static final int MAP_FOREST_OF_DEADMAN = 7063;
	private static final int LIDIA_DIARY = 7064;
	private static final int LIDIA_HAIR_PIN = 7148;
	private static final int SILVER_KEY = 7149;
	private static final int SILVER_SPEAR = 7150;
	// REWARD
	private static final int ADENA = 57;
	
	public Q023_LidiasHeart()
	{
		super(23, "Lidia\'s Heart");
		addStartNpc(INNOCENTIN);
		addTalkId(INNOCENTIN, BROKEN_BOOK_SHELF, GHOST_VON_HELLMAN, TOMBSTONE, VIOLET, BOX);
		registerItems(MAP_FOREST_OF_DEADMAN, SILVER_KEY, LIDIA_HAIR_PIN, LIDIA_DIARY, SILVER_SPEAR);
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
		
		// INNOCENTIN
		if (event.equalsIgnoreCase("8328-03.htm"))
		{
			if (st.getCond() != 1)
			{
				st.startQuest();
				st.giveItems(MAP_FOREST_OF_DEADMAN, 1);
				st.giveItems(SILVER_KEY, 1);
			}
		}
		else if (event.equalsIgnoreCase("8328-04.htm"))
		{
			st.setCond(2, true);
		}
		else if (event.equalsIgnoreCase("8328-11.htm"))
		{
			if (st.getCond() != 5)
			{
				st.setCond(5, true);
			}
		}
		else if (event.equalsIgnoreCase("8328-17.htm"))
		{
			st.setCond(6, true);
			st.takeItems(LIDIA_HAIR_PIN);
		}
		// BROKEN_BOOK_SHELF
		else if (event.equalsIgnoreCase("8526-02.htm"))
		{
			st.setCond(3, true);
			st.takeItems(SILVER_KEY);
		}
		else if (event.equalsIgnoreCase("8526-05.htm"))
		{
			st.giveItems(LIDIA_HAIR_PIN, 1);
			
			if (st.hasItems(LIDIA_DIARY, LIDIA_HAIR_PIN))
			{
				st.setCond(4, true);
			}
		}
		else if (event.equalsIgnoreCase("8526-11.htm"))
		{
			st.giveItems(LIDIA_DIARY, 1);
			
			if (st.hasItems(LIDIA_DIARY, LIDIA_HAIR_PIN))
			{
				st.setCond(4, true);
			}
		}
		// TOMBSTONE
		else if (event.equalsIgnoreCase("8523-02.htm"))
		{
			L2Npc von = addSpawn(GHOST_VON_HELLMAN, 51432, -54570, -3136, 0, false, 120000);
			von.broadcastNpcSay("Who awoke me?");
			st.playSound("SkillSound5.horror_02");
		}
		else if (event.equalsIgnoreCase("8523-05.htm"))
		{
			st.setCond(8, true);
			st.giveItems(SILVER_KEY, 1);
		}
		else if (event.equalsIgnoreCase("spawn_ghost"))
		{
			htmltext = "";
			L2Npc von = addSpawn(GHOST_VON_HELLMAN, 51432, -54570, -3136, 0, false, 120000);
			von.broadcastNpcSay("Who awoke me?");
			st.playSound("SkillSound5.horror_02");
		}
		// GHOST_VON_HELLMAN
		else if (event.equalsIgnoreCase("8524-04.htm"))
		{
			st.setCond(7, true);
			st.takeItems(LIDIA_DIARY);
		}
		// BOX
		else if (event.equalsIgnoreCase("8530-02.htm"))
		{
			st.setCond(10, true);
			st.takeItems(SILVER_KEY);
			st.giveItems(SILVER_SPEAR, 1);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		ScriptState st2 = player.getScriptState("Q022_TragedyInVonHellmannForest");
		String htmltext = getNoQuestMsg();
		if ((st == null) || (st2 == null))
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if (st2.isCompleted() && (player.getLevel() >= 64))
				{
					htmltext = "8328-02.htm";
				}
				else
				{
					htmltext = "8328-01.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case INNOCENTIN:
						if (cond == 1)
						{
							htmltext = "8328-03.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8328-07.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8328-08.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8328-10.htm";
						}
						else if (cond == 6)
						{
							htmltext = "8328-19.htm";
						}
						break;
					case BROKEN_BOOK_SHELF:
						if (cond == 2)
						{
							htmltext = "8526-01.htm";
						}
						else if (cond == 3)
						{
							if (st.hasItems(LIDIA_HAIR_PIN))
							{
								htmltext = "8526-06.htm";
							}
							else if (st.hasItems(LIDIA_DIARY))
							{
								htmltext = "8526-12.htm";
							}
							else
							{
								htmltext = "8526-02.htm";
							}
						}
						else if (cond == 4)
						{
							htmltext = "8526-13.htm";
						}
						break;
					case TOMBSTONE:
						if (cond == 6)
						{
							htmltext = "8523-01.htm";
						}
						else if (cond == 7)
						{
							htmltext = "8523-04.htm";
						}
						else if (cond == 8)
						{
							htmltext = "8523-03.htm";
						}
						break;
					case GHOST_VON_HELLMAN:
						if (cond == 6)
						{
							htmltext = "8524-01.htm";
						}
						else if (cond == 7)
						{
							htmltext = "8524-05.htm";
						}
						else if (cond == 8)
						{
							htmltext = "8524-06.htm";
						}
						break;
					case VIOLET:
						if (cond == 8)
						{
							htmltext = "8386-01.htm";
							st.setCond(9, true);
						}
						else if (cond == 9)
						{
							htmltext = "8386-02.htm";
						}
						else if (cond == 10)
						{
							htmltext = "8386-03.htm";
							st.rewardItems(ADENA, 100000);
							st.exitQuest(false, true);
						}
						break;
					case BOX:
						if (cond == 9)
						{
							htmltext = "8530-01.htm";
						}
						else if (cond == 10)
						{
							htmltext = "8530-03.htm";
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
