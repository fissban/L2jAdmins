package l2j.gameserver.model.actor.ai;

import java.util.List;
import java.util.concurrent.Future;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.DimensionalRiftData;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2ChestInstance;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2FestivalMonsterInstance;
import l2j.gameserver.model.actor.instance.L2FriendlyMobInstance;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2GuardInstance;
import l2j.gameserver.model.actor.instance.L2MinionInstance;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.model.actor.instance.L2RiftInvaderInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptEventType;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;

/**
 * This class manages AI of L2Attackable.
 */
public class AttackableAI extends CharacterAI implements Runnable
{
	private static final int RANDOM_WALK_RATE = 30;
	protected static final int MAX_ATTACK_TIMEOUT = 120000; // 2min
	
	/** The L2Attackable AI task executed every 1s (call onEvtThink method) */
	protected Future<?> aiTask;
	/** The delay after wich the attacked is stopped */
	protected long attackTimeOut;
	/** The L2Attackable aggro counter */
	protected int globalAggro;
	/** The flag used to indicate that a thinking action is in progress */
	protected boolean thinking; // to prevent recursive thinking
	
	private int chaosTime = 0;
	
	/**
	 * Constructor of L2AttackableAI.
	 * @param actor
	 */
	public AttackableAI(L2Character actor)
	{
		super(actor);
		
		attackTimeOut = Long.MAX_VALUE;
		globalAggro = -10; // 10 seconds timeout of ATTACK after respawn
	}
	
	@Override
	public void run()
	{
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}
	
	/**
	 * Return True if the target is auto attackable (depends on the actor type).<BR>
	 * <B><U> Actor is a L2GuardInstance</U> :</B><BR>
	 * <li>The target isn't a Folk or a Door</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>The L2PcInstance target has karma (=PK)</li>
	 * <li>The L2MonsterInstance target is aggressive</li> <B><U> Actor is a L2SiegeGuardInstance</U> :</B><BR>
	 * <li>The target isn't a Folk or a Door</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>A siege is in progress</li>
	 * <li>The L2PcInstance target isn't a Defender</li> <B><U> Actor is a L2FriendlyMobInstance</U> :</B><BR>
	 * <li>The target isn't a Folk, a Door or another L2NpcInstance</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>The L2PcInstance target has karma (=PK)</li> <B><U> Actor is a L2MonsterInstance</U> :</B><BR>
	 * <li>The target isn't a Folk, a Door or another L2NpcInstance</li>
	 * <li>The target isn't dead, isn't invulnerable, isn't in silent moving mode AND too far (>100)</li>
	 * <li>The target is in the actor Aggro range and is at the same height</li>
	 * <li>The actor is Aggressive</li>
	 * @param  target The targeted L2Object
	 * @return
	 */
	private boolean autoAttackCondition(L2Character target)
	{
		if ((target == null) || (activeActor == null))
		{
			return false;
		}
		
		// Check if the target is Dead
		if (target.isAlikeDead())
		{
			return false;
		}
		
		// Check if the target is a Door
		if (target instanceof L2DoorInstance)
		{
			return false;
		}
		
		final L2Attackable me = (L2Attackable) activeActor;
		
		// Check if the target is Playable
		if (target instanceof L2Playable)
		{
			// Check if the target isn't dead, is in the Aggro range and is at the same height
			if (!me.isInsideRadius(target, me.getAggroRange(), true, false))
			{
				return false;
			}
			
			// Check if the AI isn't a Raid Boss/Town guard and the target isn't in silent move mode
			if (!(me.isRaid() || (me instanceof L2GuardInstance)) && ((L2Playable) target).isSilentMoving())
			{
				return false;
			}
		}
		
		L2PcInstance targetPlayer = target.getActingPlayer();
		// Check if the target is a L2PcInstance
		if (targetPlayer != null)
		{
			// Check if the target is within the grace period for JUST getting up from fake death
			if (targetPlayer.isRecentFakeDeath())
			{
				return false;
			}
			
			if (me.getFactionId() != null)
			{
				// Check if player is an ally
				if (me.getFactionId().equals("varka_silenos_clan") && targetPlayer.isAlliedWithVarka())
				{
					return false;
				}
				
				if (me.getFactionId().equals("ketra_orc_clan") && targetPlayer.isAlliedWithKetra())
				{
					return false;
				}
			}
			
			if (targetPlayer.isInParty() && targetPlayer.getParty().isInDimensionalRift())
			{
				byte riftType = targetPlayer.getParty().getDimensionalRift().getType();
				byte riftRoom = targetPlayer.getParty().getDimensionalRift().getCurrentRoom();
				if ((me instanceof L2RiftInvaderInstance) && !DimensionalRiftData.getInstance().getRoom(riftType, riftRoom).checkIfInZone(me.getX(), me.getY(), me.getZ()))
				{
					return false;
				}
			}
		}
		
		if (me instanceof L2GuardInstance)
		{
			// Check if the L2PcInstance target has karma (=PK)
			if ((target instanceof L2PcInstance) && (((L2PcInstance) target).getKarma() > 0))
			{
				return GeoEngine.getInstance().canSeeTarget(me, target); // Los Check
			}
			
			// Check if the L2MonsterInstance target is aggressive
			if ((target instanceof L2MonsterInstance) && Config.GUARD_ATTACK_AGGRO_MOB)
			{
				return (((L2MonsterInstance) target).isAggressive() && GeoEngine.getInstance().canSeeTarget(me, target));
			}
			
			return false;
		}
		
		if (me instanceof L2FriendlyMobInstance)
		{
			// Check if the target isn't another L2NpcInstance
			if (target instanceof L2Npc)
			{
				return false;
			}
			
			// Check if the L2PcInstance target has karma (=PK)
			if ((target instanceof L2PcInstance) && (((L2PcInstance) target).getKarma() > 0))
			{
				return GeoEngine.getInstance().canSeeTarget(me, target); // Los Check
			}
			return false;
		}
		
		// Check if the target isn't another L2Npc
		if (target instanceof L2Npc)
		{
			return false;
		}
		
		// depending on config, do not allow mobs to attack new_ players in peace zones,
		// unless they are already following those players from outside the peace zone.
		if (!Config.ALT_MOB_AGGRO_IN_PEACEZONE && target.isInsideZone(ZoneType.PEACE))
		{
			return false;
		}
		
		// Check if the actor is Aggressive
		return (me.isAggressive() && GeoEngine.getInstance().canSeeTarget(me, target));
	}
	
