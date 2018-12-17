package l2j.gameserver.scripts.ai.mobs.grandboss;

import l2j.gameserver.data.GrandBossSpawnData;
import l2j.gameserver.instancemanager.zone.ZoneGrandBossManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.GrandBossHolder;
import l2j.gameserver.model.zone.type.BossZone;
import l2j.gameserver.network.external.server.Earthquake;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.network.external.server.SpecialCamera;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class Antharas extends Script
{
	// GrandBoss
	private static final int ANTHARAS = 12211;
	// Npc Teleport Cube
	private static final int CUBE = 12324;
	// State
	private static final byte DORMANT = 0;
	private static final byte WAITING = 1;
	private static final byte FIGHTING = 2;
	private static final byte DEAD = 3;
	// Instance of zone
	private static BossZone zone;
	// Instance of GrandBoss.
	private L2GrandBossInstance antharas = null;
	// Misc
	private long lastAction = 0;
	
	public Antharas()
	{
		super(-1, "ai/grandboss");
		
		addKillId(ANTHARAS);
		addAttackId(ANTHARAS);
		init();
	}
	
	private void init()
	{
		lastAction = 0;
		zone = ZoneGrandBossManager.getZone(12006);
		
		GrandBossHolder gb = GrandBossSpawnData.getBossInfo(ANTHARAS);
		
		if (gb.getStatus() == DEAD)
		{
			// load the unlock date and time for antharas from DB
			long temp = (gb.getRespawnTime()) - System.currentTimeMillis();
			// if antharas is locked until a certain time, mark it so and start the unlock timer
			// the unlock time has not yet expired. Mark antharas as currently locked. Setup a timer
			// to fire at the correct time (calculate the time between now and the unlock time,
			// setup a timer to fire after that many msec)
			if (temp > 0)
			{
				startTimer("antharas_unlock", temp, null, null);
			}
			else
			{
				// the time has already expired while the server was offline. Immediately spawn antharas in his cave.
				// also, the status needs to be changed to DORMANT
				startTimer("antharas_unlock", 0, null, null);
			}
		}
		else
		{
			antharas = (L2GrandBossInstance) addSpawn(ANTHARAS, gb.getLoc(), false, 0);
			
			if (antharas == null)
			{
				return;
			}
			
			antharas.setCurrentHpMp(gb.getCurrentHp(), gb.getCurrentMp());
			GrandBossSpawnData.addBoss(antharas);
			
			if (gb.getStatus() == WAITING)
			{
				startTimer("waiting", 1800000, antharas, null);
			}
			else if (gb.getStatus() == FIGHTING)
			{
				lastAction = System.currentTimeMillis();
				// Start repeating timer to check for inactivity
				startTimer("antharas_despawn", 60000, antharas, null, true);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "antharas_unlock":
				antharas = (L2GrandBossInstance) addSpawn(ANTHARAS, 181323, 114850, -7623, 32542, false, 0);
				antharas.broadcastPacket(new Earthquake(181323, 114850, -7623, 20, 10));
				GrandBossSpawnData.addBoss(antharas);
				GrandBossSpawnData.getBossInfo(ANTHARAS).setStatus(DORMANT);
				break;
			
			case "waiting":
				npc.teleToLocation(181323, 114850, -7623);
				npc.broadcastPacket(new PlaySound(PlaySoundType.MUSIC_BS02_A, 181323, 114850, -7623));
				GrandBossSpawnData.getBossInfo(ANTHARAS).setStatus(FIGHTING);
				
				npc.setIsInvul(true);
				startTimer("camera_1", 16, npc, null);
				break;
			
			case "camera_1":
				startTimer("camera_2", 3000, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 700, 13, -19, 0, 20000, 0, 0, 1, 0));
				break;
			
			case "camera_2":
				startTimer("camera_3", 10000, npc, null);
				npc.broadcastPacket(new SocialAction(npc.getObjectId(), SocialActionType.NPC_ANIMATION));
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 700, 13, 0, 6000, 20000, 0, 0, 1, 0));
				break;
			
			case "camera_3":
				startTimer("camera_4", 200, npc, null);
				npc.broadcastPacket(new SocialAction(npc.getObjectId(), SocialActionType.HELLO));
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 3700, 0, -3, 0, 10000, 0, 0, 1, 0));
				break;
			
			case "camera_4":
				startTimer("camera_5", 10800, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 0, -3, 22000, 30000, 0, 0, 1, 0));
				break;
			
			case "camera_5":
				startTimer("camera_6", 1900, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 0, -3, 300, 7000, 0, 0, 1, 0));
				break;
			
			case "camera_6":
				GrandBossSpawnData.getBossInfo(ANTHARAS).setStatus(FIGHTING);
				lastAction = System.currentTimeMillis();
				startTimer("antharas_despawn", 60000, npc, null, true);
				npc.moveToLocation(177560, -114616, -7704, 0);
				npc.setIsInvul(false);
				npc.setRunning();
				break;
			
			case "antharas_despawn":
				if ((System.currentTimeMillis() - lastAction) > 900000)
				{
					GrandBossSpawnData.getBossInfo(ANTHARAS).setStatus(DORMANT);
					npc.teleToLocation(185708, 114298, -8221);
					npc.setCurrentHpMp(npc.getStat().getMaxHp(), npc.getStat().getMaxMp());
					zone.oustAllPlayers();
					cancelTimer("antharas_despawn", npc, null);
				}
				break;
			
			case "spawn_cubes":
				addSpawn(CUBE, 177615, 114941, -7709, 0, false, 60);
				cancelTimer("antharas_despawn", npc, null);
				startTimer("remove_players", 900000, null, null);
				break;
			
			case "remove_players":
				zone.oustAllPlayers();
		}
		
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		lastAction = System.currentTimeMillis();
		
		if (GrandBossSpawnData.getBossInfo(ANTHARAS).getStatus() != FIGHTING)
		{
			attacker.teleToLocation(80464, 152294, -3534);
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1200, 20, -10, 0, 13000, 0, 0, 1, 0));
		npc.broadcastPacket(new PlaySound(PlaySoundType.MUSIC_BS01_D, npc.getX(), npc.getY(), npc.getZ()));
		
		startTimer("spawn_cubes", 10000, npc, null);
		
		long respawnTime = ((192 + Rnd.get(145)) * 3600000);
		startTimer("antharas_unlock", respawnTime, null, null);
		
		GrandBossSpawnData.getBossInfo(ANTHARAS).setStatus(DEAD);
		// also save the respawn time so that the info is maintained past reboots
		GrandBossSpawnData.getBossInfo(ANTHARAS).setRespawnTime(((System.currentTimeMillis()) + respawnTime));
		// saved info into DB
		GrandBossSpawnData.saveBoss(ANTHARAS);
		return null;
	}
}
