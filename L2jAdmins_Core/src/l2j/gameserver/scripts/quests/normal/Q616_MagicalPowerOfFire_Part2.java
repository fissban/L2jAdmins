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
public class Q616_MagicalPowerOfFire_Part2 extends Script
{
	// Monster
	private static final int SOUL_OF_FIRE_NASTRON = 10306;
	
	// NPCs
	private static final int UDAN_MARDUI = 8379;
	private static final int KETRAS_HOLY_ALTAR = 8558;
	
	// Items
	private static final int RED_TOTEM = 7243;
	private static final int FIRE_HEART_OF_NASTRON = 7244;
	
	// Other
	private static final int CHECK_INTERVAL = 600000; // 10 minutes
	private static final int IDLE_INTERVAL = 2; // (X * CHECK_INTERVAL) = 20 minutes
	private static L2Npc npc = null;
	private static int status = -1;
	
	public Q616_MagicalPowerOfFire_Part2()
	{
		super(616, "Magical Power of Fire - Part 2");
		
		registerItems(FIRE_HEART_OF_NASTRON);
		
		addStartNpc(UDAN_MARDUI);
		addTalkId(UDAN_MARDUI, KETRAS_HOLY_ALTAR);
		
		addAttackId(SOUL_OF_FIRE_NASTRON);
		addKillId(SOUL_OF_FIRE_NASTRON);
		
		switch (RaidBossSpawnData.getInstance().getRaidBossStatusId(SOUL_OF_FIRE_NASTRON))
		{
			case UNDEFINED:
				LOG.log(Level.WARNING, getName() + ": can not find spawned L2RaidBoss id=" + SOUL_OF_FIRE_NASTRON);
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
			L2RaidBossInstance raid = RaidBossSpawnData.getInstance().getBosses().get(SOUL_OF_FIRE_NASTRON);
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
		
		// Udan Mardui
		if (event.equalsIgnoreCase("8379-04.htm"))
		{
			if (st.hasItems(RED_TOTEM))
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
			}
			else
			{
				htmltext = "8379-02.htm";
			}
		}
		else if (event.equalsIgnoreCase("8379-08.htm"))
		{
			if (st.hasItems(FIRE_HEART_OF_NASTRON))
			{
				st.takeItems(FIRE_HEART_OF_NASTRON, 1);
				st.rewardExpAndSp(10000, 0);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				htmltext = "8379-09.htm";
			}
		}
		// Ketra's Holy Altar
		else if (event.equalsIgnoreCase("8558-02.htm"))
		{
			if (st.hasItems(RED_TOTEM))
			{
				if (status < 0)
				{
					if (spawnRaid())
					{
						st.set("cond", "2");
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.takeItems(RED_TOTEM, 1);
					}
				}
				else
				{
					htmltext = "8558-04.htm";
				}
			}
			else
			{
				htmltext = "8558-03.htm";
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
				if (!st.hasItems(RED_TOTEM))
				{
					htmltext = "8379-02.htm";
				}
				else if ((player.getLevel() < 75) && (player.getAllianceWithVarkaKetra() > -2))
				{
					htmltext = "8379-03.htm";
				}
				else
				{
					htmltext = "8379-01.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case UDAN_MARDUI:
						if (cond == 1)
						{
							htmltext = "8379-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8379-06.htm";
						}
						else
						{
							htmltext = "8379-07.htm";
						}
						break;
					
					case KETRAS_HOLY_ALTAR:
						if (cond == 1)
						{
							htmltext = "8558-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8558-05.htm";
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
			st.giveItems(FIRE_HEART_OF_NASTRON, 1);
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
			npc = addSpawn(KETRAS_HOLY_ALTAR, 142368, -82512, -6487, 58000, false, 0);
		}
	}
	
	private static boolean spawnRaid()
	{
		L2RaidBossInstance raid = RaidBossSpawnData.getInstance().getBosses().get(SOUL_OF_FIRE_NASTRON);
		if (raid.getRaidStatus() == StatusEnum.ALIVE)
		{
			// set temporarily spawn location (to provide correct behavior of L2RaidBossInstance.checkAndReturnToSpawn())
			raid.getSpawn().setX(142624);
			raid.getSpawn().setY(-82285);
			raid.getSpawn().setZ(-6491);
			
			// teleport raid from secret place
			raid.teleToLocation(142624, -82285, -6491);
			
			// set raid status
			status = IDLE_INTERVAL;
			
			return true;
		}
		
		return false;
	}
	
	private static void despawnRaid(L2Npc raid)
	{
		// reset spawn location
		raid.getSpawn().setX(-105300);
		raid.getSpawn().setY(-252700);
		raid.getSpawn().setZ(-15542);
		
		// teleport raid back to secret place
		if (!raid.isDead())
		{
			raid.teleToLocation(-105300, -252700, -15542);
		}
		
		// reset raid status
		status = -1;
	}
	
}
