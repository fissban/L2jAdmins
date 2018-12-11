package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.gameserver.util.Util;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q631_DeliciousTopChoiceMeat extends Script
{
	// NPC
	private static final int TUNATUN = 8537;
	
	// Item
	private static final int TOP_QUALITY_MEAT = 7546;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(1460, 601000);
		CHANCES.put(1460, 480000);
		CHANCES.put(1460, 447000);
		CHANCES.put(1460, 808000);
		CHANCES.put(1460, 447000);
		CHANCES.put(1460, 808000);
		CHANCES.put(1460, 447000);
		CHANCES.put(1460, 808000);
		CHANCES.put(1479, 477000);
		CHANCES.put(1479, 863000);
		CHANCES.put(1479, 477000);
		CHANCES.put(1479, 863000);
		CHANCES.put(1479, 477000);
		CHANCES.put(1479, 863000);
		CHANCES.put(1479, 477000);
		CHANCES.put(1479, 863000);
		CHANCES.put(1498, 509000);
		CHANCES.put(1498, 920000);
		CHANCES.put(1498, 509000);
		CHANCES.put(1498, 920000);
		CHANCES.put(1498, 509000);
		CHANCES.put(1498, 920000);
		CHANCES.put(1498, 509000);
		CHANCES.put(1498, 920000);
	}
	
	// Rewards
	private static final int[][] REWARDS =
	{
		{
			4039,
			15
		},
		{
			4043,
			15
		},
		{
			4044,
			15
		},
		{
			4040,
			10
		},
		{
			4042,
			10
		},
		{
			4041,
			5
		}
	};
	
	public Q631_DeliciousTopChoiceMeat()
	{
		super(631, "Delicious Top Choice Meat");
		
		registerItems(TOP_QUALITY_MEAT);
		
		addStartNpc(TUNATUN);
		addTalkId(TUNATUN);
		
		for (final int npcId : CHANCES.keySet())
		{
			addKillId(npcId);
		}
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
		
		if (event.equalsIgnoreCase("8537-03.htm"))
		{
			if (player.getLevel() >= 65)
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
			}
			else
			{
				htmltext = "8537-02.htm";
				st.exitQuest(true);
			}
		}
		else if (Util.isDigit(event))
		{
			if (st.getItemsCount(TOP_QUALITY_MEAT) >= 120)
			{
				htmltext = "8537-06.htm";
				st.takeItems(TOP_QUALITY_MEAT, -1);
				
				final int[] reward = REWARDS[Integer.parseInt(event)];
				st.rewardItems(reward[0], reward[1]);
				
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "1");
				htmltext = "8537-07.htm";
			}
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
				htmltext = "8537-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "8537-03a.htm";
				}
				else if (cond == 2)
				{
					if (st.getItemsCount(TOP_QUALITY_MEAT) >= 120)
					{
						htmltext = "8537-04.htm";
					}
					else
					{
						st.set("cond", "1");
						htmltext = "8537-03a.htm";
					}
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final L2PcInstance partyMember = getRandomPartyMember(player, npc, "1");
		if (partyMember == null)
		{
			return null;
		}
		
		final ScriptState st = partyMember.getScriptState(getName());
		
		if (st.dropItems(TOP_QUALITY_MEAT, 1, 120, CHANCES.get(npc.getId())))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