	@Override
	public void stopAITask()
	{
		if (aiTask != null)
		{
			aiTask.cancel(false);
			aiTask = null;
		}
		
		super.stopAITask();
	}
	
	/**
	 * Set the Intention of this L2CharacterAI and create an AI Task executed every 1s (call onEvtThink method) for this L2Attackable.<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : If actor knowPlayer isn't EMPTY, IDLE will be change in ACTIVE</B></FONT><BR>
	 * @param intention The new Intention to set to the AI
	 * @param arg0      The first parameter of the Intention
	 * @param arg1      The second parameter of the Intention
	 */
	@Override
	synchronized void changeIntention(CtrlIntentionType intention, Object arg0, Object arg1)
	{
		if ((intention == CtrlIntentionType.IDLE) || (intention == CtrlIntentionType.ACTIVE))
		{
			// Check if actor is not dead
			if (!activeActor.isAlikeDead())
			{
				final L2Attackable npc = (L2Attackable) activeActor;
				
				// If its knownPlayer isn't empty set the Intention to ACTIVE
				if (!npc.getKnownList().getObjectType(L2PcInstance.class).isEmpty())
				{
					intention = CtrlIntentionType.ACTIVE;
				}
				else
				{
					if (npc.getSpawn() != null)
					{
						if (!npc.isInsideRadius(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ(), Config.MAX_DRIFT_RANGE * 2, true, false))
						{
							intention = CtrlIntentionType.ACTIVE;
						}
					}
				}
			}
			
			if (intention == CtrlIntentionType.IDLE)
			{
				// Set the Intention of this L2AttackableAI to IDLE
				super.changeIntention(CtrlIntentionType.IDLE, null, null);
				
				// Stop AI task and detach AI from NPC
				stopAITask();
				
				// Cancel the AI
				getActor().detachAI();
				
				return;
			}
		}
		
		// Set the Intention of this L2AttackableAI to intention
		super.changeIntention(intention, arg0, arg1);
		
		// If not idle - create an AI task (schedule onEvtThink repeatedly)
		if (aiTask == null)
		{
			aiTask = ThreadPoolManager.scheduleAtFixedRate(this, 1000, 1000);
		}
	}
	
