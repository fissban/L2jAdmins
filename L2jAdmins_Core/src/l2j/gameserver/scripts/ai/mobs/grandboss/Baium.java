package l2j.gameserver.scripts.ai.mobs.grandboss;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.data.GrandBossSpawnData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.instancemanager.zone.ZoneGrandBossManager;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Attackable.AggroInfo;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.GrandBossHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.zone.type.BossZone;
import l2j.gameserver.network.external.server.Earthquake;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;

/**
 * Following animations are handled in that time tempo :
 * <ul>
 * <li>wake(2), 0-13 secs</li>
 * <li>neck(3), 14-24 secs.</li>
 * <li>roar(1), 25-37 secs.</li>
 * </ul>
 * Waker's sacrifice is handled between neck and roar animation.
 */
public class Baium extends Script
{
	private static final BossZone BAIUM_LAIR = ZoneGrandBossManager.getZone(12007);
	
	private static final int STONE_BAIUM = 12535;
	private static final int LIVE_BAIUM = 12372;
	private static final int ARCHANGEL = 12373;
	
	// Baium status tracking
	public static final byte ASLEEP = 0; // baium is in the stone version, waiting to be woken up. Entry is unlocked.
	public static final byte AWAKE = 1; // baium is awake and fighting. Entry is locked.
	public static final byte DEAD = 2; // baium has been killed and has not yet spawned. Entry is locked.
	
	// Archangels spawns
	private static final List<LocationHolder> ANGEL_LOCATION = new ArrayList<>();
	
	{
		ANGEL_LOCATION.add(new LocationHolder(115780, 15564, 10080, 13620));
		ANGEL_LOCATION.add(new LocationHolder(114880, 16236, 10080, 5400));
		ANGEL_LOCATION.add(new LocationHolder(115168, 17200, 10080, 0));
		ANGEL_LOCATION.add(new LocationHolder(115792, 16608, 10080, 0));
	}
	
	private L2Character actualVictim;
	private long lastAttackVsBaiumTime = 0;
	private final List<L2Npc> minions = new ArrayList<>(5);
	
