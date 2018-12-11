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
public class Q627_HeartInSearchOfPower extends Script
{
	// NPCs
	private static final int NECROMANCER = 8518;
	private static final int ENFEUX = 8519;
	
	// Items
	private static final int SEAL_OF_LIGHT = 7170;
	private static final int BEAD_OF_OBEDIENCE = 7171;
	private static final int GEM_OF_SAINTS = 7172;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(1520, 550000);
		CHANCES.put(1523, 584000);
		CHANCES.put(1524, 621000);
		CHANCES.put(1524, 621000);
		CHANCES.put(1526, 606000);
		CHANCES.put(1529, 625000);
		CHANCES.put(1530, 578000);
		CHANCES.put(1531, 690000);
		CHANCES.put(1532, 671000);
		CHANCES.put(1535, 693000);
		CHANCES.put(1536, 615000);
		CHANCES.put(1539, 762000);
		CHANCES.put(1539, 762000);
		CHANCES.put(1531, 690000);
	}
	
	// Rewards
	private static final Map<String, int[]> REWARDS = new HashMap<>();
	{
		REWARDS.put("adena", new int[]
		{
			0,
			0,
			100000
		});
		REWARDS.put("asofe", new int[]
		{
			4043,
			13,
			6400
		});
		REWARDS.put("thon", new int[]
		{
			4044,
			13,
			6400
		});
		REWARDS.put("enria", new int[]
		{
			4042,
			6,
			13600
		});
		REWARDS.put("mold", new int[]
		{
			4041,
			3,
			17200
		});
	}
	
	public Q627_HeartInSearchOfPower()
	{
		super(627, "Heart in Search of Power");
		
		registerItems(BEAD_OF_OBEDIENCE);
		
		addStartNpc(NECROMANCER);
		addTalkId(NECROMANCER, ENFEUX);
		
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
		
		if (event.equalsIgnoreCase("8518-01.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8518-03.htm"))
		{
			if (st.getItemsCount(BEAD_OF_OBEDIENCE) == 300)
			{
				st.set("cond", "3");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(BEAD_OF_OBEDIENCE, -1);
				st.giveItems(SEAL_OF_LIGHT, 1);
			}
			else
			{
				htmltext = "8518-03a.htm";
				st.set("cond", "1");
				st.takeItems(BEAD_OF_OBEDIENCE, -1);
			}
		}
		else if (event.equalsIgnoreCase("8519-01.htm"))
		{
			if (st.getItemsCount(SEAL_OF_LIGHT) == 1)
			{
				st.set("cond", "4");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(SEAL_OF_LIGHT, 1);
				st.giveItems(GEM_OF_SAINTS, 1);
			}
		}
		else if (REWARDS.containsKey(event))
		{
			if (st.getItemsCount(GEM_OF_SAINTS) == 1)
			{
				htmltext = "8518-07.htm";
				st.takeItems(GEM_OF_SAINTS, 1);
				
				if (REWARDS.get(event)[0] > 0)
				{
					st.giveItems(REWARDS.get(event)[0], REWARDS.get(event)[1]);
				}
				st.rewardItems(Inventory.ADENA_ID, REWARDS.get(event)[2]);
				
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				htmltext = "8518-7.htm";
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
				htmltext = player.getLevel() < 60 ? "8518-00a.htm" : "8518-00.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case NECROMANCER:
						if (cond == 1)
						{
							htmltext = "8518-01a.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8518-02.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8518-04.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8518-05.htm";
						}
						break;
					
					case ENFEUX:
						if (cond == 3)
						{
							htmltext = "8519-00.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8519-02.htm";
						}
						break;
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
		
		if (st.dropItems(BEAD_OF_OBEDIENCE, 1, 300, CHANCES.get(npc.getId())))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
