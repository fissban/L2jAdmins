package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q370_AnElderSowsSeeds extends Script
{
	// NPC
	private static final int CASIAN = 7612;
	
	// ITEMs
	private static final int SPELLBOOK_PAGE = 5916;
	private static final int CHAPTER_OF_FIRE = 5917;
	private static final int CHAPTER_OF_WATER = 5918;
	private static final int CHAPTER_OF_WIND = 5919;
	private static final int CHAPTER_OF_EARTH = 5920;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(82, 86000);
		CHANCES.put(84, 94000);
		CHANCES.put(86, 90000);
		CHANCES.put(89, 100000);
		CHANCES.put(90, 202000);
	}
	
	public Q370_AnElderSowsSeeds()
	{
		super(370, "An Elder Sows Seeds");
		
		registerItems(SPELLBOOK_PAGE, CHAPTER_OF_FIRE, CHAPTER_OF_WATER, CHAPTER_OF_WIND, CHAPTER_OF_EARTH);
		
		addStartNpc(CASIAN);
		addTalkId(CASIAN);
		
		addKillId(82, 84, 86, 89, 90);
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
		
		if (event.equalsIgnoreCase("7612-3.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7612-6.htm"))
		{
			if (st.hasItems(CHAPTER_OF_FIRE, CHAPTER_OF_WATER, CHAPTER_OF_WIND, CHAPTER_OF_EARTH))
			{
				htmltext = "7612-8.htm";
				st.takeItems(CHAPTER_OF_FIRE, 1);
				st.takeItems(CHAPTER_OF_WATER, 1);
				st.takeItems(CHAPTER_OF_WIND, 1);
				st.takeItems(CHAPTER_OF_EARTH, 1);
				st.rewardItems(Inventory.ADENA_ID, 3600);
			}
		}
		else if (event.equalsIgnoreCase("7612-9.htm"))
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
				htmltext = player.getLevel() < 28 ? "7612-0a.htm" : "7612-0.htm";
				break;
			
			case STARTED:
				htmltext = "7612-4.htm";
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
		
		partyMember.getScriptState(getName()).dropItems(SPELLBOOK_PAGE, 1, 0, CHANCES.get(npc.getId()));
		
		return null;
	}
	
}
