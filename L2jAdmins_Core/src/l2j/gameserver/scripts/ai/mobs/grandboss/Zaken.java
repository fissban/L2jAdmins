package l2j.gameserver.scripts.ai.mobs.grandboss;

import l2j.gameserver.data.GrandBossSpawnData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.GrandBossHolder;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * Original Code in python
 * @author fissban
 */
public class Zaken extends Script
{
	// GrandBoss
	private static final int ZAKEN = 12374;
	// State
	private static final byte DEAD = 1;
	private static final byte ALIVE = 0;
	// Instance of GrandBoss.
	private static L2GrandBossInstance zaken = null;
	
	public Zaken()
	{
		super(-1, "ai/mobs/grandboss");
		
		addKillId(ZAKEN);
		addAttackId(ZAKEN);
		init();
	}
	
	private void init()
	{
		GrandBossHolder gb = GrandBossSpawnData.getBossInfo(ZAKEN);
		
		if (gb.getStatus() == DEAD)
		{
			// load the respawn date and time for zaken from DB
			long temp = gb.getRespawnTime() - System.currentTimeMillis();
			
			if (temp > 0)
			{
				startTimer("zaken_spawn", temp, null, null, false);
			}
			else
			{
				startTimer("zaken_spawn", 0, null, null, false);
			}
		}
		else
		{
			zaken = (L2GrandBossInstance) addSpawn(ZAKEN, gb.getLoc(), false, 0);
			GrandBossSpawnData.addBoss(zaken);
			zaken.setCurrentHpMp(gb.getCurrentHp(), gb.getCurrentMp());
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event == "zaken_spawn")
		{
			zaken = (L2GrandBossInstance) addSpawn(ZAKEN, 55312, 219168, -3223, 0, false, 0);
			GrandBossSpawnData.getBossInfo(ZAKEN).setStatus(ALIVE);
			GrandBossSpawnData.addBoss(zaken);
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		long respawnTime = ((19 + Rnd.get(35)) * 3600000);
		startTimer("zaken_spawn", respawnTime, null, null);
		
		GrandBossSpawnData.getBossInfo(ZAKEN).setStatus(DEAD);
		// also save the respawn time so that the info is maintained past reboots
		GrandBossSpawnData.getBossInfo(ZAKEN).setRespawnTime(System.currentTimeMillis() + respawnTime);
		// saved info into DB
		GrandBossSpawnData.saveBoss(ZAKEN);
		return null;
	}
}
