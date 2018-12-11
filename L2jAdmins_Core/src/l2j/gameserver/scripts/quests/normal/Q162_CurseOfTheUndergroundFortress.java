package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q162_CurseOfTheUndergroundFortress extends Script
{
	// NPCs
	private static final int UNOREN = 7147;
	// MOBs
	private static final int SHADE_HORROR = 33;
	private static final int DARK_TERROR = 345;
	private static final int MIST_TERROR = 371;
	private static final int DUNGEON_SKELETON_ARCHER = 463;
	private static final int DUNGEON_SKELETON = 464;
	private static final int DREAD_SOLDIER = 504;
	// ITEMs
	private static final int BONE_FRAGMENT = 1158;
	private static final int ELF_SKULL = 1159;
	// REWARDs
	private static final int BONE_SHIELD = 625;
	// DROPs
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(SHADE_HORROR, 250000);
		CHANCES.put(DARK_TERROR, 260000);
		CHANCES.put(MIST_TERROR, 230000);
		CHANCES.put(DUNGEON_SKELETON_ARCHER, 250000);
		CHANCES.put(DUNGEON_SKELETON, 230000);
		CHANCES.put(DREAD_SOLDIER, 260000);
	}
	
	public Q162_CurseOfTheUndergroundFortress()
	{
		super(162, "Curse of the Underground Fortress");
		
		registerItems(BONE_FRAGMENT, ELF_SKULL);
		
		addStartNpc(UNOREN);
		addTalkId(UNOREN);
		addKillId(SHADE_HORROR, DARK_TERROR, MIST_TERROR, DUNGEON_SKELETON_ARCHER, DUNGEON_SKELETON, DREAD_SOLDIER);
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
		
		if (event.equalsIgnoreCase("7147-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getRace() == Race.DARK_ELF)
				{
					htmltext = "7147-00.htm";
				}
				else if (player.getLevel() < 12)
				{
					htmltext = "7147-01.htm";
				}
				else
				{
					htmltext = "7147-02.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "7147-05.htm";
				}
				else if (cond == 2)
				{
					htmltext = "7147-06.htm";
					st.takeItems(ELF_SKULL, -1);
					st.takeItems(BONE_FRAGMENT, -1);
					st.giveItems(BONE_SHIELD, 1);
					st.rewardItems(Inventory.ADENA_ID, 24000);
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(false);
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final ScriptState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
		{
			return null;
		}
		
		final int npcId = npc.getId();
		
		switch (npcId)
		{
			case DUNGEON_SKELETON:
			case DUNGEON_SKELETON_ARCHER:
			case DREAD_SOLDIER:
				if (st.dropItems(BONE_FRAGMENT, 1, 10, CHANCES.get(npcId)) && (st.getItemsCount(ELF_SKULL) >= 3))
				{
					st.set("cond", "2");
				}
				break;
			
			case SHADE_HORROR:
			case DARK_TERROR:
			case MIST_TERROR:
				if (st.dropItems(ELF_SKULL, 1, 3, CHANCES.get(npcId)) && (st.getItemsCount(BONE_FRAGMENT) >= 10))
				{
					st.set("cond", "2");
				}
				break;
		}
		
		return null;
	}
	
}
