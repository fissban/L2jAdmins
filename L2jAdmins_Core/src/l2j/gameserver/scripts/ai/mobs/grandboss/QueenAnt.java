package l2j.gameserver.scripts.ai.mobs.grandboss;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.data.GrandBossSpawnData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.GrandBossHolder;
import l2j.gameserver.model.holder.SkillHolder;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class QueenAnt extends Script
{
	// GrandBoss
	private static final int QUEEN = 12001;
	// Minions
	private static final int LARVA = 12002;
	private static final int NURSE = 12003;
	private static final int GUARD = 12004;
	// Skills Minions
	private static final SkillHolder HEAL1 = new SkillHolder(4020, 1);
	private static final SkillHolder HEAL2 = new SkillHolder(4024, 1);
	// Queen ant skill
	private static final SkillHolder QUEEN_BRANDISH = new SkillHolder(4017, 1);
	private static final SkillHolder QUEEN_STRIKE = new SkillHolder(4018, 1);
	private static final SkillHolder QUEEN_SPRINKLE = new SkillHolder(4019, 1);
	//
	// State
	private static final byte ALIVE = 0;
	private static final byte DEAD = 1;
	// Instance of GrandBoss
	private static L2GrandBossInstance queen = null;
	// Instance of Minions
	private static Map<Integer, L2MonsterInstance> minions = new HashMap<>();
	private static L2MonsterInstance larva = null;
	
	public QueenAnt()
	{
		super(-1, "ai/mobs/grandboss");
		
		addAttackId(QUEEN);
		addFactionCallId(QUEEN, NURSE);
		addKillId(QUEEN, LARVA, NURSE);
		addSpawnId(GUARD, LARVA, NURSE);
		
		init();
	}
	
	private void init()
	{
		// zone = GrandBossManager.getInstance().getZone(179700, 113800, -7709);
		GrandBossHolder gb = GrandBossSpawnData.getBossInfo(QUEEN);
		
		if (gb.getStatus() == DEAD)
		{
			// load the respawn date and time for queen ant from DB
			long temp = gb.getRespawnTime() - System.currentTimeMillis();
			if (temp > 0)
			{
				startTimer("queen_spawn", temp, null, null);
			}
			else
			{
				startTimer("queen_spawn", 0, null, null);
			}
		}
		else
		{
			queen = (L2GrandBossInstance) addSpawn(QUEEN, gb.getLoc(), false, 0);
			queen.broadcastPacket(new PlaySound(PlaySoundType.MUSIC_BS01_A, queen.getX(), queen.getY(), queen.getZ()));
			queen.setCurrentHpMp(gb.getCurrentHp(), gb.getCurrentMp());
			larva = (L2MonsterInstance) addSpawn(LARVA, -21600, 179482, -5846, getRandom(360), false, 0);
			GrandBossSpawnData.addBoss(queen);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "queen_spawn":
				queen = (L2GrandBossInstance) addSpawn(QUEEN, -21610, 181594, -5734, 0, false, 0);
				queen.broadcastPacket(new PlaySound(PlaySoundType.MUSIC_BS01_A, queen.getX(), queen.getY(), queen.getZ()));
				GrandBossSpawnData.getBossInfo(QUEEN).setStatus(ALIVE);
				GrandBossSpawnData.addBoss(queen);
				larva = (L2MonsterInstance) addSpawn(LARVA, -21600, 179482, -5846, getRandom(360), false, 0);
				break;
			
			case "queen_use_skill":
				// no action
				break;
			
			case "minion_despawn":
				// no action
				break;
		}
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getId())
		{
			case NURSE:
			case GUARD:
				minions.put(npc.getObjectId(), (L2MonsterInstance) npc);
				break;
			case LARVA:
				npc.setIsParalyzed(true);
				npc.setIsInvul(true);
				break;
		}
		
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if ((GrandBossSpawnData.getBossInfo(QUEEN).getStatus() == ALIVE) && minions.containsKey(npc.getObjectId()))
		{
			minions.remove(npc.getObjectId());
		}
		else if (npc.getId() == QUEEN)
		{
			npc.broadcastPacket(new PlaySound(PlaySoundType.MUSIC_BS02_D, npc.getX(), npc.getY(), npc.getZ()));
			GrandBossSpawnData.getBossInfo(QUEEN).setStatus(DEAD);
			long respawnTime = ((19 + Rnd.get(35)) * 3600000);
			startTimer("queen_spawn", respawnTime, null, null);
			
			despawnAllMinions();
			cancelTimers("queen_use_skill");
			// also save the respawn time so that the info is maintained past reboots
			GrandBossSpawnData.getBossInfo(QUEEN).setRespawnTime(System.currentTimeMillis() + respawnTime);
			// saved info into DB
			GrandBossSpawnData.saveBoss(QUEEN);
		}
		
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (getTimer("queen_use_skill") == null)
		{
			queenUseSkills(npc, attacker);
			
			int reuseDelay = QUEEN_STRIKE.getSkill().getReuseDelay() * 2;// custom and logical
			startTimer("queen_use_skill", reuseDelay, null, null, false);
		}
		
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isPet)
	{
		switch (npc.getId())
		{
			case QUEEN:
				if (getTimer("queen_use_skill") == null)
				{
					queenUseSkills(npc, attacker);
					
					int reuseDelay = QUEEN_STRIKE.getSkill().getReuseDelay() * 2;// custom and logical
					startTimer("queen_use_skill", reuseDelay, null, null, false);
				}
				break;
			
			case NURSE:
				nurseHeal();
				break;
		}
		
		return null;
	}
	
	private static void despawnAllMinions()
	{
		for (L2MonsterInstance mob : minions.values())
		{
			if (mob != null)
			{
				mob.decayMe();
			}
		}
		minions.clear();
	}
	
	private static void queenUseSkills(L2Npc npc, L2Character player)
	{
		if ((player == null) || (npc == null))
		{
			return;
		}
		
		if ((Rnd.get(10) == 0) && !npc.isInsideRadius(player, 500, false, false))
		{
			handleCast(npc, player, QUEEN_STRIKE);
		}
		else if ((Rnd.get(10) == 0) && !npc.isInsideRadius(player, 150, false, false))
		{
			if (Rnd.get(10) < 8)
			{
				handleCast(npc, player, QUEEN_STRIKE);
			}
			else
			{
				handleCast(npc, player, QUEEN_SPRINKLE);
			}
		}
		else if ((Rnd.get(2) == 0) && npc.isInsideRadius(player, 250, false, false))
		{
			handleCast(npc, player, QUEEN_BRANDISH);
		}
	}
	
	private void nurseHeal()
	{
		for (L2MonsterInstance nurse : minions.values())
		{
			if ((nurse == null) || nurse.isDead() || nurse.isCastingNow())
			{
				continue;
			}
			
			boolean larvaNeedHeal = (larva != null) && (larva.getCurrentHp() < larva.getStat().getMaxHp());
			if (larvaNeedHeal)
			{
				handleCast(nurse, larva, Rnd.nextBoolean() ? HEAL1 : HEAL2);
				continue;
			}
			
			boolean queenNeedHeal = (queen != null) && (queen.getCurrentHp() < queen.getStat().getMaxHp());
			if (queenNeedHeal)
			{
				handleCast(nurse, queen, HEAL1);
				continue;
			}
			// if nurse not casting - remove target
			if (nurse.getTarget() != null)
			{
				nurse.setTarget(null);
			}
		}
	}
	
	private static void handleCast(L2Character actor, L2Character target, SkillHolder skill)
	{
		actor.setTarget(target);
		actor.doCast(skill.getSkill());
	}
}
