package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

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
public class Q628_HuntOfTheGoldenRamMercenaryForce extends Script
{
	// NPCs
	private static final int KAHMAN = 8554;
	
	// Items
	private static final int SPLINTER_STAKATO_CHITIN = 7248;
	private static final int NEEDLE_STAKATO_CHITIN = 7249;
	private static final int GOLDEN_RAM_BADGE_RECRUIT = 7246;
	private static final int GOLDEN_RAM_BADGE_SOLDIER = 7247;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(1508, 500000);
		CHANCES.put(1509, 430000);
		CHANCES.put(1510, 521000);
		CHANCES.put(1511, 575000);
		CHANCES.put(1511, 746000);
		CHANCES.put(1513, 500000);
		CHANCES.put(1514, 430000);
		CHANCES.put(1515, 520000);
		CHANCES.put(1516, 531000);
		CHANCES.put(1516, 744000);
	}
	
	public Q628_HuntOfTheGoldenRamMercenaryForce()
	{
		super(628, "Hunt of the Golden Ram Mercenary Force");
		
		registerItems(SPLINTER_STAKATO_CHITIN, NEEDLE_STAKATO_CHITIN, GOLDEN_RAM_BADGE_RECRUIT, GOLDEN_RAM_BADGE_SOLDIER);
		
		addStartNpc(KAHMAN);
		addTalkId(KAHMAN);
		
		for (int npcId : CHANCES.keySet())
		{
			addKillId(npcId);
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
		
		if (event.equalsIgnoreCase("8554-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8554-03a.htm"))
		{
			if ((st.getItemsCount(SPLINTER_STAKATO_CHITIN) >= 100) && (st.getInt("cond") == 1)) // Giving GOLDEN_RAM_BADGE_RECRUIT Medals
			{
				htmltext = "8554-04.htm";
				st.set("cond", "2");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(SPLINTER_STAKATO_CHITIN, -1);
				st.giveItems(GOLDEN_RAM_BADGE_RECRUIT, 1);
			}
		}
		else if (event.equalsIgnoreCase("8554-07.htm")) // Cancel Quest
		{
			st.playSound(PlaySoundType.QUEST_GIVEUP);
			st.exitQuest(true);
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
				htmltext = (player.getLevel() < 66) ? "8554-01a.htm" : "8554-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					if (st.getItemsCount(SPLINTER_STAKATO_CHITIN) >= 100)
					{
						htmltext = "8554-03.htm";
					}
					else
					{
						htmltext = "8554-03a.htm";
					}
				}
				else if (cond == 2)
				{
					if ((st.getItemsCount(SPLINTER_STAKATO_CHITIN) >= 100) && (st.getItemsCount(NEEDLE_STAKATO_CHITIN) >= 100))
					{
						htmltext = "8554-05.htm";
						st.set("cond", "3");
						st.playSound(PlaySoundType.QUEST_FINISH);
						st.takeItems(SPLINTER_STAKATO_CHITIN, -1);
						st.takeItems(NEEDLE_STAKATO_CHITIN, -1);
						st.takeItems(GOLDEN_RAM_BADGE_RECRUIT, 1);
						st.giveItems(GOLDEN_RAM_BADGE_SOLDIER, 1);
					}
					else if (!st.hasItems(SPLINTER_STAKATO_CHITIN) && !st.hasItems(NEEDLE_STAKATO_CHITIN))
					{
						htmltext = "8554-04b.htm";
					}
					else
					{
						htmltext = "8554-04a.htm";
					}
				}
				else if (cond == 3)
				{
					htmltext = "8554-05a.htm";
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
		
		final int cond = st.getInt("cond");
		final int npcId = npc.getId();
		
		switch (npcId)
		{
			case 21508:
			case 21509:
			case 21510:
			case 21511:
			case 21512:
				if ((cond == 1) || (cond == 2))
				{
					st.dropItems(SPLINTER_STAKATO_CHITIN, 1, 100, CHANCES.get(npcId));
				}
				break;
			
			case 21513:
			case 21514:
			case 21515:
			case 21516:
			case 21517:
				if (cond == 2)
				{
					st.dropItems(NEEDLE_STAKATO_CHITIN, 1, 100, CHANCES.get(npcId));
				}
				break;
		}
		
		return null;
	}
	
}
