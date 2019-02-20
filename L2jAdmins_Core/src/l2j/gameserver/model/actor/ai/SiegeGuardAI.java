package l2j.gameserver.model.actor.ai;

import java.util.Collection;
import java.util.concurrent.Future;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2NpcInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeGuardInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.util.Rnd;

/**
 * This class manages AI of L2Attackable.
 */
public class SiegeGuardAI extends CharacterAI implements Runnable
{
	private static final int MAX_ATTACK_TIMEOUT = 120000; // int ticks, i.e. 120 seconds
	
	/** The L2Attackable AI task executed every 1s (call onEvtThink method) */
	private Future<?> aiTask;
	/** The delay after wich the attacked is stopped */
	private long attackTimeout;
	/** The L2Attackable aggro counter */
	private int globalAggro;
	/** The flag used to indicate that a thinking action is in progress */
	private boolean thinking; // to prevent recursive thinking
	
	/**
	 * Constructor of L2AttackableAI.
	 * @param actor
	 */
	public SiegeGuardAI(L2Character actor)
	{
		super(actor);
		
		attackTimeout = Integer.MAX_VALUE;
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
		// Check if the target isn't another guard, folk or a door
		if ((target == null) || (target instanceof L2SiegeGuardInstance) || (target instanceof L2NpcInstance) || (target instanceof L2DoorInstance))
		{
			return false;
		}
		
		// Check if the target isn't dead
		if (target.isAlikeDead())
		{
			return false;
		}
		
		// Check if the target isn't invulnerable
		if (target.isInvul())
		{
			return false;
		}
		
		// Get the owner if the target is a summon
		if (target instanceof L2Summon)
		{
			L2PcInstance owner = ((L2Summon) target).getOwner();
			if (getActiveChar().isInsideRadius(owner, 1000, true, false))
			{
				target = owner;
			}
		}
		
		// Check if the target is a L2PcInstance
		if (target instanceof L2Playable)
		{
			// Check if the target isn't in silent move mode AND too far (>250)
			if (((L2Playable) target).isSilentMoving() && !getActiveChar().isInsideRadius(target, 250, false, false))
			{
				return false;
			}
		}
		
		// Los Check Here
		return (getActiveChar().isAutoAttackable(target) && GeoEngine.getInstance().canSeeTarget(getActiveChar(), target));
	}
	
