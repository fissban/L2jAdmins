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

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q610_MagicalPowerOfWater_Part2 extends Script
{
	// Monster
	private static final int SOUL_OF_WATER_ASHUTAR = 10316;
	
	// NPCs
	private static final int ASEFA = 8372;
	private static final int VARKAS_HOLY_ALTAR = 8560;
	
	// Items
	private static final int GREEN_TOTEM = 7238;
	private static final int ICE_HEART_OF_ASHUTAR = 7239;
	
	// Other
	private static final int CHECK_INTERVAL = 600000; // 10 minutes
	private static final int IDLE_INTERVAL = 2; // (X * CHECK_INTERVAL) = 20 minutes
	private static L2Npc npc = null;
	private static int status = -1;
	
	public Q610_MagicalPowerOfWater_Part2()
	{
		super(610, "Magical Power of Water - Part 2");
		
		registerItems(ICE_HEART_OF_ASHUTAR);
		
		addStartNpc(ASEFA);
		addTalkId(ASEFA, VARKAS_HOLY_ALTAR);
		
		addAttackId(SOUL_OF_WATER_ASHUTAR);
		addKillId(SOUL_OF_WATER_ASHUTAR);
		
		switch (RaidBossSpawnData.getInstance().getRaidBossStatusId(SOUL_OF_WATER_ASHUTAR))
		{
			case UNDEFINED:
				LOG.log(Level.WARNING, getName() + ": can not find spawned L2RaidBoss id=" + SOUL_OF_WATER_ASHUTAR);
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
			L2RaidBossInstance raid = RaidBossSpawnData.getInstance().getBosses().get(SOUL_OF_WATER_ASHUTAR);
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
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		// Asefa
		if (event.equalsIgnoreCase("8372-04.htm"))
		{
			if (st.hasItems(GREEN_TOTEM))
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
			}
			else
			{
				htmltext = "8372-02.htm";
			}
		}
		else if (event.equalsIgnoreCase("8372-07.htm"))
		{
			if (st.hasItems(ICE_HEART_OF_ASHUTAR))
			{
				st.takeItems(ICE_HEART_OF_ASHUTAR, 1);
				st.rewardExpAndSp(10000, 0);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				htmltext = "8372-08.htm";
			}
		}
		// Varka's Holy Altar
		else if (event.equalsIgnoreCase("8560-02.htm"))
		{
			if (st.hasItems(GREEN_TOTEM))
			{
				if (status < 0)
				{
					if (spawnRaid())
					{
						st.set("cond", "2");
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.takeItems(GREEN_TOTEM, 1);
					}
				}
				else
				{
					htmltext = "8560-04.htm";
				}
			}
			else
			{
				htmltext = "8560-03.htm";
			}
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
		
		switch (st.getState())
		{
			case CREATED:
				if (!st.hasItems(GREEN_TOTEM))
				{
					htmltext = "8372-02.htm";
				}
				else if ((player.getLevel() < 75) && (player.getAllianceWithVarkaKetra() < 2))
				{
					htmltext = "8372-03.htm";
				}
				else
				{
					htmltext = "8372-01.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case ASEFA:
						htmltext = (cond < 3) ? "8372-05.htm" : "8372-06.htm";
						break;
					
					case VARKAS_HOLY_ALTAR:
						if (cond == 1)
						{
							htmltext = "8560-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8560-05.htm";
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
		for (L2PcInstance partyMember : getPartyMembers(player, npc, "cond", "2"))
		{
			ScriptState st = partyMember.getScriptState(getName());
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(ICE_HEART_OF_ASHUTAR, 1);
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
			npc = addSpawn(VARKAS_HOLY_ALTAR, 105452, -36775, -1050, 34000, false, 0);
		}
	}
	
	private static boolean spawnRaid()
	{
		L2RaidBossInstance raid = RaidBossSpawnData.getInstance().getBosses().get(SOUL_OF_WATER_ASHUTAR);
		if (raid.getRaidStatus() == StatusEnum.ALIVE)
		{
			// set temporarily spawn location (to provide correct behavior of L2RaidBossInstance.checkAndReturnToSpawn())
			raid.getSpawn().setX(104771);
			raid.getSpawn().setY(-36993);
			raid.getSpawn().setZ(-1149);
			
			// teleport raid from secret place
			raid.teleToLocation(104771, -36993, -1149);
			raid.broadcastNpcSay("The water charm then is the storm and the tsunami strength! Opposes with it only has the blind alley!");
			
			// set raid status
			status = IDLE_INTERVAL;
			
			return true;
		}
		
		return false;
	}
	
	private static void despawnRaid(L2Npc raid)
	{
		// reset spawn location
		raid.getSpawn().setX(-105900);
		raid.getSpawn().setY(-252700);
		raid.getSpawn().setZ(-15542);
		
		// teleport raid back to secret place
		if (!raid.isDead())
		{
			raid.teleToLocation(-105900, -252700, -15542);
		}
		
		// reset raid status
		status = -1;
	}
	
}
