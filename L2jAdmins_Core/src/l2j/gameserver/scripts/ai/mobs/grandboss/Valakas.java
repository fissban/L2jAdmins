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
 * Original Code in python
 * @author fissban
 */
public class Valakas extends Script
{
	// GrandBoss
	private static final int VALAKAS = 12899;
	// Npc Teleport Cube
	private static final int CUBE = 8759;
	// State
	private static final byte DORMANT = 0; // Valakas is spawned and no one has entered yet. Entry is unlocked
	private static final byte WAITING = 1; // Valakas is spawned and someone has entered, triggering a 30 minute window for additional people to enter
	private static final byte FIGHTING = 2;// Valakas is engaged in battle, annihilating his foes. Entry is locked
	private static final byte DEAD = 3;// Valakas has been killed. Entry is locked
	// Instance of zone
	private static BossZone zone;
	// Instance of GrandBoss.
	private static L2GrandBossInstance valakas = null;
	// Misc
	private static long lastAction = 0;
	
	public Valakas()
	{
		super(-1, "ai/mobs/grandboss");
		
		addKillId(VALAKAS);
		addAttackId(VALAKAS);
		init();
	}
	
	private void init()
	{
		lastAction = 0;
		zone = ZoneGrandBossManager.getZone(12008);
		
		GrandBossHolder gb = GrandBossSpawnData.getBossInfo(VALAKAS);
		
		if (gb.getStatus() == DEAD)
		{
			// load the unlock date and time for antharas from DB
			long temp = gb.getRespawnTime() - System.currentTimeMillis();
			// the unlock time has not yet expired. Mark Valakas as currently locked (dead). Setup a timer
			// to fire at the correct time (calculate the time between now and the unlock time,
			// setup a timer to fire after that many msec)
			if (temp > 0)
			{
				startTimer("valakas_unlock", temp, null, null);
			}
			else
			{
				// the time has already expired while the server was offline.
				// the status needs to be changed to DORMANT
				startTimer("valakas_unlock", 0, null, null);
			}
		}
		else
		{
			valakas = (L2GrandBossInstance) addSpawn(VALAKAS, gb.getLoc(), false, 0);
			GrandBossSpawnData.addBoss(valakas);
			valakas.setCurrentHpMp(gb.getCurrentHp(), gb.getCurrentMp());
			
			lastAction = System.currentTimeMillis();
			
			if (gb.getStatus() == WAITING)
			{
				startTimer("waiting", 1800000, valakas, null);
			}
			
			// Start repeating timer to check for inactivity
			startTimer("valakas_despawn", 60000, valakas, null, true);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "waiting":
				valakas = (L2GrandBossInstance) addSpawn(VALAKAS, 212852, -114842, -1632, 0, false, 0);
				valakas.broadcastPacket(new SocialAction(valakas.getObjectId(), SocialActionType.VICTORY));
				valakas.broadcastPacket(new Earthquake(valakas.getX(), valakas.getY(), valakas.getZ(), 20, 10));
				GrandBossSpawnData.addBoss(valakas);
				break;
			case "camera_1":
				startTimer("camera_2", 1500, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1300, 180, -5, 3000, 15000, 0, -5, 1, 0));
				break;
			case "camera_2":
				startTimer("camera_3", 3300, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 500, 180, -8, 600, 15000, 0, 60, 1, 0));
				break;
			case "camera_3":
				startTimer("camera_4", 2900, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 800, 180, -8, 2700, 15000, 0, 30, 1, 0));
				break;
			case "camera_4":
				startTimer("camera_5", 2700, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 200, 250, 70, 0, 15000, 30, 80, 1, 0));
				break;
			case "camera_5":
				startTimer("camera_6", 1, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 250, 70, 2500, 15000, 30, 80, 1, 0));
				break;
			case "camera_6":
				startTimer("camera_7", 3200, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 700, 150, 30, 0, 15000, -10, 60, 1, 0));
				break;
			case "camera_7":
				startTimer("camera_8", 1400, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1200, 150, 20, 2900, 15000, -10, 30, 1, 0));
				break;
			case "camera_8":
				startTimer("camera_9", 6700, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 750, 170, 15, 3400, 15000, 10, -15, 1, 0));
				break;
			case "camera_9":
				startTimer("valakas_fight", 5700, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 750, 170, -10, 3400, 15000, 4000, -15, 1, 0));
				break;
			case "valakas_fight":
				GrandBossSpawnData.getBossInfo(VALAKAS).setStatus(FIGHTING);
				startTimer("valakas_despawn", 60000, npc, null, true);
				lastAction = System.currentTimeMillis();
				break;
			case "valakas_despawn":
				if (((lastAction + 1800000) < System.currentTimeMillis()))
				{
					npc.deleteMe(); // despawn the live-baium
					GrandBossSpawnData.getBossInfo(VALAKAS).setStatus(DORMANT);
					zone.oustAllPlayers();
					cancelTimer("valakas_despawn", npc, null);
				}
				break;
			case "spawn_cubes":
				addSpawn(CUBE, 212852, -114842, -1632, 0, false, 900000);
				cancelTimer("valakas_despawn", npc, null);
				startTimer("remove_players", 900000, null, null);
				break;
			case "valakas_unlock":
				GrandBossSpawnData.getBossInfo(VALAKAS).setStatus(DORMANT);
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
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		npc.broadcastPacket(new PlaySound(PlaySoundType.MUSIC_BS01_D, npc.getX(), npc.getY(), npc.getZ()));
		
		long respawnTime = ((192 + Rnd.get(145)) * 3600000);
		startTimer("valaks_unlock", respawnTime, null, null);
		startTimer("spawn_cubes", 10000, npc, null);
		
		GrandBossSpawnData.getBossInfo(VALAKAS).setStatus(DEAD);
		// also save the respawn time so that the info is maintained past reboots
		GrandBossSpawnData.getBossInfo(VALAKAS).setRespawnTime(System.currentTimeMillis() + respawnTime);
		// saved info into DB
		GrandBossSpawnData.saveBoss(VALAKAS);
		return null;
	}
}