	/**
	 * Set the Intention of this L2CharacterAI and create an AI Task executed every 1s (call onEvtThink method) for this L2Attackable.<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : If actor knowPlayer isn't EMPTY, AI_INTENTION_IDLE will be change in AI_INTENTION_ACTIVE</B></FONT><BR>
	 * @param intention The new Intention to set to the AI
	 * @param arg0      The first parameter of the Intention
	 * @param arg1      The second parameter of the Intention
	 */
	@Override
	synchronized void changeIntention(CtrlIntentionType intention, Object arg0, Object arg1)
	{
		if (Config.DEBUG)
		{
			LOG.info("L2SiegeAI.changeIntention(" + intention + ", " + arg0 + ", " + arg1 + ")");
		}
		
		if (intention == CtrlIntentionType.IDLE /* || intention == AI_INTENTION_ACTIVE */) // active becomes idle if only a summon is present
		{
			// Check if actor is not dead
			if (!getActiveChar().isAlikeDead())
			{
				L2Attackable npc = getActiveChar();
				
				// If its knownPlayer isn't empty set the Intention to AI_INTENTION_ACTIVE
				if (!npc.getKnownList().getObjectType(L2PcInstance.class).isEmpty())
				{
					intention = CtrlIntentionType.ACTIVE;
				}
				else
				{
					intention = CtrlIntentionType.IDLE;
				}
			}
			
			if (intention == CtrlIntentionType.IDLE)
			{
				// Set the Intention of this L2AttackableAI to AI_INTENTION_IDLE
				super.changeIntention(CtrlIntentionType.IDLE, null, null);
				
				// Stop AI task and detach AI from NPC
				if (aiTask != null)
				{
					aiTask.cancel(true);
					aiTask = null;
				}
				
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
		attackTimeout = MAX_ATTACK_TIMEOUT + System.currentTimeMillis();
		
		// Manage the Attack Intention : Stop current Attack (if necessary), Start a new Attack and Launch Think Event
		super.onIntentionAttack(target);
	}
	
	/**
	 * Manage AI standard thinks of a L2Attackable (called by onEvtThink).<BR>
	 * <b><u>Actions</u>:</b><BR>
	 * <li>Update every 1s the globalAggro counter to come close to 0</li>
	 * <li>If the actor is Aggressive and can attack, add all autoAttackable L2Character in its Aggro Range to its aggroList, chose a target and order to attack it</li>
	 * <li>If the actor can't attack, order to it to return to its home location</li>
	 */
	private void thinkActive()
	{
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
			for (L2Character target : getActiveChar().getKnownList().getObjectTypeInRadius(L2Character.class, ((L2Attackable) getActiveChar()).getStat().getPhysicalAttackRange()))
			{
				if (target == null)
				{
					continue;
				}
				if (autoAttackCondition(target)) // check aggression
				{
					// Get the hate level of the L2Attackable against this L2Character target contained in aggroList
					int hating = getActiveChar().getHating(target);
					
					// Add the attacker to the L2Attackable aggroList with 0 damage and 1 hate
					if (hating == 0)
					{
						getActiveChar().addDamageHate(target, 0, 1);
					}
				}
			}
			
			// Chose a target from its aggroList
			L2Character hated;
			if (getActiveChar().isConfused())
			{
				hated = (L2Character) getTarget(); // Force mobs to attak anybody if confused
			}
			else
			{
				hated = getActiveChar().getMostHated();
			}
			
			// Order to the L2Attackable to attack the target
			if (hated != null)
			{
				// Get the hate level of the L2Attackable against this L2Character target contained in aggroList
				if ((getActiveChar().getHating(hated) + globalAggro) > 0)
				{
					// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
					if (!getActiveChar().isRunning())
					{
						getActiveChar().setRunning();
					}
					
					// Set the AI Intention to AI_INTENTION_ATTACK
					setIntention(CtrlIntentionType.ATTACK, hated);
				}
				
				return;
			}
		}
		
		// Order to the L2SiegeGuardInstance to return to its home location because there's no target to attack
		getActiveChar().returnHome();
	}
	
	/**
	 * Manage AI attack thinks of a L2Attackable (called by onEvtThink).<BR>
	 * <b><u>Actions</u>:</b><BR>
	 * <li>Update the attack timeout if actor is running</li>
	 * <li>If target is dead or timeout is expired, stop this attack and set the Intention to AI_INTENTION_ACTIVE</li>
	 * <li>Call all L2Object of its Faction inside the Faction Range</li>
	 * <li>Chose a target and order to attack it with magic skill or physical attack</li> TODO: Manage casting rules to healer mobs (like Ant Nurses)
	 */
	private void thinkAttack()
	{
		if (attackTimeout < System.currentTimeMillis())
		{
			// Check if the actor is running
			if (getActiveChar().isRunning())
			{
				// Set the actor movement type to walk and send Server->Client packet ChangeMoveType to all others L2PcInstance
				getActiveChar().setWalking();
				
				// Calculate a new attack timeout
				attackTimeout = MAX_ATTACK_TIMEOUT + System.currentTimeMillis();
			}
		}
		
		// Check if target is dead or if timeout is expired to stop this attack
		if ((getTarget() == null) || ((L2Character) getTarget()).isAlikeDead() || (attackTimeout < System.currentTimeMillis()))
		{
			// Stop hating this target after the attack timeout or if target is dead
			if (getTarget() != null)
			{
				L2Attackable npc = getActiveChar();
				int hate = npc.getHating((L2Character) getTarget());
				if (hate > 0)
				{
					npc.addDamageHate((L2Character) getTarget(), 0, -hate);
				}
			}
			
			// Cancel target and timeout
			attackTimeout = Integer.MAX_VALUE;
			setTarget(null);
			
			// Set the AI Intention to AI_INTENTION_ACTIVE
			setIntention(CtrlIntentionType.ACTIVE);
			
			getActiveChar().setWalking();
			return;
		}
		
		attackPrepare();
		factionNotify();
	}
	
	private final void factionNotify()
	{
		// Call all L2Object of its Faction inside the Faction Range
		if ((((L2Npc) getActiveChar()).getFactionId() == null) || (getTarget() == null) || (getActiveChar() == null))
		{
			return;
		}
		
		if (((L2Character) getTarget()).isInvul())
		{
			return;
		}
		
		if (Rnd.get(10) > 4)
		{
			return;
		}
		
		// Go through all L2Object that belong to its faction
		for (L2Character cha : getTarget().getKnownList().getObjectTypeInRadius(L2Character.class, 1000))
		{
			if (cha == null)
			{
				continue;
			}
			
			if (!(cha instanceof L2Npc))
			{
				continue;
			}
			
			L2Npc npc = (L2Npc) cha;
			
			if (!((L2Npc) getActiveChar()).getFactionId().equals(npc.getFactionId()))
			{
				continue;
			}
			
			// Check if the L2Object is inside the Faction Range of the actor
			if (((npc.getAI().getIntention() == CtrlIntentionType.IDLE) || (npc.getAI().getIntention() == CtrlIntentionType.ACTIVE)) && getActiveChar().isInsideRadius(npc, npc.getFactionRange(), false, true) && getActiveChar().getAttackByList().contains(getTarget()) && (npc.getTarget() == null))
			{
				if (GeoEngine.getInstance().canSeeTarget(npc, getTarget()))
				{
					// Notify the L2Object AI with EVT_AGGRESSION
					npc.getAI().notifyEvent(CtrlEventType.AGGRESSION, getTarget(), 1);
				}
			}
		}
	}
	
	private void attackPrepare()
	{
		// Get all information needed to chose between physical or magical attack
		Collection<Skill> skills = null;
		double dist_2 = 0;
		int range = 0;
		
		try
		{
			getActiveChar().setTarget(getTarget());
			skills = getActiveChar().getAllSkills();
			dist_2 = getActiveChar().getPlanDistanceSq(getTarget().getX(), getTarget().getY());
			range = getActiveChar().getStat().getPhysicalAttackRange() + (int) (getActiveChar().getTemplate().getCollisionRadius() + getActiveChar().getTemplate().getCollisionRadius());
		}
		catch (NullPointerException e)
		{
			getActiveChar().setTarget(null);
			setIntention(CtrlIntentionType.IDLE);
			return;
		}
		
		// never attack defenders
		if ((getTarget() instanceof L2PcInstance) && getActiveChar().getCastle().getSiege().isDefender(((L2PcInstance) getTarget()).getClan()))
		{
			// Cancel the target
			getActiveChar().clearHating((L2Character) getTarget());
			getActiveChar().setTarget(null);
			setIntention(CtrlIntentionType.IDLE);
			return;
		}
		
		if (!GeoEngine.getInstance().canSeeTarget(getActiveChar(), getTarget()))
		{
			// Siege guards differ from normal mobs currently:
			// If target cannot be seen, don't attack any more
			getActiveChar().clearHating((L2Character) getTarget());
			getActiveChar().setTarget(null);
			setIntention(CtrlIntentionType.IDLE);
			return;
		}
		
		// Check if the actor isn't muted and if it is far from target
		if (!getActiveChar().isMuted() && (dist_2 > ((range + 20) * (range + 20))))
		{
			// Check if the L2SiegeGuardInstance is attacking, knows the target and can't run
			if (!(getActiveChar().isAttackingNow()) && (getActiveChar().getStat().getRunSpeed() == 0) && (getActiveChar().getKnownList().getObject(getTarget())))
			{
				// Cancel the target
				getActiveChar().getKnownList().removeObject(getTarget());
				getActiveChar().setTarget(null);
				setIntention(CtrlIntentionType.IDLE);
			}
			else
			{
				double dx = getActiveChar().getX() - getTarget().getX();
				double dy = getActiveChar().getY() - getTarget().getY();
				double dz = getActiveChar().getZ() - getTarget().getZ();
				double homeX = getTarget().getX() - getActiveChar().getSpawn().getX();
				double homeY = getTarget().getY() - getActiveChar().getSpawn().getY();
				
				// Check if the L2SiegeGuardInstance isn't too far from it's home location
				if ((((dx * dx) + (dy * dy)) > 10000) && (((homeX * homeX) + (homeY * homeY)) > 3240000) && (getActiveChar().getKnownList().getObject(getTarget())))
				{
					// Cancel the target
					getActiveChar().getKnownList().removeObject(getTarget());
					getActiveChar().setTarget(null);
					setIntention(CtrlIntentionType.IDLE);
				}
				else
				// Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
				{
					// Temporary hack for preventing guards jumping off towers,
					// before replacing this with effective geodata checks and AI modification
					if ((dz * dz) < (170 * 170))
					{
						moveToPawn(getTarget(), range);
					}
				}
			}
			
			return;
			
		}
		// Else, if the actor is muted and far from target, just "move to pawn"
		else if (getActiveChar().isMuted() && (dist_2 > ((range + 20) * (range + 20))))
		{
			// Temporary hack for preventing guards jumping off towers,
			// before replacing this with effective geodata checks and AI modification
			double dz = getActiveChar().getZ() - getTarget().getZ();
			if ((dz * dz) < (170 * 170))
			{
				moveToPawn(getTarget(), range);
			}
			return;
		}
		// Else, if this is close enough to attack
		else if (dist_2 <= ((range + 20) * (range + 20)))
		{
			// Force mobs to attak anybody if confused
			L2Character hated = null;
			if (getActiveChar().isConfused())
			{
				hated = (L2Character) getTarget();
			}
			else
			{
				hated = ((L2Attackable) getActiveChar()).getMostHated();
			}
			
			if (hated == null)
			{
				setIntention(CtrlIntentionType.ACTIVE);
				return;
			}
			if (hated != getTarget())
			{
				setTarget(hated);
			}
			
			attackTimeout = MAX_ATTACK_TIMEOUT + System.currentTimeMillis();
			
			// check for close combat skills && heal/buff skills
			if (!getActiveChar().isMuted() && (Rnd.nextInt(100) <= 5))
			{
				for (Skill sk : skills)
				{
					int castRange = sk.getCastRange();
					
					if (((castRange * castRange) >= dist_2) && (castRange <= 70) && !sk.isPassive() && (getActiveChar().getCurrentMp() >= getActiveChar().getStat().getMpConsume(sk)) && !getActiveChar().isSkillDisabled(sk))
					{
						L2Object OldTarget = getActiveChar().getTarget();
						if ((sk.getSkillType() == SkillType.BUFF) || (sk.getSkillType() == SkillType.HEAL))
						{
							boolean useSkillSelf = true;
							if ((sk.getSkillType() == SkillType.HEAL) && (getActiveChar().getCurrentHp() > (int) (getActiveChar().getStat().getMaxHp() / 1.5)))
							{
								useSkillSelf = false;
								break;
							}
							if (sk.getSkillType() == SkillType.BUFF)
							{
								for (Effect effect : getActiveChar().getAllEffects())
								{
									if ((effect != null) && (effect.getSkill() == sk))
									{
										useSkillSelf = false;
										break;
									}
								}
							}
							if (useSkillSelf)
							{
								getActiveChar().setTarget(getActiveChar());
							}
						}
						
						clientStopMoving(null);
						getActor().doCast(sk);
						getActiveChar().setTarget(OldTarget);
						return;
					}
				}
			}
			// Finally, do the physical attack itself
			getActor().doAttack((L2Character) getTarget());
		}
	}
	
	/**
	 * Manage AI thinking actions of a L2Attackable.
	 */
	@Override
	protected void onEvtThink()
	{
		// Check if the actor can't use skills and if a thinking action isn't already in progress
		if (thinking || getActiveChar().isAllSkillsDisabled())
		{
			return;
		}
		
		// Start thinking action
		thinking = true;
		
		try
		{
			// Manage AI thinks of a L2Attackable
			if (getIntention() == CtrlIntentionType.ACTIVE)
			{
				thinkActive();
			}
			else if (getIntention() == CtrlIntentionType.ATTACK)
			{
				thinkAttack();
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
	 * <li>Set the Intention to AI_INTENTION_ATTACK</li>
	 * @param attacker The L2Character that attacks the actor
	 */
	@Override
	protected void onEvtAttacked(L2Character attacker)
	{
		// Calculate the attack timeout
		attackTimeout = MAX_ATTACK_TIMEOUT + System.currentTimeMillis();
		
		// Set the globalAggro to 0 to permit attack even just after spawn
		if (globalAggro < 0)
		{
			globalAggro = 0;
		}
		
		// Add the attacker to the aggroList of the actor
		((L2Attackable) getActiveChar()).addDamageHate(attacker, 0, 1);
		
		// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
		if (!getActiveChar().isRunning())
		{
			getActiveChar().setRunning();
		}
		
		// Set the Intention to AI_INTENTION_ATTACK
		if (getIntention() != CtrlIntentionType.ATTACK)
		{
			setIntention(CtrlIntentionType.ATTACK, attacker);
		}
		
		super.onEvtAttacked(attacker);
	}
	
	/**
	 * Launch actions corresponding to the Event Aggression.<BR>
	 * <b><u>Actions</u>:</b><BR>
	 * <li>Add the target to the actor aggroList or update hate if already present</li>
	 * <li>Set the actor Intention to AI_INTENTION_ATTACK (if actor is L2GuardInstance check if it isn't too far from its home location)</li>
	 * @param target The L2Character that attacks
	 * @param aggro  The value of hate to add to the actor against the target
	 */
	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
		if (getActiveChar() == null)
		{
			return;
		}
		
		if (target != null)
		{
			// Add the target to the actor aggroList or update hate if already present
			getActiveChar().addDamageHate(target, 0, aggro);
			
			// Get the hate of the actor against the target
			if (getActiveChar().getHating(target) <= 0)
			{
				if (getActiveChar().getMostHated() == null)
				{
					globalAggro = -25;
					getActiveChar().clearAggroList();
					setIntention(CtrlIntentionType.IDLE);
				}
				return;
			}
			
			// Set the actor AI Intention to AI_INTENTION_ATTACK
			if (getIntention() != CtrlIntentionType.ATTACK)
			{
				// Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
				if (!getActiveChar().isRunning())
				{
					activeActor.setRunning();
				}
				
				double homeX = target.getX() - getActiveChar().getSpawn().getX();
				double homeY = target.getY() - getActiveChar().getSpawn().getY();
				
				// Check if the L2SiegeGuardInstance is not too far from its home location
				if (((homeX * homeX) + (homeY * homeY)) < 3240000)
				{
					setIntention(CtrlIntentionType.ATTACK, target);
				}
			}
		}
		else
		{
			// currently only for setting lower general aggro
			if (aggro >= 0)
			{
				return;
			}
			
			L2Character mostHated = getActiveChar().getMostHated();
			if (mostHated == null)
			{
				globalAggro = -25;
				return;
			}
			for (L2Character aggroed : getActiveChar().getAggroList().keySet())
			{
				getActiveChar().addDamageHate(aggroed, 0, aggro);
			}
			
			if (getActiveChar().getHating(mostHated) <= 0)
			{
				globalAggro = -25;
				getActiveChar().clearAggroList();
				setIntention(CtrlIntentionType.IDLE);
			}
		}
	}
	
	@Override
	protected void onEvtDead()
	{
		stopAITask();
		super.onEvtDead();
	}
	
	@Override
	public void stopAITask()
	{
		if (aiTask != null)
		{
			aiTask.cancel(false);
			aiTask = null;
		}
		getActor().detachAI();
	}
	
	private L2SiegeGuardInstance getActiveChar()
	{
		return (L2SiegeGuardInstance) activeActor;
	}
}
