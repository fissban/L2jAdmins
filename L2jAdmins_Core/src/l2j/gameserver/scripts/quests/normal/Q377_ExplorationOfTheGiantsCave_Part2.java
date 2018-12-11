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
public class Q377_ExplorationOfTheGiantsCave_Part2 extends Script
{
	// Items
	private static final int ANCIENT_BOOK = 5955;
	private static final int DICTIONARY_INTERMEDIATE = 5892;
	
	private static final int[][] BOOKS =
	{
		// science & technology -> majestic leather, leather armor of nightmare
		{
			5945,
			5946,
			5947,
			5948,
			5949
		},
		// culture -> armor of nightmare, majestic plate
		{
			5950,
			5951,
			5952,
			5953,
			5954
		}
	};
	
	// Rewards
	private static final int[][] RECIPES =
	{
		// science & technology -> majestic leather, leather armor of nightmare
		{
			5338,
			5336
		},
		// culture -> armor of nightmare, majestic plate
		{
			5420,
			5422
		}
	};
	
	public Q377_ExplorationOfTheGiantsCave_Part2()
	{
		super(377, "Exploration of the Giants' Cave, Part 2");
		
		addStartNpc(8147); // Sobling
		addTalkId(8147);
		
		addKillId(654, 656, 657, 658);
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
		
		if (event.equalsIgnoreCase("8147-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8147-04.htm"))
		{
			htmltext = checkItems(st);
		}
		else if (event.equalsIgnoreCase("8147-07.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
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
				htmltext = (player.getLevel() < 57) || !st.hasItems(DICTIONARY_INTERMEDIATE) ? "8147-01.htm" : "8147-02.htm";
				break;
			
			case STARTED:
				htmltext = checkItems(st);
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		partyMember.getScriptState(getName()).dropItems(ANCIENT_BOOK, 1, 0, 18000);
		
		return null;
	}
	
	private static String checkItems(ScriptState st)
	{
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