	/**
	 * Manage the Attack Intention : Stop current Attack (if necessary), Calculate attack timeout, Start a new Attack and Launch Think Event.<BR>
	 * @param target The L2Character to attack
	 */
	@Override
	protected void onIntentionAttack(L2Character target)
	{
		// Calculate the attack timeout
		attackTimeOut = MAX_ATTACK_TIMEOUT + System.currentTimeMillis();
		
		// Manage the Attack Intention : Stop current Attack (if necessary), Start a new Attack and Launch Think Event
		super.onIntentionAttack(target);
	}
	
	/**
	 * Manage AI standard thinks of a L2Attackable (called by onEvtThink).<BR>
	 * <b><u>Actions</u>:</b><BR>
	 * <li>Update every 1s the globalAggro counter to come close to 0</li>
	 * <li>If the actor is Aggressive and can attack, add all autoAttackable L2Character in its Aggro Range to its aggroList, chose a target and order to attack it</li>
	 * <li>If the actor is a L2GuardInstance that can't attack, order to it to return to its home location</li>
	 * <li>If the actor is a L2MonsterInstance that can't attack, order to it to random walk (1/100)</li>
	 */
	private void thinkActive()
	{
		final L2Attackable npc = (L2Attackable) activeActor;
		
		// Update every 1s the globalAggro counter to come close to 0
		if (globalAggro != 0)
		{
			if (globalAggro < 0)
			{
				globalAggro++;
			}
			else
			{
				globalAggro--;
			}
		}
		
		// Add all autoAttackable L2Character in L2Attackable Aggro Range to its aggroList with 0 damage and 1 hate
		// A L2Attackable isn't aggressive during 10s after its spawn because globalAggro is set to -10
		if (globalAggro >= 0)
		{
			// Get all visible objects inside its Aggro Range
			for (final L2Character target : npc.getKnownList().getObjectType(L2Character.class))
			{
				// Check to see if this is a festival mob spawn. If it is, then check to see if the aggro trigger is a festival participant...if so, move to attack it.
				if ((npc instanceof L2FestivalMonsterInstance) && (target instanceof L2PcInstance))
				{
					if (!((L2PcInstance) target).isFestivalParticipant())
					{
						continue;
					}
				}
				
				// For each L2Character check if the target is auto attackable
				if (autoAttackCondition(target)) // check aggression
				{
					// Add the attacker to the L2Attackable aggroList
					if (npc.getHating(target) == 0)
					{
						npc.addDamageHate(target, 0, 1);
					}
				}
			}
			
			// Chose a target from its aggroList
			L2Character hated = npc.isConfused() ? (L2Character) getTarget() : npc.getMostHated();
			
			// Order to the L2Attackable to attack the target
			if (hated != null)
			{
				// Get the hate level of the L2Attackable against this L2Character target contained in aggroList
				if ((npc.getHating(hated) + globalAggro) > 0)
				{
					// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
					if (!npc.isRunning())
					{
						npc.setRunning();
					}
					
					// Set the AI Intention to ATTACK
					setIntention(CtrlIntentionType.ATTACK, hated);
				}
				
				return;
			}
		}
		
		// Chance to forget attackers after some time
		// if ((npc.getCurrentHp() == npc.getStat().getMaxHp()) && (npc.getCurrentMp() == npc.getStat().getMaxMp()) && !npc.getAttackByList().isEmpty() && (Rnd.nextInt(500) == 0))
		// {
		// npc.clearAggroList();
		// npc.getAttackByList().clear();
		// }
		
		// Check if the actor is a L2GuardInstance
		if (npc instanceof L2GuardInstance)
		{
			// Order to the L2GuardInstance to return to its home location because there's no target to attack
			((L2GuardInstance) npc).returnHome();
		}
		
		// If this is a festival monster, then it remains in the same location.
		if (npc instanceof L2FestivalMonsterInstance)
		{
			return;
		}
		
		// Minions following leader
		if ((npc instanceof L2MinionInstance) && (((L2MinionInstance) npc).getLeader() != null))
		{
			L2MinionInstance minion = (L2MinionInstance) npc;
			
			int offset;
			final int minRadius = 30;
			
			if (npc.isRaid())
			{
				offset = 500; // for Raids - need correction
			}
			else
			{
				offset = 200; // for normal minions - need correction :)
			}
			
			if (minion.getLeader().isRunning())
			{
				npc.setRunning();
			}
			else
			{
				npc.setWalking();
			}
			
			if (npc.getPlanDistanceSq(minion.getLeader()) > (offset * offset))
			{
				int x1, y1, z1;
				x1 = Rnd.get(minRadius * 2, offset * 2); // x
				y1 = Rnd.get(x1, offset * 2); // distance
				y1 = (int) Math.sqrt((y1 * y1) - (x1 * x1)); // y
				if (x1 > (offset + minRadius))
				{
					x1 = (minion.getLeader().getX() + x1) - offset;
				}
				else
				{
					x1 = (minion.getLeader().getX() - x1) + minRadius;
				}
				if (y1 > (offset + minRadius))
				{
					y1 = (minion.getLeader().getY() + y1) - offset;
				}
				else
				{
					y1 = (minion.getLeader().getY() - y1) + minRadius;
				}
				
				z1 = minion.getLeader().getZ();
				// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
				moveTo(x1, y1, z1);
				return;
			}
		}
		else if ((npc.getSpawn() != null) && (Rnd.nextInt(RANDOM_WALK_RATE) == 0) && !((npc instanceof L2RaidBossInstance) || (npc instanceof L2MinionInstance) || (npc instanceof L2GrandBossInstance) || (npc instanceof L2ChestInstance) || (npc instanceof L2GuardInstance) || npc.isQuestMonster()))
		{
			// spawn coords
			int x1 = npc.getSpawn().getX();
			int y1 = npc.getSpawn().getY();
			int z1 = npc.getSpawn().getZ();
			
			boolean inSpawnRange = false;
			if (npc.getX() == x1 && npc.getY() == y1)
			{
				inSpawnRange = true;
			}
			
			// If the L2MonsterInstance is close to his spawn, he will walk back to the
			if (!inSpawnRange && GeoEngine.getInstance().canMoveToTarget(npc.getX(), npc.getY(), npc.getZ(), x1, y1, z1))
			{
				npc.setIsReturningToSpawnPoint(true);
				npc.setWalking();
				moveTo(x1, y1, z1);
			}
			else
			{
				// Order to the L2MonsterInstance to random walk
				x1 = Rnd.get(Config.MAX_DRIFT_RANGE * 2); // x
				y1 = Rnd.get(x1, Config.MAX_DRIFT_RANGE * 2); // distance
				y1 = (int) Math.sqrt((y1 * y1) - (x1 * x1)); // y
				x1 += npc.getSpawn().getX() - Config.MAX_DRIFT_RANGE;
				y1 += npc.getSpawn().getY() - Config.MAX_DRIFT_RANGE;
				z1 = npc.getZ();
				
				// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
				moveTo(x1, y1, z1);
			}
		}
		
		return;
	}
	
