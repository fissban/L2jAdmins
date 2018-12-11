package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        CaFi
 * @originalQuest aCis & python
 */
public class Q619_RelicsOfTheOldEmpire extends Script
{
	// NPC
	private static int GHOST_OF_ADVENTURER = 8538;
	
	// Items
	private static int RELICS = 7254;
	private static int ENTRANCE = 7075;
	
	// Rewards ; all S grade weapons recipe (60%)
	private static int[] REWARDS = new int[]
	{
		6881,
		6883,
		6885,
		6887,
		6891,
		6893,
		6895,
		6897,
		6899,
		7580
	};
	
	// Rewards2
	private static final int REWARDS2[] =
	{
		6882,
		6884,
		6886,
		6888,
		6893,
		6894,
		6896,
		6898,
		6900,
		7581
	};
	
	public Q619_RelicsOfTheOldEmpire()
	{
		super(619, "Relics of the Old Empire");
		
		registerItems(RELICS);
		
		addStartNpc(GHOST_OF_ADVENTURER);
		addTalkId(GHOST_OF_ADVENTURER);
		
		for (int id = 1396; id <= 1434; id++)
		{
			// IT monsters
			addKillId(id);
		}
		
		// monsters at IT entrance
		addKillId(1798, 1799, 1800);
		
		for (int id = 12955; id <= 13091; id++)
		{
			// Sepulchers monsters
			addKillId(id);
		}
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
		
		if (event.equalsIgnoreCase("8538-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8538-09.htm"))
		{
			if (st.getItemsCount(RELICS) >= 1000)
			{
				htmltext = "8538-09.htm";
				st.takeItems(RELICS, 1000);
				st.giveItems(REWARDS2[Rnd.get(REWARDS2.length)], 1);
				st.giveItems(REWARDS[Rnd.get(REWARDS.length)], 1);
			}
			else
			{
				htmltext = "8538-06.htm";
			}
		}
		else if (event.equalsIgnoreCase("8538-10.htm"))
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
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getLevel() < 74) ? "8538-02.htm" : "8538-01.htm";
				break;
			
			case STARTED:
				if (st.getItemsCount(RELICS) >= 1000)
				{
					htmltext = "8538-04.htm";
				}
				else if (st.hasItems(ENTRANCE))
				{
					htmltext = "8538-06.htm";
				}
				else
				{
					htmltext = "8538-07.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		ScriptState st = partyMember.getScriptState(getName());
		
		st.dropItemsAlways(RELICS, 1, 0);
		st.dropItems(ENTRANCE, 1, 0, 50000);
		
		return null;
	}
	
}
