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
public class Q629_CleanUpTheSwampOfScreams extends Script
{
	// NPC
	private static final int PIERCE = 8553;
	
	// ITEMS
	private static final int TALON_OF_STAKATO = 7250;
	private static final int GOLDEN_RAM_COIN = 7251;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(1508, 500000);
		CHANCES.put(1509, 431000);
		CHANCES.put(1510, 521000);
		CHANCES.put(1511, 576000);
		CHANCES.put(1511, 746000);
		CHANCES.put(1513, 530000);
		CHANCES.put(1514, 538000);
		CHANCES.put(1515, 545000);
		CHANCES.put(1516, 553000);
		CHANCES.put(1516, 560000);
	}
	
	public Q629_CleanUpTheSwampOfScreams()
	{
		super(629, "Clean up the Swamp of Screams");
		
		registerItems(TALON_OF_STAKATO, GOLDEN_RAM_COIN);
		
		addStartNpc(PIERCE);
		addTalkId(PIERCE);
		
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
		
		if (event.equalsIgnoreCase("8553-1.htm"))
		{
			if (player.getLevel() >= 66)
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
			}
			else
			{
				htmltext = "8553-0a.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("8553-3.htm"))
		{
			if (st.getItemsCount(TALON_OF_STAKATO) >= 100)
			{
				st.takeItems(TALON_OF_STAKATO, 100);
				st.giveItems(GOLDEN_RAM_COIN, 20);
			}
			else
			{
				htmltext = "8553-3a.htm";
			}
		}
		else if (event.equalsIgnoreCase("8553-5.htm"))
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
		
		if (!st.hasAtLeastOneItem(7246, 7247))
		{
			return "8553-6.htm";
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getLevel() < 66) ? "8553-0a.htm" : "8553-0.htm";
				break;
			
			case STARTED:
				htmltext = (st.getItemsCount(TALON_OF_STAKATO) >= 100) ? "8553-2.htm" : "8553-1a.htm";
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
		
		partyMember.getScriptState(getName()).dropItems(TALON_OF_STAKATO, 1, 100, CHANCES.get(npc.getId()));
		
		return null;
	}
	
}
