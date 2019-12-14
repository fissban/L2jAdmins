package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q354_ConquestOfAlligatorIsland extends Script
{
	// ITEMs
	private static final int ALLIGATOR_TOOTH = 5863;
	private static final int TORN_MAP_FRAGMENT = 5864;
	private static final int PIRATE_TREASURE_MAP = 5915;
	
	private static final Map<Integer, int[][]> DROPLIST = new HashMap<>();
	{
		DROPLIST.put(804, new int[][]
		{
			{
				ALLIGATOR_TOOTH,
				1,
				0,
				490000
			},
			{
				TORN_MAP_FRAGMENT,
				1,
				0,
				100000
			}
		}); // Crokian Lad
		DROPLIST.put(805, new int[][]
		{
			{
				ALLIGATOR_TOOTH,
				1,
				0,
				560000
			},
			{
				TORN_MAP_FRAGMENT,
				1,
				0,
				100000
			}
		}); // Dailaon Lad
		DROPLIST.put(806, new int[][]
		{
			{
				ALLIGATOR_TOOTH,
				1,
				0,
				500000
			},
			{
				TORN_MAP_FRAGMENT,
				1,
				0,
				100000
			}
		}); // Crokian Lad Warrior
		DROPLIST.put(807, new int[][]
		{
			{
				ALLIGATOR_TOOTH,
				1,
				0,
				600000
			},
			{
				TORN_MAP_FRAGMENT,
				1,
				0,
				100000
			}
		}); // Farhite Lad
		DROPLIST.put(808, new int[][]
		{
			{
				ALLIGATOR_TOOTH,
				1,
				0,
				690000
			},
			{
				TORN_MAP_FRAGMENT,
				1,
				0,
				100000
			}
		}); // Nos Lad
		DROPLIST.put(991, new int[][]
		{
			{
				ALLIGATOR_TOOTH,
				1,
				0,
				600000
			},
			{
				TORN_MAP_FRAGMENT,
				1,
				0,
				100000
			}
		}); // Swamp Tribe
	}
	
	public Q354_ConquestOfAlligatorIsland()
	{
		super(354, "Conquest of Alligator Island");
		
		registerItems(ALLIGATOR_TOOTH, TORN_MAP_FRAGMENT);
		
		addStartNpc(7895); // Kluck
		addTalkId(7895);
		
		addKillId(804, 805, 806, 807, 808, 991);
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
		
		if (event.equalsIgnoreCase("7895-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7895-03.htm"))
		{
			if (st.hasItems(TORN_MAP_FRAGMENT))
			{
				htmltext = "7895-03a.htm";
			}
		}
		else if (event.equalsIgnoreCase("7895-05.htm"))
		{
			final int amount = st.getItemsCount(ALLIGATOR_TOOTH);
			if (amount > 0)
			{
				int reward = (amount * 220) + 3100;
				if (amount >= 100)
				{
					reward += 7600;
					htmltext = "7895-05b.htm";
				}
				else
				{
					htmltext = "7895-05a.htm";
				}
				
				st.takeItems(ALLIGATOR_TOOTH, -1);
				st.rewardItems(Inventory.ADENA_ID, reward);
			}
		}
		else if (event.equalsIgnoreCase("7895-07.htm"))
		{
			if (st.getItemsCount(TORN_MAP_FRAGMENT) >= 10)
			{
				htmltext = "7895-08.htm";
				st.takeItems(TORN_MAP_FRAGMENT, 10);
				st.giveItems(PIRATE_TREASURE_MAP, 1);
				st.playSound(PlaySoundType.QUEST_ITEMGET);
			}
		}
		else if (event.equalsIgnoreCase("7895-09.htm"))
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
				htmltext = player.getLevel() < 38 ? "7895-00.htm" : "7895-01.htm";
				break;
			
			case STARTED:
				htmltext = st.hasItems(TORN_MAP_FRAGMENT) ? "7895-03a.htm" : "7895-03.htm";
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
		
		partyMember.getScriptState(getName()).dropMultipleItems(DROPLIST.get(npc.getId()));
		
		return null;
	}
	
}