	/**
	 * Manage AI attack thinks of a L2Attackable (called by onEvtThink).<BR>
	 * <b><u>Actions</u>:</b><BR>
	 * <li>Update the attack timeout if actor is running</li>
	 * <li>If target is dead or timeout is expired, stop this attack and set the Intention to ACTIVE</li>
	 * <li>Call all L2Object of its Faction inside the Faction Range</li>
	 * <li>Chose a target and order to attack it with magic skill or physical attack</li> TODO: Manage casting rules to healer mobs (like Ant Nurses)
	 */
	private void thinkAttack()
	{
		L2Attackable npc = (L2Attackable) activeActor;
		
		if (npc.isCastingNow())
		{
			return;
		}
		
		L2Character attackTarget;
		
		if (npc.isConfused() && (getTarget() != null) && (getTarget() instanceof L2Character))
		{
			attackTarget = (L2Character) getTarget();
		}
		else
		{
			attackTarget = npc.getMostHated();
		}
		
		// If target doesn't exist, is dead or if timeout is expired (non-aggro mobs or mobs which are too far stop to attack)
		if ((attackTarget == null) || attackTarget.isAlikeDead() || ((attackTimeOut < System.currentTimeMillis()) && (!npc.isAggressive() || (Math.sqrt(npc.getPlanDistanceSq(attackTarget.getX(), attackTarget.getY())) > (npc.getAggroRange() * 2)))))
		{
			attackTimeOut = MAX_ATTACK_TIMEOUT + System.currentTimeMillis();
			
			// Stop hating this target after the attack timeout or if target is dead
			npc.stopHating(attackTarget);
			setIntention(CtrlIntentionType.ACTIVE);
			npc.setWalking();
			return;
		}
		
		/**
		 * FACTION AI
		 */
		
		final String factionId = npc.getFactionId();
		
		// Call all L2Object of its Faction inside the Faction Range
		if (factionId != null)
		{
			// Go through all L2Object that belong to its faction
			for (final L2Npc called : npc.getKnownList().getObjectTypeInRadius(L2Npc.class, npc.getFactionRange()))
			{
				if (!factionId.equals(called.getFactionId()))
				{
					continue;
				}
				
				if (called instanceof L2GrandBossInstance)
				{
					continue;
				}
				
				// Check if the L2Object is inside the Faction Range of the actor
				if ((called.getAI() != null) && npc.getAttackByList().contains(attackTarget) && ((called.getAI().getIntention() == CtrlIntentionType.IDLE) || (called.getAI().getIntention() == CtrlIntentionType.ACTIVE)))
				{
					if (!GeoEngine.getInstance().canSeeTarget(npc, called))
					{
						continue;
					}
					
					if (attackTarget instanceof L2Playable)
					{
						List<Script> quests = called.getTemplate().getEventScript(ScriptEventType.ON_FACTION_CALL);
						if (quests != null)
						{
							L2PcInstance player = attackTarget.getActingPlayer();
							boolean isSummon = attackTarget instanceof L2Summon;
							for (Script quest : quests)
							{
								quest.notifyFactionCall(called, npc, player, isSummon);
							}
						}
					}
					
					if ((attackTarget instanceof L2PcInstance) && attackTarget.isInParty() && attackTarget.getParty().isInDimensionalRift())
					{
						byte riftType = attackTarget.getParty().getDimensionalRift().getType();
						byte riftRoom = attackTarget.getParty().getDimensionalRift().getCurrentRoom();
						
						if ((activeActor instanceof L2RiftInvaderInstance) && !DimensionalRiftData.getInstance().getRoom(riftType, riftRoom).checkIfInZone(npc.getX(), npc.getY(), npc.getZ()))
						{
							continue;
						}
					}
					
					if ((called instanceof L2Attackable) && (called.getAI().getIntention() != CtrlIntentionType.ATTACK))
					{
						called.getAI().notifyEvent(CtrlEventType.AGGRESSION, attackTarget, 9999);
					}
				}
			}
		}
		
		/**
		 * RAIDS
		 */
		if (npc.isRaid())
		{
			chaosTime++;
			if ((npc instanceof L2RaidBossInstance) && (chaosTime > 30) && (Rnd.get(100) <= (100 - ((npc.getCurrentHp() * ((((L2MonsterInstance) npc).hasMinions()) ? 200 : 100)) / npc.getStat().getMaxHp()))))
			{
				attackTarget = aggroReconsider(attackTarget);
				chaosTime = 0;
			}
			else if ((npc instanceof L2GrandBossInstance) && (chaosTime > 30))
			{
				double chaosRate = 100 - ((npc.getCurrentHp() * 300) / npc.getStat().getMaxHp());
				if (((chaosRate <= 10) && (Rnd.get(100) <= 10)) || ((chaosRate > 10) && (Rnd.get(100) <= chaosRate)))
				{
					attackTarget = aggroReconsider(attackTarget);
					chaosTime = 0;
				}
			}
			else if ((chaosTime > 30) && (Rnd.get(100) <= (100 - ((npc.getCurrentHp() * 200) / npc.getStat().getMaxHp()))))
			{
				attackTarget = aggroReconsider(attackTarget);
				chaosTime = 0;
			}
		}
		
		if (attackTarget == null)
		{
			setIntention(CtrlIntentionType.ACTIVE);
			return;
		}
		
		setTarget(attackTarget);
		npc.setTarget(attackTarget);
		
		attackTimeOut = MAX_ATTACK_TIMEOUT + System.currentTimeMillis();
		
		// check for close combat skills && heal/buff skills
		if (!activeActor.isMuted())
		{
			boolean isFish = false;
			
			if (activeActor instanceof L2MonsterInstance)
			{
				switch (((L2MonsterInstance) activeActor).getId())
				{
					case 13245:// Caught Frog
					case 13246:// Caught Undine
					case 13247:// Caught Rakul
					case 13248:// Caught Sea Giant
					case 13249:// Caught Sea Horse Soldier
					case 13250:// Caught Homunculus
					case 13251:// Caught Flava
					case 13252:// Caught Gigantic Eye
						isFish = true;
				}
			}
			
			for (final Skill sk : activeActor.getAllSkills())
			{
				if (!canUseSkill(activeActor, sk))
				{
					continue;
				}
				
				if ((Rnd.get(100) <= 8) || (isFish && (Rnd.get(100) <= 20)))
				{
					L2Object oldTarget = activeActor.getTarget();
					
					if (sk.getSkillType() == SkillType.BUFF)
					{
						// check all effect in actor
						for (Effect effect : activeActor.getAllEffects())
						{
							if ((effect != null) && (effect.getSkill() == sk))
							{
								break;
							}
						}
						
						// useSkillSelf
						activeActor.setTarget(activeActor);
					}
					else if (sk.getSkillType() == SkillType.HEAL)
					{
						if (activeActor.getCurrentHp() > (activeActor.getStat().getMaxHp() / 1.5))
						{
							break;
						}
						
						// useSkillSelf
						activeActor.setTarget(activeActor);
					}
					
					// GeoData Los Check here
					if (GeoEngine.getInstance().canSeeTarget(activeActor, activeActor.getTarget()))
					{
						clientStopMoving(null);
						getActor().doCast(sk);
						activeActor.setTarget(oldTarget);
						return;
					}
				}
			}
		}
		
		// We should calculate new distance cuz mob can have changed the target
		double dist = activeActor.getPlanDistanceSq(attackTarget.getX(), attackTarget.getY());
		int collision = (int) npc.getTemplate().getCollisionRadius();
		int combinedCollision = collision + collision;
		int range = npc.getStat().getPhysicalAttackRange() + combinedCollision;
		
		if (npc.isMovementDisabled())
		{
			// If you are immobilized and do not have to rank your goal we are looking for a new one
			if (!Util.checkIfInRange(range, activeActor, attackTarget, false))
			{
				attackTarget = targetReconsider(range, true);
			}
			
			// Any AI type, even healer or mage, will try to melee attack if it can't do anything else (desesperate situation).
			if ((attackTarget != null) && Util.checkIfInRange(range, activeActor, attackTarget, false))
			{
				activeActor.doAttack(attackTarget);
			}
			
			return;
		}
		
		if (attackTarget.isMoving())
		{
			range = range + 50;
			if (npc.isMoving())
			{
				range = range + 50;
			}
		}
		
		if (Rnd.get(100) <= 3)
		{
			for (L2Attackable nearby : npc.getKnownList().getObjectTypeInRadius(L2Attackable.class, collision))
			{
				if (nearby != attackTarget)
				{
					int newX = combinedCollision + Rnd.get(40);
					if (Rnd.nextBoolean())
					{
						newX = attackTarget.getX() + newX;
					}
					else
					{
						newX = attackTarget.getX() - newX;
					}
					
					int newY = combinedCollision + Rnd.get(40);
					if (Rnd.nextBoolean())
					{
						newY = attackTarget.getY() + newY;
					}
					else
					{
						newY = attackTarget.getY() - newY;
					}
					
					if (!npc.isInsideRadius(newX, newY, collision, false))
					{
						int newZ = npc.getZ() + 30;
						if (GeoEngine.getInstance().canMoveToTarget(npc.getX(), npc.getY(), npc.getZ(), newX, newY, newZ))
						{
							moveTo(newX, newY, newZ);
						}
					}
					return;
				}
			}
		}
		
		// for archers
		ItemWeapon weapon = activeActor.getActiveWeaponItem();
		if ((weapon != null) && (weapon.getType() == WeaponType.BOW))
		{
			if (Util.checkIfInRange(100, activeActor, attackTarget, false) && (Rnd.get(4) > 1))
			{
				final int posX = npc.getX() + ((attackTarget.getX() < npc.getX()) ? 300 : -300);
				final int posY = npc.getY() + ((attackTarget.getY() < npc.getY()) ? 300 : -300);
				final int posZ = npc.getZ() + 30;
				
				if (GeoEngine.getInstance().canMoveToTarget(npc.getX(), npc.getY(), npc.getZ(), posX, posY, posZ))
				{
					setIntention(CtrlIntentionType.MOVE_TO, new LocationHolder(posX, posY, posZ));
					return;
				}
			}
		}
		
		// Check if the actor isn't far from target
		if (dist > (range * range))
		{
			// Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
			if (attackTarget.isMoving())
			{
				range -= 100;
			}
			if (range < 5)
			{
				range = 5;
			}
			moveToPawn(getTarget(), range);
			return;
		}
		
		if (maybeMoveToPawn(getTarget(), activeActor.getStat().getPhysicalAttackRange()))
		{
			return;
		}
		
		// Finally, physical attacks
		clientStopMoving(null);
		getActor().doAttack(attackTarget);
	}
	
