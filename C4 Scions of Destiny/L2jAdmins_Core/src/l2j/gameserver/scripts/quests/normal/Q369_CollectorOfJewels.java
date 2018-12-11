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
public class Q369_CollectorOfJewels extends Script
{
	// NPC
	private static final int NELL = 7376;
	
	// ITEMs
	private static final int FLARE_SHARD = 5882;
	private static final int FREEZING_SHARD = 5883;
	
	// Reward
	private static final int ADENA = 57;
	
	// Droplist
	private static final Map<Integer, int[]> DROPLIST = new HashMap<>();
	{
		DROPLIST.put(609, new int[]
		{
			FLARE_SHARD,
			630000
		});
		DROPLIST.put(612, new int[]
		{
			FLARE_SHARD,
			770000
		});
		DROPLIST.put(749, new int[]
		{
			FLARE_SHARD,
			850000
		});
		DROPLIST.put(616, new int[]
		{
			FREEZING_SHARD,
			600000
		});
		DROPLIST.put(619, new int[]
		{
			FREEZING_SHARD,
			730000
		});
		DROPLIST.put(747, new int[]
		{
			FREEZING_SHARD,
			850000
		});
	}
	
	public Q369_CollectorOfJewels()
	{
		super(369, "Collector of Jewels");
		
		registerItems(FLARE_SHARD, FREEZING_SHARD);
		
		addStartNpc(NELL);
		addTalkId(NELL);
		
		for (final int mob : DROPLIST.keySet())
		{
			addKillId(mob);
		}
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
		
		if (event.equalsIgnoreCase("7376-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7376-07.htm"))
		{
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if (event.equalsIgnoreCase("7376-08.htm"))
		{
			st.exitQuest(true);
			st.playSound(PlaySoundType.QUEST_FINISH);
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
				htmltext = player.getLevel() < 25 ? "7376-01.htm" : "7376-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				final int flare = st.getItemsCount(FLARE_SHARD);
				final int freezing = st.getItemsCount(FREEZING_SHARD);
				
				if (cond == 1)
				{
					htmltext = "7376-04.htm";
				}
				else if ((cond == 2) && (flare >= 50) && (freezing >= 50))
				{
					htmltext = "7376-05.htm";
					st.set("cond", "3");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.takeItems(FLARE_SHARD, -1);
					st.takeItems(FREEZING_SHARD, -1);
					st.rewardItems(ADENA, 12500);
				}
				else if (cond == 3)
				{
					htmltext = "7376-09.htm";
				}
				else if ((cond == 4) && (flare >= 200) && (freezing >= 200))
				{
					htmltext = "7376-10.htm";
					st.takeItems(FLARE_SHARD, -1);
					st.takeItems(FREEZING_SHARD, -1);
					st.rewardItems(ADENA, 63500);
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(true);
				}
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
		
		final ScriptState st = partyMember.getScriptState(getName());
		
		final int cond = st.getInt("cond");
		final int[] drop = DROPLIST.get(npc.getId());
		
		if (cond == 1)
		{
			if (st.dropItems(drop[0], 1, 50, drop[1]) && (st.getItemsCount(drop[0] == FLARE_SHARD ? FREEZING_SHARD : FLARE_SHARD) >= 50))
			{
				st.set("cond", "2");
			}
		}
		else if ((cond == 3) && st.dropItems(drop[0], 1, 200, drop[1]) && (st.getItemsCount(drop[0] == FLARE_SHARD ? FREEZING_SHARD : FLARE_SHARD) >= 200))
		{
			st.set("cond", "4");
		}
		
		return null;
	}
	
}