	public Baium()
	{
		super(-1, "ai/mobs/grandboss");
		
		// Quest NPC starter initialization
		addStartNpc(STONE_BAIUM);
		addTalkId(STONE_BAIUM);
		
		addAttackId(LIVE_BAIUM);
		addKillId(LIVE_BAIUM);
		addSpawnId(LIVE_BAIUM);
		
		GrandBossHolder gb = GrandBossSpawnData.getBossInfo(LIVE_BAIUM);
		
		if (gb.getStatus() == DEAD)
		{
			// load the unlock date and time for baium from DB
			long temp = gb.getRespawnTime() - System.currentTimeMillis();
			if (temp > 0)
			{
				// The time has not yet expired. Mark Baium as currently locked (dead).
				startTimer("baium_unlock", temp, null, null, false);
			}
			else
			{
				// The time has expired while the server was offline. Spawn the stone-baium as ASLEEP.
				addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0);
				GrandBossSpawnData.getBossInfo(LIVE_BAIUM).setStatus(ASLEEP);
			}
		}
		else if (gb.getStatus() == AWAKE)
		{
			L2Npc baium = addSpawn(LIVE_BAIUM, gb.getLoc(), false, 0);
			GrandBossSpawnData.addBoss((L2GrandBossInstance) baium);
			
			baium.setCurrentHpMp(gb.getCurrentHp(), gb.getCurrentMp());
			baium.setRunning();
			
			// start monitoring baium's inactivity
			lastAttackVsBaiumTime = System.currentTimeMillis();
			startTimer("baium_despawn", 60000, baium, null, true);
			startTimer("skill_range", 2000, baium, null, true);
			
			// Spawns angels
			for (LocationHolder loc : ANGEL_LOCATION)
			{
				L2Npc angel = addSpawn(ARCHANGEL, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, 0);
				angel.setRunning();
				minions.add(angel);
			}
			
			// Angels AI
			startTimer("angels_aggro_reconsider", 5000, null, null, true);
		}
		else
		{
			addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if ((npc != null) && (npc.getId() == LIVE_BAIUM))
		{
			if (event.equalsIgnoreCase("skill_range"))
			{
				callSkillAI(npc);
			}
			else if (event.equalsIgnoreCase("baium_neck"))
			{
				npc.broadcastPacket(new SocialAction(npc.getObjectId(), SocialActionType.VICTORY));
			}
			else if (event.equalsIgnoreCase("sacrifice_waker"))
			{
				if (player != null)
				{
					// If player is far of Baium, teleport him back.
					if (!Util.checkIfInShortRadius(300, player, npc, true))
					{
						player.teleToLocation(115929, 17349, 10077);
					}
					
					// 60% to die.
					if (Rnd.get(100) < 60)
					{
						player.doDie(npc);
					}
				}
			}
			else if (event.equalsIgnoreCase("baium_roar"))
			{
				// Roar animation
				npc.broadcastPacket(new SocialAction(npc.getObjectId(), SocialActionType.NPC_ANIMATION));
				
				// Spawn angels
				for (LocationHolder loc : ANGEL_LOCATION)
				{
					L2Npc angel = addSpawn(ARCHANGEL, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, 0);
					angel.setRunning();
					minions.add(angel);
				}
				
				// Angels AI
				startTimer("angels_aggro_reconsider", 5000, null, null, true);
			}
			else if (event.equalsIgnoreCase("baium_move"))
			{
				npc.setIsInvul(false);
				npc.setRunning();
				
				// Start monitoring baium's inactivity and activate the AI
				lastAttackVsBaiumTime = System.currentTimeMillis();
				
				startTimer("baium_despawn", 60000, npc, null, true);
				startTimer("skill_range", 2000, npc, null, true);
			}
			// despawn the live baium after 30 minutes of inactivity
			// also check if the players are cheating, having pulled Baium outside his zone...
			else if (event.equalsIgnoreCase("baium_despawn"))
			{
				if ((lastAttackVsBaiumTime + 1800000) < System.currentTimeMillis())
				{
					// despawn the live-baium
					npc.deleteMe();
					
					// Unspawn angels
					for (L2Npc minion : minions)
					{
						if (minion != null)
						{
							minion.getSpawn().stopRespawn();
							minion.deleteMe();
						}
					}
					minions.clear();
					
					// spawn stone-baium
					addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0);
					GrandBossSpawnData.getBossInfo(LIVE_BAIUM).setStatus(ASLEEP);
					BAIUM_LAIR.oustAllPlayers();
					cancelTimer("baium_despawn", npc, null);
				}
				else if (((lastAttackVsBaiumTime + 300000) < System.currentTimeMillis()) && ((npc.getCurrentHp() / npc.getStat().getMaxHp()) < 0.75))
				{
					npc.setTarget(npc);
					npc.doCast(SkillData.getInstance().getSkill(4135, 1));
				}
				else if (!BAIUM_LAIR.isInsideZone(npc))
				{
					npc.teleToLocation(116033, 17447, 10104);
				}
			}
		}
		else if (event.equalsIgnoreCase("baium_unlock"))
		{
			GrandBossSpawnData.getBossInfo(LIVE_BAIUM).setStatus(ASLEEP);
			addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0);
		}
		else if (event.equalsIgnoreCase("angels_aggro_reconsider"))
		{
			boolean updateTarget = false; // Update or no the target
			
			for (L2Npc minion : minions)
			{
				L2Attackable angel = ((L2Attackable) minion);
				if (angel == null)
				{
					continue;
				}
				
				L2Character victim = angel.getMostHated();
				
				if (Rnd.get(100) < 10)
				{
					updateTarget = true;
				}
				else
				{
					if (victim != null) // Target is a unarmed player ; clean aggro.
					{
						if ((victim instanceof L2PcInstance) && (victim.getActiveWeaponInstance() == null))
						{
							AggroInfo ai = angel.getAggroList().get(victim);
							if (ai != null)
							{
								ai.setHate(0);
							}
							updateTarget = true;
						}
					}
					else
					{
						// No target currently.
						updateTarget = true;
					}
				}
				
				if (updateTarget)
				{
					L2Character newVictim = getRandomTarget(minion);
					if ((newVictim != null) && (victim != newVictim))
					{
						angel.addDamageHate(newVictim, 0, 10000);
						angel.getAI().setIntention(CtrlIntentionType.ATTACK, newVictim);
					}
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		
		if (GrandBossSpawnData.getBossInfo(LIVE_BAIUM).getStatus() == ASLEEP)
		{
			if (BAIUM_LAIR.isPlayerAllowed(player))
			{
				GrandBossSpawnData.getBossInfo(LIVE_BAIUM).setStatus(AWAKE);
				
				L2Npc baium = addSpawn(LIVE_BAIUM, npc, false, 0);
				baium.setIsInvul(true);
				
				GrandBossSpawnData.addBoss((L2GrandBossInstance) baium);
				
				// First animation
				baium.broadcastPacket(new SocialAction(baium.getObjectId(), SocialActionType.HELLO));
				baium.broadcastPacket(new Earthquake(baium.getX(), baium.getY(), baium.getZ(), 40, 10));
				
				// Second animation, waker sacrifice, followed by angels spawn, third animation and finally movement.
				startTimer("baium_neck", 13000, baium, null, false);
				startTimer("sacrifice_waker", 24000, baium, player, false);
				startTimer("baium_roar", 28000, baium, null, false);
				startTimer("baium_move", 35000, baium, null, false);
				
				// Delete the statue.
				npc.deleteMe();
			}
			else
			{
				htmltext = "Conditions are not right to wake up Baium";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		// npc.disableCoreAI(true);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (!BAIUM_LAIR.isInsideZone(attacker))
		{
			attacker.doDie(attacker);
			return null;
		}
		
		if (npc.isInvul())
		{
			return null;
		}
		
		if (npc.getId() == LIVE_BAIUM)
		{
			if (attacker.isRiding())
			{
				Skill skill = SkillData.getInstance().getSkill(4258, 1);
				if (attacker.getEffect(skill) == null)
				{
					npc.setTarget(attacker);
					npc.doCast(skill);
				}
			}
			// update a variable with the last action against baium
			lastAttackVsBaiumTime = System.currentTimeMillis();
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		cancelTimer("baium_despawn", npc, null);
		npc.broadcastPacket(new PlaySound(PlaySoundType.MUSIC_BS01_D, npc.getX(), npc.getY(), npc.getZ()));
		
		// spawn the "Teleportation Cubic" for 15 minutes (to allow players to exit the lair)
		addSpawn(29055, 115203, 16620, 10078, 0, false, 900000);
		
		long respawnTime = ((121 + Rnd.get(8)) * 3600000);
		
		GrandBossSpawnData.getBossInfo(LIVE_BAIUM).setStatus(DEAD);
		// also save the respawn time so that the info is maintained past reboots
		GrandBossSpawnData.getBossInfo(LIVE_BAIUM).setRespawnTime(System.currentTimeMillis() + respawnTime);
		// saved info into DB
		GrandBossSpawnData.saveBoss(LIVE_BAIUM);
		startTimer("baium_unlock", respawnTime, null, null, false);
		
		// Unspawn angels.
		for (L2Npc minion : minions)
		{
			if (minion != null)
			{
				minion.getSpawn().stopRespawn();
				minion.deleteMe();
			}
		}
		minions.clear();
		
		// Clean Baium AI
		cancelTimer("skill_range", npc, null);
		
		// Clean angels AI
		cancelTimer("angels_aggro_reconsider", null, null);
		
		return super.onKill(npc, killer, isPet);
	}
	
	/**
	 * This method allows to select a random target, and is used both for Baium and angels.
	 * @param  npc to check.
	 * @return     the random target.
	 */
	private L2Character getRandomTarget(L2Npc npc)
	{
		int npcId = npc.getId();
		List<L2Character> result = new ArrayList<>();
		
		for (L2Character obj : npc.getKnownList().getObjectType(L2Character.class))
		{
			if (obj instanceof L2PcInstance)
			{
				if (obj.isDead() || !(GeoEngine.getInstance().canSeeTarget(npc, obj)))
				{
					continue;
				}
				
				if (((L2PcInstance) obj).isGM() && ((L2PcInstance) obj).getInvisible())
				{
					continue;
				}
				
				if ((npcId == ARCHANGEL) && (((L2PcInstance) obj).getActiveWeaponInstance() == null))
				{
					continue;
				}
				
				result.add(obj);
			}
			// Case of Archangels, they can hit Baium.
			else if ((obj instanceof L2GrandBossInstance) && (npcId == ARCHANGEL))
			{
				result.add(obj);
			}
		}
		
		// If there's no players available, Baium and Angels are hitting each other.
		if (result.isEmpty())
		{
			if (npcId == LIVE_BAIUM) // Case of Baium. Angels should never be without target.
			{
				for (L2Npc minion : minions)
				{
					if (minion != null)
					{
						result.add(minion);
					}
				}
			}
		}
		
		return (result.isEmpty()) ? null : result.get(Rnd.get(result.size()));
	}
	
	/**
	 * The personal casting AI for Baium.
	 * @param npc baium, basically...
	 */
	private void callSkillAI(L2Npc npc)
	{
		if (npc.isInvul() || npc.isCastingNow())
		{
			return;
		}
		
		// Pickup a target if no or dead victim. If Baium was hitting an angel, 50% luck he reconsiders his target. 10% luck he decides to reconsiders his target.
		if ((actualVictim == null) || actualVictim.isDead() || !(npc.getKnownList().getObject(actualVictim)) || ((actualVictim instanceof L2MonsterInstance) && (Rnd.get(10) < 5)) || (Rnd.get(10) == 0))
		{
			actualVictim = getRandomTarget(npc);
		}
		
		// If result is null, return directly.
		if (actualVictim == null)
		{
			return;
		}
		
		final Skill skill = SkillData.getInstance().getSkill(getRandomSkill(npc), 1);
		
		// Adapt the skill range, because Baium is fat.
		if (Util.checkIfInRange((int) (skill.getCastRange() + npc.getCollisionRadius()), npc, actualVictim, true))
		{
			npc.getAI().setIntention(CtrlIntentionType.IDLE);
			npc.setTarget(skill.getId() == 4135 ? npc : actualVictim);
			npc.doCast(skill);
		}
		else
		{
			npc.getAI().setIntention(CtrlIntentionType.FOLLOW, actualVictim, null);
		}
	}
	
	/**
	 * Pick a random skill through that list.<br>
	 * If Baium feels surrounded, he will use AoE skills. Same behavior if he is near 2+ angels.<br>
	 * @param  npc baium
	 * @return     a usable skillId
	 */
	private int getRandomSkill(L2Npc npc)
	{
		// Baium's selfheal. It happens exceptionaly.
		if ((npc.getCurrentHp() / npc.getStat().getMaxHp()) < 0.1)
		{
			if (Rnd.get(10000) == 777)
			{
				return 4135;
			}
		}
		
		int skill = 4127; // Default attack if nothing is possible.
		final int chance = Rnd.get(100); // Remember, it's 0 to 99, not 1 to 100.
		
		// If Baium feels surrounded or see 2+ angels, he unleashes his wrath upon heads :).
		
		if ((npc.getKnownList().getObjectTypeInRadius(L2PcInstance.class, 600).size() >= 20) || (npc.getKnownList().getObjectTypeInRadius(L2MonsterInstance.class, 600).size() >= 2))
		{
			if (chance < 25)
			{
				skill = 4130;
			}
			else if ((chance >= 25) && (chance < 50))
			{
				skill = 4131;
			}
			else if ((chance >= 50) && (chance < 75))
			{
				skill = 4128;
			}
			else if ((chance >= 75) && (chance < 100))
			{
				skill = 4129;
			}
		}
		else
		{
			if ((npc.getCurrentHp() / npc.getStat().getMaxHp()) > 0.75)
			{
				if (chance < 10)
				{
					skill = 4128;
				}
				else if ((chance >= 10) && (chance < 20))
				{
					skill = 4129;
				}
			}
			else if ((npc.getCurrentHp() / npc.getStat().getMaxHp()) > 0.5)
			{
				if (chance < 10)
				{
					skill = 4131;
				}
				else if ((chance >= 10) && (chance < 20))
				{
					skill = 4128;
				}
				else if ((chance >= 20) && (chance < 30))
				{
					skill = 4129;
				}
			}
			else if ((npc.getCurrentHp() / npc.getStat().getMaxHp()) > 0.25)
			{
				if (chance < 10)
				{
					skill = 4130;
				}
				else if ((chance >= 10) && (chance < 20))
				{
					skill = 4131;
				}
				else if ((chance >= 20) && (chance < 30))
				{
					skill = 4128;
				}
				else if ((chance >= 30) && (chance < 40))
				{
					skill = 4129;
				}
			}
			else
			{
				if (chance < 10)
				{
					skill = 4130;
				}
				else if ((chance >= 10) && (chance < 20))
				{
					skill = 4131;
				}
				else if ((chance >= 20) && (chance < 30))
				{
					skill = 4128;
				}
				else if ((chance >= 30) && (chance < 40))
				{
					skill = 4129;
				}
			}
		}
		return skill;
	}
}