	private static boolean canUseSkill(L2Character actor, Skill sk)
	{
		if (sk.isPassive() || (sk.getSkillType() == SkillType.NOTDONE))
		{
			return false;
		}
		// Check if skill if disable
		if (actor.isSkillDisabled(sk))
		{
			return false;
		}
		// Check mp
		if (actor.getCurrentMp() < actor.getStat().getMpConsume(sk))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Method used when the actor can't attack his current target (immobilize state, for example).
	 * <ul>
	 * <li>If the actor got an hate list, pickup a new target from it.</li>
	 * <li>If the actor didn't find a target on his hate list, check if he is aggro type and pickup a new target using his knownlist.</li>
	 * </ul>
	 * @param  range      The range to check (skill range for skill ; physical range for mele).
	 * @param  rangeCheck That boolean is used to see if a check based on the distance must be made (skill check).
	 * @return            The new L2Character victim.
	 */
	protected L2Character targetReconsider(int range, boolean rangeCheck)
	{
		final L2Attackable actor = (L2Attackable) activeActor;
		
		// Verify first if aggro list is empty, if not search a victim following his aggro position.
		if (!actor.noHasTarget())
		{
			// Store aggro value && most hated, in order to add it to the random target we will choose.
			final L2Character previousMostHated = actor.getMostHated();
			final int aggroMostHated = actor.getHating(previousMostHated);
			
			for (L2Character obj : actor.getAggroList().keySet())
			{
				if (!autoAttackCondition(obj))
				{
					continue;
				}
				
				if (rangeCheck)
				{
					// Verify the distance, -15 if the victim is moving, -15 if the npc is moving.
					double dist = Math.sqrt(actor.getPlanDistanceSq(obj.getX(), obj.getY())) - obj.getTemplate().getCollisionRadius();
					if (actor.isMoving())
					{
						dist -= 15;
					}
					
					if (obj.isMoving())
					{
						dist -= 15;
					}
					
					if (dist > range)
					{
						continue;
					}
				}
				
				// Stop to hate the most hated.
				actor.stopHating(previousMostHated);
				
				// Add previous most hated aggro to that new victim.
				actor.addDamageHate(obj, 0, (aggroMostHated > 0) ? aggroMostHated : 2000);
				return obj;
			}
		}
		
		// If hate list gave nothing, then verify first if the actor is aggressive, and then pickup a victim from his knownlist.
		if (actor.isAggressive())
		{
			for (L2Character target : actor.getKnownList().getObjectTypeInRadius(L2Character.class, actor.getAggroRange()))
			{
				if (!autoAttackCondition(target))
				{
					continue;
				}
				
				if (rangeCheck)
				{
					// Verify the distance, -15 if the victim is moving, -15 if the npc is moving.
					double dist = Math.sqrt(actor.getPlanDistanceSq(target.getX(), target.getY())) - target.getTemplate().getCollisionRadius();
					if (actor.isMoving())
					{
						dist -= 15;
					}
					
					if (target.isMoving())
					{
						dist -= 15;
					}
					
					if (dist > range)
					{
						continue;
					}
				}
				
				// Only 1 aggro, as the hate list is supposed to be cleaned. Simulate an aggro range entrance.
				actor.addDamageHate(target, 0, 1);
				return target;
			}
		}
		
		// Return null if no new victim has been found.
		return null;
	}
	
	/**
	 * Method used for chaotic mode (RBs / GBs and their minions).<br>
	 * @param  oldTarget The previous target, reused if no available target is found.
	 * @return           old target if none could fits or the new target.
	 */
	protected L2Character aggroReconsider(L2Character oldTarget)
	{
		final L2Attackable actor = (L2Attackable) activeActor;
		
		// Choose a new victim, and make checks to see if it fits.
		for (L2Character victim : actor.getAggroList().keySet())
		{
			if (!autoAttackCondition(victim))
			{
				continue;
			}
			
			// Add most hated aggro to the victim aggro.
			actor.addDamageHate(victim, 0, actor.getHating(actor.getMostHated()));
			return victim;
		}
		return oldTarget;
	}
	
	/**
	 * Manage AI thinking actions of a L2Attackable.
	 */
	@Override
	protected void onEvtThink()
	{
		// Check if the actor can't use skills and if a thinking action isn't already in progress
		if (thinking || activeActor.isAllSkillsDisabled())
		{
			return;
		}
		
		// Start thinking action
		thinking = true;
		
		try
		{
			// Manage AI thoughts.
			switch (getIntention())
			{
				case ACTIVE:
					thinkActive();
					break;
				case ATTACK:
					thinkAttack();
					break;
			}
		}
		finally
		{
			// Stop thinking action
			thinking = false;
		}
	}
	
	/**
	 * Launch actions corresponding to the Event Attacked.<BR>
	 * <b><u>Actions</u>:</b><BR>
	 * <li>Init the attack : Calculate the attack timeout, Set the globalAggro to 0, Add the attacker to the actor aggroList</li>
	 * <li>Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance</li>
	 * <li>Set the Intention to ATTACK</li>
	 * @param attacker The L2Character that attacks the actor
	 */
	@Override
	protected void onEvtAttacked(L2Character attacker)
	{
		final L2Attackable me = (L2Attackable) activeActor;
		
		// Calculate the attack timeout
		attackTimeOut = MAX_ATTACK_TIMEOUT + System.currentTimeMillis();
		
		// Set the globalAggro to 0 to permit attack even just after spawn
		if (globalAggro < 0)
		{
			globalAggro = 0;
		}
		
		// Add the attacker to the aggroList of the actor
		me.addDamageHate(attacker, 0, 1);
		
		// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
		if (!me.isRunning())
		{
			me.setRunning();
		}
		
		// Set the Intention to ATTACK
		if (getIntention() != CtrlIntentionType.ATTACK)
		{
			setIntention(CtrlIntentionType.ATTACK, attacker);
		}
		else if (me.getMostHated() != getTarget())
		{
			setIntention(CtrlIntentionType.ATTACK, attacker);
		}
		
		callMinions(me, attacker);
		
		super.onEvtAttacked(attacker);
	}
	
	private void callMinions(L2Attackable me, L2Character attacker)
	{
		if (attacker == null)
		{
			if (me.getMostHated() == null)
			{
				return;
			}
			attacker = me.getMostHated();
		}
		// If this attackable is a L2MonsterInstance and it has spawned minions, call its minions to battle
		if (me instanceof L2MonsterInstance)
		{
			L2MonsterInstance master = (L2MonsterInstance) me;
			
			if (me instanceof L2MinionInstance)
			{
				master = ((L2MinionInstance) me).getLeader();
				
				if ((master != null) && !master.isDead())
				{
					master.addDamage(attacker, 1);
					master.getMinionList().callToAssist(attacker);
				}
			}
			else if (master.hasMinions())
			{
				master.getMinionList().callToAssist(attacker);
			}
		}
	}
	
	/**
	 * Launch actions corresponding to the Event Aggression.<BR>
	 * <b><u>Actions</u>:</b><BR>
	 * <li>Add the target to the actor aggroList or update hate if already present</li>
	 * <li>Set the actor Intention to ATTACK (if actor is L2GuardInstance check if it isn't too far from its home location)</li>
	 * @param target The L2Character that attacks
	 * @param aggro  The value of hate to add to the actor against the target
	 */
	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
		final L2Attackable me = (L2Attackable) activeActor;
		
		if (target != null)
		{
			// Add the target to the actor aggroList or update hate if already present
			me.addDamageHate(target, 0, aggro);
			
			// Get the hate of the actor against the target
			// only if hate is definitely reduced
			if (aggro < 0)
			{
				if (me.getHating(target) <= 0)
				{
					if (me.getMostHated() == null)
					{
						globalAggro = -25;
						me.clearAggroList();
						setIntention(CtrlIntentionType.ACTIVE);
						me.setWalking();
					}
				}
				return;
			}
			
			// Set the actor AI Intention to ATTACK
			if (getIntention() != CtrlIntentionType.ATTACK)
			{
				// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
				if (!me.isRunning())
				{
					me.setRunning();
				}
				
				setIntention(CtrlIntentionType.ATTACK, target);
			}
			
			callMinions(me, target);
		}
	}
	
	@Override
	protected void onIntentionActive()
	{
		// Cancel attack timeout
		attackTimeOut = Long.MAX_VALUE;
		
		super.onIntentionActive();
	}
}
