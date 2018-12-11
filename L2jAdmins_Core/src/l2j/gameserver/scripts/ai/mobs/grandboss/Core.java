package l2j.gameserver.scripts.ai.mobs.grandboss;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2j.gameserver.data.GrandBossSpawnData;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.GrandBossHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class Core extends Script
{
	// GrandBoss
	private static final int CORE = 12052;
	// Minions
	private static final int DEATH_KNIGHT = 12054;
	private static final int DEATH_WRITHER = 12055;
	private static final int SUSCEPTOR = 12058;
	// State
	private static final byte DEAD = 1;
	private static final byte ALIVE = 0;
	// Spawn minions/grandboss
	private static final LocationHolder SPAWN = new LocationHolder(17726, 108915, -6480);
	// Instance of GrandBoss
	private L2GrandBossInstance core = null;
	// Instance of Minions
	private static List<L2Attackable> minions = new CopyOnWriteArrayList<>();
	// Misc
	private boolean firstAttacked = false;
	private static boolean spawnBoss = false;
	
	public Core()
	{
		super(-1, "ai/mobs/grandboss");
		
		addAttackId(CORE);
		addKillId(CORE);
		addKillId(DEATH_KNIGHT, DEATH_WRITHER, SUSCEPTOR);
		addSpawnId(DEATH_KNIGHT, DEATH_WRITHER, SUSCEPTOR);
		
		init();
	}
	
	private void init()
	{
		GrandBossHolder gb = GrandBossSpawnData.getBossInfo(CORE);
		
		if (gb.getStatus() == DEAD)
		{
			// load the respawn date and time for core from DB
			long temp = gb.getRespawnTime();
			
			if (temp > 0)
			{
				startTimer("core_spawn", temp, null, null, false);
			}
			else
			{
				startTimer("core_spawn", 0, null, null, false);
			}
		}
		else if (!spawnBoss)
		{
			core = (L2GrandBossInstance) addSpawn(CORE, gb.getLoc(), false, 0);
			GrandBossSpawnData.addBoss(core);
			core.setCurrentHpMp(gb.getCurrentHp(), gb.getCurrentMp());
			spawnBoss = true;
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "core_spawn":
				core = (L2GrandBossInstance) addSpawn(CORE, SPAWN.getX(), SPAWN.getY(), SPAWN.getZ(), 0, false, 0);
				core.broadcastPacket(new PlaySound(PlaySoundType.MUSIC_BS01_A, core.getX(), core.getY(), core.getZ()));
				GrandBossSpawnData.getBossInfo(CORE).setStatus(ALIVE);
				GrandBossSpawnData.addBoss(core);
				spawnBoss = true;
				break;
			case "minion_despawn":
				for (L2Attackable mob : minions)
				{
					if (mob != null)
					{
						mob.decayMe();
					}
				}
				minions.clear();
				break;
			case "minion_spawn":
				minions.add((L2Attackable) addSpawn(npc.getId(), SPAWN.getX(), SPAWN.getY(), SPAWN.getZ(), 0, false, 0));
				break;
		}
		
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (firstAttacked)
		{
			if (Rnd.get(100) == 0)
			{
				npc.broadcastNpcSay("Removing intruders.");
			}
		}
		else
		{
			firstAttacked = true;
			npc.broadcastNpcSay("A non-permitted target has been discovered.");
			npc.broadcastNpcSay("Starting intruder removal system.");
		}
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		minions.add((L2Attackable) npc);
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if ((GrandBossSpawnData.getBossInfo(CORE).getStatus() == ALIVE) && (minions != null) && minions.contains(npc))
		{
			minions.remove(npc);
			startTimer("minion_spawn", 360000, npc, null, false);// 6 min default
		}
		else if (npc.getId() == CORE)
		{
			npc.broadcastPacket(new PlaySound(PlaySoundType.MUSIC_BS02_D, npc.getX(), npc.getY(), npc.getZ()));
			npc.broadcastNpcSay("A fatal error has occurred.");
			npc.broadcastNpcSay("System is being shut down...");
			npc.broadcastNpcSay("......");
			
			firstAttacked = false; // ???
			
			long respawnTime = 27 + (Rnd.get(47) * 3600000);
			startTimer("core_spawn", respawnTime, null, null);
			startTimer("minion_despawn", 60000, null, null, false);
			
			GrandBossSpawnData.getBossInfo(CORE).setStatus(DEAD);
			// also save the respawn time so that the info is maintained past reboots
			GrandBossSpawnData.getBossInfo(CORE).setRespawnTime(System.currentTimeMillis() + respawnTime);
			// saved info into DB
			GrandBossSpawnData.saveBoss(CORE);
		}
		
		return null;
	}
}
