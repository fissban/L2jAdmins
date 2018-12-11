package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q376_ExplorationOfTheGiantsCave_Part1 extends Script
{
	// NPCs
	private static final int SOBLING = 8147;
	private static final int CLIFF = 7182;
	
	// Items
	private static final int PARCHMENT = 5944;
	private static final int DICTIONARY_BASIC = 5891;
	private static final int MYSTERIOUS_BOOK = 5890;
	private static final int DICTIONARY_INTERMEDIATE = 5892;
	private static final int[][] BOOKS =
	{
		// medical theory -> tallum tunic, tallum stockings
		{
			5937,
			5938,
			5939,
			5940,
			5941
		},
		// architecture -> dark crystal leather, tallum leather
		{
			5932,
			5933,
			5934,
			5935,
			5936
		},
		// golem plans -> dark crystal breastplate, tallum plate
		{
			5922,
			5923,
			5924,
			5925,
			5926
		},
		// basics of magic -> dark crystal gaiters, dark crystal leggings
		{
			5927,
			5928,
			5929,
			5930,
			5931
		}
	};
	
	// Rewards
	private static final int[][] RECIPES =
	{
		// medical theory -> tallum tunic, tallum stockings
		{
			5346,
			5354
		},
		// architecture -> dark crystal leather, tallum leather
		{
			5332,
			5334
		},
		// golem plans -> dark crystal breastplate, tallum plate
		{
			5416,
			5418
		},
		// basics of magic -> dark crystal gaiters, dark crystal leggings
		{
			5424,
			5340
		}
	};
	
	public Q376_ExplorationOfTheGiantsCave_Part1()
	{
		super(376, "Exploration of the Giants' Cave, Part 1");
		
		registerItems(DICTIONARY_BASIC, MYSTERIOUS_BOOK);
		
		addStartNpc(SOBLING);
		addTalkId(SOBLING, CLIFF);
		
		addKillId(647, 648, 649, 650);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		// Sobling
		if (event.equalsIgnoreCase("8147-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.set("condBook", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(DICTIONARY_BASIC, 1);
		}
		else if (event.equalsIgnoreCase("8147-04.htm"))
		{
			htmltext = checkItems(st);
		}
		else if (event.equalsIgnoreCase("8147-09.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		// Cliff
		else if (event.equalsIgnoreCase("7182-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(MYSTERIOUS_BOOK, -1);
			st.giveItems(DICTIONARY_INTERMEDIATE, 1);
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
				htmltext = player.getLevel() < 51 ? "8147-01.htm" : "8147-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case SOBLING:
						htmltext = checkItems(st);
						break;
					
					case CLIFF:
						if ((cond == 2) && st.hasItems(MYSTERIOUS_BOOK))
						{
							htmltext = "7182-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7182-03.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		// Drop parchment to anyone
		L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		ScriptState st = partyMember.getScriptState(getName());
		
		st.dropItems(PARCHMENT, 1, 0, 20000);
		
		// Drop mysterious book to person who still need it
		partyMember = getRandomPartyMember(player, npc, "condBook", "1");
		if (partyMember == null)
		{
			return null;
		}
		
		st = partyMember.getScriptState(getName());
		
		if (st.dropItems(MYSTERIOUS_BOOK, 1, 1, 1000))
		{
			st.unset("condBook");
		}
		
		return null;
	}
	
	private static String checkItems(ScriptState st)
	{
		if (st.hasItems(MYSTERIOUS_BOOK))
		{
			final int cond = st.getInt("cond");
			if (cond == 1)
			{
				st.set("cond", "2");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				return "8147-07.htm";
			}
			return "8147-08.htm";
		}
		
		for (int type = 0; type < BOOKS.length; type++)
		{
			boolean complete = true;
			for (final int book : BOOKS[type])
			{
				if (!st.hasItems(book))
				{
					complete = false;
				}
			}
			
			if (complete)
			{
				for (final int book : BOOKS[type])
				{
					st.takeItems(book, 1);
				}
				
				st.giveItems(RECIPES[type][Rnd.get(RECIPES[type].length)], 1);
				return "8147-04.htm";
			}
		}
		return "8147-05.htm";
	}
	
}
