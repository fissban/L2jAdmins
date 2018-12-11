package l2j.gameserver.scripts.quests.normal;

import java.util.logging.Level;

import l2j.gameserver.data.RaidBossSpawnData;
import l2j.gameserver.data.RaidBossSpawnData.StatusEnum;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
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
public class Q625_TheFinestIngredients_Part2 extends Script
{
	// Monster
	private static final int ICICLE_EMPEROR_BUMBALUMP = 10296;
	
	// NPCs
	private static final int JEREMY = 8521;
	private static final int YETI_TABLE = 8542;
	
	// Items
	private static final int SOY_SAUCE_JAR = 7205;
	private static final int FOOD_FOR_BUMBALUMP = 7209;
	private static final int SPECIAL_YETI_MEAT = 7210;
	private static final int REWARD_DYE[] =
	{
		4589,
		4590,
		4591,
		4592,
		4593,
		4594
	};
	
	// Other
	private static final int CHECK_INTERVAL = 600000; // 10 minutes
	private static final int IDLE_INTERVAL = 3; // (X * CHECK_INTERVAL) = 30 minutes
	private static L2Npc npc = null;
	private static int status = -1;
	
	public Q625_TheFinestIngredients_Part2()
	{
		super(625, "The Finest Ingredients - Part 2");
		
		registerItems(FOOD_FOR_BUMBALUMP, SPECIAL_YETI_MEAT);
		
		addStartNpc(JEREMY);
		addTalkId(JEREMY, YETI_TABLE);
		
		addAttackId(ICICLE_EMPEROR_BUMBALUMP);
		addKillId(ICICLE_EMPEROR_BUMBALUMP);
		
		switch (RaidBossSpawnData.getInstance().getRaidBossStatusId(ICICLE_EMPEROR_BUMBALUMP))
		{
			case UNDEFINED:
				LOG.log(Level.WARNING, getName() + ": can not find spawned L2RaidBoss id=" + ICICLE_EMPEROR_BUMBALUMP);
				break;
			
			case ALIVE:
				spawnNpc();
			case DEAD:
				startTimer("check", CHECK_INTERVAL, null, null, true);
				break;
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		// global quest timer has player==null -> cannot get QuestState
		if (event.equals("check"))
		{
			final L2RaidBossInstance raid = RaidBossSpawnData.getInstance().getBosses().get(ICICLE_EMPEROR_BUMBALUMP);
			if ((raid != null) && (raid.getRaidStatus() == StatusEnum.ALIVE))
			{
				if ((status >= 0) && (status-- == 0))
				{
					despawnRaid(raid);
				}
				
				spawnNpc();
			}
			
			return null;
		}
		
		String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		// Jeremy
		if (event.equalsIgnoreCase("8521-03.htm"))
		{
			if (st.hasItems(SOY_SAUCE_JAR))
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
				st.takeItems(SOY_SAUCE_JAR, 1);
				st.giveItems(FOOD_FOR_BUMBALUMP, 1);
			}
			else
			{
				htmltext = "8521-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("8521-08.htm"))
		{
			if (st.hasItems(SPECIAL_YETI_MEAT))
			{
				st.takeItems(SPECIAL_YETI_MEAT, 1);
				st.rewardItems(REWARD_DYE[Rnd.get(REWARD_DYE.length)], 5);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				htmltext = "8521-09.htm";
			}
		}
		// Yeti's Table
		else if (event.equalsIgnoreCase("8542-02.htm"))
		{
			if (st.hasItems(FOOD_FOR_BUMBALUMP))
			{
				if (status < 0)
				{
					if (spawnRaid())
					{
						st.set("cond", "2");
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.takeItems(FOOD_FOR_BUMBALUMP, 1);
					}
				}
				else
				{
					htmltext = "8542-04.htm";
				}
			}
			else
			{
				htmltext = "8542-03.htm";
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
				htmltext = player.getLevel() < 73 ? "8521-02.htm" : "8521-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case JEREMY:
						if (cond == 1)
						{
							htmltext = "8521-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8521-06.htm";
						}
						else
						{
							htmltext = "8521-07.htm";
						}
						break;
					
					case YETI_TABLE:
						if (cond == 1)
						{
							htmltext = "8542-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8542-05.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		status = IDLE_INTERVAL;
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		for (final L2PcInstance partyMember : getPartyMembers(player, npc, "cond", "2"))
		{
			final ScriptState st = partyMember.getScriptState(getName());
			
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(SPECIAL_YETI_MEAT, 1);
		}
		
		// despawn raid (reset info)
		despawnRaid(npc);
		
		// despawn npc
		if (npc != null)
		{
			npc.deleteMe();
			npc = null;
		}
		
		return null;
	}
	
	private void spawnNpc()
	{
		// spawn npc, if not spawned
		if (npc == null)
		{
			npc = addSpawn(YETI_TABLE, 157136, -121456, -2363, 40000, false, 0);
		}
	}
	
	private static boolean spawnRaid()
	{
		final L2RaidBossInstance raid = RaidBossSpawnData.getInstance().getBosses().get(ICICLE_EMPEROR_BUMBALUMP);
		if (raid.getRaidStatus() == StatusEnum.ALIVE)
		{
			// set temporarily spawn location (to provide correct behavior of L2RaidBossInstance.checkAndReturnToSpawn())
			raid.getSpawn().setX(157117);
			raid.getSpawn().setY(-121939);
			raid.getSpawn().setZ(-2397);
			
			// teleport raid from secret place
			raid.teleToLocation(157117, -121939, -2397);
			raid.broadcastNpcSay("Hmmm, what do I smell over here?"); // FIXME el mensaje no es original
			// set raid status
			status = IDLE_INTERVAL;
			
			return true;
		}
		
		return false;
	}
	
	private static void despawnRaid(L2Npc raid)
	{
		// reset spawn location
		raid.getSpawn().setX(-104700);
		raid.getSpawn().setY(-252700);
		raid.getSpawn().setZ(-15542);
		
		// teleport raid back to secret place
		if (!raid.isDead())
		{
			raid.teleToLocation(-104700, -252700, -15542);
		}
		
		// reset raid status
		status = -1;
	}
	
}
