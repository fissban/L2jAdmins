package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

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
public class Q359_ForSleeplessDeadmen extends Script
{
	// Item
	private static final int REMAINS = 5869;
	
	// Monsters
	private static final int DOOM_SERVANT = 1006;
	private static final int DOOM_GUARD = 1007;
	private static final int DOOM_ARCHER = 1008;
	
	// Reward
	private static final int REWARD[] =
	{
		6341,
		6342,
		6343,
		6344,
		6345,
		6346,
		5494,
		5495
	};
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(DOOM_SERVANT, 320000);
		CHANCES.put(DOOM_GUARD, 340000);
		CHANCES.put(DOOM_ARCHER, 420000);
	}
	
	public Q359_ForSleeplessDeadmen()
	{
		super(359, "For Sleepless Deadmen");
		
		registerItems(REMAINS);
		
		addStartNpc(7857); // Orven
		addTalkId(7857);
		
		addKillId(DOOM_SERVANT, DOOM_GUARD, DOOM_ARCHER);
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
		
		if (event.equalsIgnoreCase("7857-06.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7857-10.htm"))
		{
			st.giveItems(REWARD[Rnd.get(REWARD.length)], 4);
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
				htmltext = player.getLevel() < 60 ? "7857-01.htm" : "7857-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "7857-07.htm";
				}
				else if (cond == 2)
				{
					htmltext = "7857-08.htm";
					st.set("cond", "3");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.takeItems(REMAINS, -1);
				}
				else if (cond == 3)
				{
					htmltext = "7857-09.htm";
				}
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
		
		if (st.dropItems(REMAINS, 1, 60, CHANCES.get(npc.getId())))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
