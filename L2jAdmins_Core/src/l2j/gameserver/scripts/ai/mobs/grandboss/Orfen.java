package l2j.gameserver.scripts.ai.mobs.grandboss;

import l2j.gameserver.data.GrandBossSpawnData;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.GrandBossHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.holder.SkillHolder;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class Orfen extends Script
{
	// GrandBoss
	private static final int ORFEN = 12169;
	// Minions
	private static final int RAIKEL_LEOS = 12171;
	private static final int RIBA_IREN = 12173;
	// Texts
	private static final String[] TEXT =
	{
		"%s1",
		"%s1. Do you think thats going to work?!",
		"%s1. Stop kidding yourself about your own powerlessness!",
		"Youre really stupid to have challenged me. %s1! Get ready!"
	};
	// States
	private static final byte ALIVE = 0;
	private static final byte DEAD = 1;
	// Spawns
	private static final LocationHolder SPAWN_CAVE = new LocationHolder(43746, 17226, -4394);
	private static final LocationHolder SPAWN_NORMAL = new LocationHolder(55024, 17368, -5412);
	// Skills
	private static final SkillHolder HEAL = new SkillHolder(4516, 1);
	private static final SkillHolder PARALIZE = new SkillHolder(4067, 1);
	// Instance of GrandBoss
	private static L2GrandBossInstance orfen = null;
	// Misc
	private static boolean teleport = false;
	private static long lastAction = 0;
	private static boolean dontSay = false;
	
	public Orfen()
	{
		super(-1, "ai/mobs/grandboss");
		
		addAttackId(ORFEN);
		addKillId(ORFEN);
		addFactionCallId(RAIKEL_LEOS, RIBA_IREN);
		
		init();
	}
	
	private void init()
	{
		GrandBossHolder gb = GrandBossSpawnData.getBossInfo(ORFEN);
		if (gb.getStatus() == DEAD)
		{
			// load the respawn date and time for core from DB
			long temp = gb.getRespawnTime() - System.currentTimeMillis();
			if (temp > 0)
			{
				startTimer("spawn_ofern", temp, null, null, false);
			}
			else
			{
				startTimer("spawn_ofern", 0, null, null, false);
			}
		}
		else
		{
			orfen = (L2GrandBossInstance) addSpawn(ORFEN, gb.getLoc(), false, 0);
			orfen.setCurrentHpMp(gb.getCurrentHp(), gb.getCurrentMp());
			
			GrandBossSpawnData.addBoss(orfen);
			GrandBossSpawnData.getBossInfo(ORFEN).setStatus(ALIVE);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "spawn_ofern":
				orfen = (L2GrandBossInstance) addSpawn(ORFEN, SPAWN_NORMAL, false, 0);
				orfen.broadcastPacket(new PlaySound(PlaySoundType.MUSIC_BS01_A, orfen.getX(), orfen.getY(), orfen.getZ()));
				
				GrandBossSpawnData.addBoss(orfen);
				GrandBossSpawnData.getBossInfo(ORFEN).setStatus(ALIVE);
				break;
			case "return_ofern":
				if ((System.currentTimeMillis() - lastAction) > 900000)
				{
					npc.getAI().setIntention(CtrlIntentionType.IDLE);
					((L2Attackable) npc).clearAggroList();
					npc.teleToLocation(SPAWN_CAVE, false);
					cancelTimer("text_orfen", npc, null);
				}
				break;
			case "text_orfen":
				if (orfen != null)
				{
					orfen.broadcastNpcSay(TEXT[Rnd.get(3)].replace("%s1", player.getName()));
				}
				break;
		}
		
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		lastAction = System.currentTimeMillis();
		
		if (!teleport && (npc.getCurrentHp() < (npc.getStat().getMaxHp() / 2)))
		{
			teleport = true;
			npc.getAI().setIntention(CtrlIntentionType.IDLE);
			((L2Attackable) npc).clearAggroList();
			npc.teleToLocation(SPAWN_CAVE, false);
			
			startTimer("return_ofern", 900000, npc, null, false);
		}
		
		if (getTimer("text_orfen", npc, null) == null)
		{
			startTimer("text_orfen", 60000, npc, attacker, true);
		}
		else
		{
			if (!dontSay && (npc.getCurrentHp() < (npc.getStat().getMaxHp() * 0.1)))
			{
				dontSay = true;
				orfen.broadcastNpcSay(TEXT[3].replace("%s1", attacker.getName()));
			}
		}
		
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isPet)
	{
		if ((caller == null) || (npc == null) || npc.isCastingNow())
		{
			return super.onFactionCall(npc, caller, attacker, isPet);
		}
		
		int npcId = npc.getId();
		int callerId = caller.getId();
		if ((npcId == RAIKEL_LEOS) && (Rnd.get(20) == 0))
		{
			npc.setTarget(attacker);
			npc.doCast(PARALIZE.getSkill());
		}
		else if (npcId == RIBA_IREN)
		{
			int chance = 1;
			if (callerId == ORFEN)
			{
				chance = 9;
			}
			
			if ((callerId != RIBA_IREN) && ((caller.getCurrentHp() / caller.getStat().getMaxHp()) < 0.5) && (Rnd.get(10) < chance))
			{
				npc.getAI().setIntention(CtrlIntentionType.IDLE);
				npc.setTarget(caller);
				npc.doCast(HEAL.getSkill());
			}
		}
		return super.onFactionCall(npc, caller, attacker, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		long respawnTime = (28 + Rnd.get(41)) * 3600000;
		startTimer("spawn_ofern", respawnTime, npc, null);
		cancelTimer("return_ofern", npc, null);
		
		GrandBossSpawnData.getBossInfo(ORFEN).setStatus(DEAD);
		// also save the respawn time so that the info is maintained past reboots
		GrandBossSpawnData.getBossInfo(ORFEN).setRespawnTime(System.currentTimeMillis() + respawnTime);
		// saved info into DB
		GrandBossSpawnData.saveBoss(ORFEN);
		
		return super.onKill(npc, killer, isSummon);
	}
}
