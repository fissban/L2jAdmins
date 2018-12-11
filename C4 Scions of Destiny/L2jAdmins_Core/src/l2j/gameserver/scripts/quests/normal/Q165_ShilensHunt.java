package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q165_ShilensHunt extends Script
{
	// NPCs
	private static final int NELSYA = 7348;
	// MOBs
	private static final int ASHEN_WOLF = 456;
	private static final int YOUNG_BROWN_KELTIR = 529;
	private static final int BROWN_KELTIR = 532;
	private static final int ELDER_BROWN_KELTIR = 536;
	// ITEMs
	private static final int DARK_BEZOAR = 1160;
	private static final int LESSER_HEALING_POTION = 1060;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(ASHEN_WOLF, 1000000);
		CHANCES.put(YOUNG_BROWN_KELTIR, 333333);
		CHANCES.put(BROWN_KELTIR, 333333);
		CHANCES.put(ELDER_BROWN_KELTIR, 666667);
	}
	
	public Q165_ShilensHunt()
	{
		super(165, "Shilen's Hunt");
		
		registerItems(DARK_BEZOAR);
		
		addStartNpc(NELSYA);
		addTalkId(NELSYA);
		addKillId(ASHEN_WOLF, YOUNG_BROWN_KELTIR, BROWN_KELTIR, ELDER_BROWN_KELTIR);
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
		
		if (event.equalsIgnoreCase("7348-03.htm"))
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
		String htmltext = getNoQuestMsg();
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "7348-00.htm";
				}
				else if (player.getLevel() < 3)
				{
					htmltext = "7348-01.htm";
				}
				else
				{
					htmltext = "7348-02.htm";
				}
				break;
			
			case STARTED:
				if (st.getItemsCount(DARK_BEZOAR) >= 13)
				{
					htmltext = "7348-05.htm";
					st.takeItems(DARK_BEZOAR, -1);
					st.rewardItems(LESSER_HEALING_POTION, 5);
					st.rewardExpAndSp(1000, 0);
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(false);
				}
				else
				{
					htmltext = "7348-04.htm";
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
		
		if (st.dropItems(DARK_BEZOAR, 1, 13, CHANCES.get(npc.getId())))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
