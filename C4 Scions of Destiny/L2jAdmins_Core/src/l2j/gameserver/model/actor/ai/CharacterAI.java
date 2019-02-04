package l2j.gameserver.model.actor.ai;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.enums.ItemLocationType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.AutoAttackStop;
import l2j.gameserver.task.continuous.AttackStanceTaskManager;

/**
 * This class manages AI of L2Character.<br>
 * L2CharacterAI :<br>
 * <li>L2AttackableAI
 * <li>L2DoorAI
 * <li>L2PlayerAI
 * <li>L2SummonAI</li>
 */
public class CharacterAI extends AbstractAI
{
	public class IntentionCommand
	{
		public CtrlIntentionType crtlIntention;
		public Object arg0, arg1;
		
		public IntentionCommand(CtrlIntentionType pIntention, Object pArg0, Object pArg1)
		{
			crtlIntention = pIntention;
			arg0 = pArg0;
			arg1 = pArg1;
		}
	}
	
	/**
	 * Constructor of L2CharacterAI.
	 * @param actor
	 */
	public CharacterAI(L2Character actor)
	{
		super(actor);
	}
	
	public IntentionCommand getNextIntention()
	{
		return null;
	}
	
	@Override
	protected void onEvtAttacked(L2Character attacker)
	{
		clientStartAutoAttack();
	}
	
	/**
	 * Manage the Idle Intention : Stop Attack, Movement and Stand Up the actor.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Set the AI Intention to {@link CtrlIntentionType#IDLE}
	 * <li>Init cast and attack target
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
	 * <li>Stand up the actor server side AND client side by sending Server->Client packet ChangeWaitType (broadcast)</li>
	 */
	@Override
	protected void onIntentionIdle()
	{
		// Set the AI Intention to AI_INTENTION_IDLE
		changeIntention(CtrlIntentionType.IDLE, null, null);
		
		// Init cast and attack target
		setTarget(null);
		
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);
		
		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		clientStopAutoAttack();
	}
	
	/**
	 * Manage the Active Intention : Stop Attack, Movement and Launch Think Event.<br>
	 * <b><u> Actions</u> : <I>if the Intention is not already Active</I></b><br>
	 * <li>Set the AI Intention to {@link CtrlIntentionType#ACTIVE}
	 * <li>Init cast and attack target
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
	 * <li>Launch the Think Event</li>
	 */
	@Override
	protected void onIntentionActive()
	{
		// Check if the Intention is not already Active
		if (getIntention() != CtrlIntentionType.ACTIVE)
		{
			// Set the AI Intention to ACTIVE
			changeIntention(CtrlIntentionType.ACTIVE, null, null);
			
			// Init cast and attack target
			setTarget(null);
			
			// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
			clientStopMoving(null);
			
			// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
			clientStopAutoAttack();
			
			// Also enable random animations for this L2Character if allowed
			// This is only for mobs - town npcs are handled in their constructor
			if (activeActor instanceof L2Attackable)
			{
				((L2Npc) activeActor).startRandomAnimationTimer();
			}
			
			// Launch the Think Event
			onEvtThink();
		}
	}
	
	/**
	 * Manage the Rest Intention.<br>
	 * <b><u> Actions</u> : </b><br>
	 * <li>Set the AI Intention to {@link CtrlIntentionType#IDLE}</li>
	 */
	@Override
	protected void onIntentionRest()
	{
		// Set the AI Intention to AI_INTENTION_IDLE
		setIntention(CtrlIntentionType.IDLE);
	}
	
	/**
	 * Manage the Attack Intention : Stop current Attack (if necessary), Start a new Attack and Launch Think Event.<br>
	 * <b><u> Actions</u> : </b><br>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Set the Intention of this AI to {@link CtrlIntentionType#ATTACK}
	 * <li>Set or change the AI attack target
	 * <li>Start the actor Auto Attack client side by sending Server->Client packet AutoAttackStart (broadcast)
	 * <li>Launch the Think Event <b><u> Overridden in</u> :</b><br>
	 * <li>L2AttackableAI : Calculate attack timeout</li>
	 */
	@Override
	protected void onIntentionAttack(L2Character target)
	{
		if (target == null)
		{
			clientActionFailed();
			return;
		}
		
		if (getIntention() == CtrlIntentionType.REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		
		if (activeActor.isAllSkillsDisabled() || activeActor.isCastingNow() || activeActor.isAfraid())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		
		// Check if the Intention is already ATTACK
		if (getIntention() == CtrlIntentionType.ATTACK)
		{
			// Check if the AI already targets the L2Character
			if (getTarget() != target)
			{
				// Set the AI attack target (change target)
				setTarget(target);
				
				stopFollow();
				
				// Launch the Think Event
				notifyEvent(CtrlEventType.THINK);
			}
			else
			{
				clientActionFailed(); // else client freezes until cancel target
			}
		}
		else
		{
			// Set the AI attack target
			setTarget(target);
			
			// Set the Intention of this AbstractAI to ATTACK
			changeIntention(CtrlIntentionType.ATTACK, target, null);
			
			stopFollow();
			
			// Launch the Think Event
			notifyEvent(CtrlEventType.THINK);
		}
	}
	
	/**
	 * Manage the Cast Intention : Stop current Attack, Init the AI in order to cast and Launch Think Event.<br>
	 * <b><u> Actions</u> : </b><br>
	 * <li>Set the AI cast target
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
	 * <li>Set the AI skill used by {@link CtrlIntentionType#CAST}
	 * <li>Set the Intention of this AI to {@link CtrlIntentionType#CAST}
	 * <li>Launch the Think Event</li>
	 */
	@Override
	protected void onIntentionCast(Skill skill, L2Object target)
	{
		if ((getIntention() == CtrlIntentionType.REST) && skill.isMagic())
		{
			clientActionFailed();
			activeActor.setIsCastingNow(false);
			return;
		}
		
		// Set the AI cast target
		setTarget(target);
		
		// Stop actions client-side to cast the skill
		if (skill.getHitTime() > 50)
		{
			// Abort the attack of the L2Character and send Server->Client ActionFailed packet
			activeActor.abortAttack();
		}
		
		// Set the AI skill used by CAST
		currentSkill = skill;
		
		// Change the Intention of this AbstractAI to CAST
		changeIntention(CtrlIntentionType.CAST, skill, target);
		
		// Launch the Think Event
		notifyEvent(CtrlEventType.THINK, null);
	}
	
	/**
	 * Manage the Move To Intention : Stop current Attack and Launch a Move to Location Task.<br>
	 * <b><u> Actions</u>:</b><br>
	 * <li>Stop the actor auto-attack server side AND client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Set the Intention of this AI to {@link CtrlIntentionType#MOVE_TO}
	 * <li>Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)</li>
	 */
	@Override
	protected void onIntentionMoveTo(LocationHolder pos)
	{
		if (getIntention() == CtrlIntentionType.REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		
		if (activeActor.isAllSkillsDisabled() || activeActor.isCastingNow())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		
		// Set the Intention of this AbstractAI to MOVE_TO
		changeIntention(CtrlIntentionType.MOVE_TO, pos, null);
		
		// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
		moveTo(pos.getX(), pos.getY(), pos.getZ());
	}
	
	/**
	 * Manage the Follow Intention : Stop current Attack and Launch a Follow Task.<br>
	 * <b><u> Actions</u>:</b><br>
	 * <li>Stop the actor auto-attack server side AND client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Set the Intention of this AI to AI_INTENTION_FOLLOW
	 * <li>Create and Launch an AI Follow Task to execute every 1s
	 */
	@Override
	protected void onIntentionFollow(L2Character target)
	{
		if (getIntention() == CtrlIntentionType.REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		
		if (activeActor.isAllSkillsDisabled() || activeActor.isCastingNow())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		
		if (activeActor.isMovementDisabled())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		
		// Dead actors can`t follow
		if (activeActor.isDead())
		{
			clientActionFailed();
			return;
		}
		
		// do not follow yourself
		if (activeActor == target)
		{
			clientActionFailed();
			return;
		}
		
		// Set the Intention of this AbstractAI to AI_INTENTION_FOLLOW
		changeIntention(CtrlIntentionType.FOLLOW, target, null);
		
		// Create and Launch an AI Follow Task to execute every 1s
		startFollow(target);
	}
	
	/**
	 * Manage the PickUp Intention : Set the pick up target and Launch a Move To Pawn Task (offset=20).<br>
	 * <b><u> Actions</u> : </b><br>
	 * <li>Set the AI pick up target
	 * <li>Set the Intention of this AI to AI_INTENTION_PICK_UP
	 * <li>Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)</li>
	 */
	@Override
	protected void onIntentionPickUp(L2Object object)
	{
		if (getIntention() == CtrlIntentionType.REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		
		if (activeActor.isAllSkillsDisabled())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		
		if ((object.getX() == 0) && (object.getY() == 0))
		{
			object.setXYZ(getActor().getX(), getActor().getY(), getActor().getZ() + 5);
			return;
		}
		
		if ((object instanceof ItemInstance) && (((ItemInstance) object).getLocation() != ItemLocationType.VOID))
		{
			return;
		}
		
		// Set the Intention of this AbstractAI to AI_INTENTION_PICK_UP
		changeIntention(CtrlIntentionType.PICK_UP, object, null);
		
		// Set the AI pick up target
		setTarget(object);
		
		// Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
		moveToPawn(object, 20);
	}
	
	/**
	 * Manage the Interact Intention : Set the interact target and Launch a Move To Pawn Task (offset=60).<br>
	 * <b><u> Actions</u>:</b><br>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Set the AI interact target
	 * <li>Set the Intention of this AI to AI_INTENTION_INTERACT
	 * <li>Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)</li>
	 */
	@Override
	protected void onIntentionInteract(L2Object object)
	{
		if (getIntention() == CtrlIntentionType.REST)
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		
		if (activeActor.isAllSkillsDisabled())
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			clientActionFailed();
			return;
		}
		
		if (getIntention() != CtrlIntentionType.INTERACT)
		{
			// Set the Intention of this AbstractAI to AI_INTENTION_INTERACT
			changeIntention(CtrlIntentionType.INTERACT, object, null);
			
			// Set the AI interact target
			setTarget(object);
			
			// Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
			moveToPawn(object, 60);
		}
	}
	
	@Override
	protected void onEvtThink()
	{
		// do nothing
	}
	
	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{
		// do nothing
	}
	
	/**
	 * Launch actions corresponding to the Event Stunned then onAttacked Event.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
	 * <li>Break an attack and send Server->Client ActionFailed packet and a System Message to the L2Character
	 * <li>Break a cast and send Server->Client ActionFailed packet and a System Message to the L2Character
	 * <li>Launch actions corresponding to the Event onAttacked (only for L2AttackableAI after the stunning period)</li>
	 */
	@Override
	protected void onEvtStunned(L2Character attacker)
	{
		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		activeActor.broadcastPacket(new AutoAttackStop(activeActor));
		
		AttackStanceTaskManager.getInstance().remove(activeActor);
		
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);
		
		// Launch actions corresponding to the Event onAttacked (only for L2AttackableAI after the stunning period)
		onEvtAttacked(attacker);
	}
	
	/**
	 * Launch actions corresponding to the Event Paralyzed then onAttacked Event.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
	 * <li>Break an attack and send Server->Client ActionFailed packet and a System Message to the L2Character
	 * <li>Break a cast and send Server->Client ActionFailed packet and a System Message to the L2Character
	 * <li>Launch actions corresponding to the Event onAttacked (only for L2AttackableAI after the paralyzing period)
	 */
	@Override
	protected void onEvtParalyzed(L2Character attacker)
	{
		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		activeActor.broadcastPacket(new AutoAttackStop(activeActor));
		
		AttackStanceTaskManager.getInstance().remove(activeActor);
		
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);
		
		// Launch actions corresponding to the Event onAttacked (only for L2AttackableAI after the paralyzing period)
		onEvtAttacked(attacker);
	}
	
	/**
	 * Launch actions corresponding to the Event Sleeping.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
	 * <li>Break an attack and send Server->Client ActionFailed packet and a System Message to the L2Character
	 * <li>Break a cast and send Server->Client ActionFailed packet and a System Message to the L2Character
	 */
	@Override
	protected void onEvtSleeping(L2Character attacker)
	{
		// Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
		activeActor.broadcastPacket(new AutoAttackStop(activeActor));
		
		AttackStanceTaskManager.getInstance().remove(activeActor);
		
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);
	}
	
	/**
	 * Launch actions corresponding to the Event Rooted.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
	 * <li>Launch actions corresponding to the Event onAttacked</li>
	 */
	@Override
	protected void onEvtRooted(L2Character attacker)
	{
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);
		
		// Launch actions corresponding to the Event onAttacked
		onEvtAttacked(attacker);
	}
	
	/**
	 * Launch actions corresponding to the Event Confused.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
	 * <li>Launch actions corresponding to the Event onAttacked</li>
	 */
	@Override
	protected void onEvtConfused(L2Character attacker)
	{
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);
		
		// Launch actions corresponding to the Event onAttacked
		onEvtAttacked(attacker);
	}
	
	/**
	 * Launch actions corresponding to the Event Muted.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Break a cast and send Server->Client ActionFailed packet and a System Message to the L2Character
	 */
	@Override
	protected void onEvtMuted(L2Character attacker)
	{
		// Break a cast and send Server->Client ActionFailed packet and a System Message to the L2Character
		onEvtAttacked(attacker);
	}
	
	/**
	 * Launch actions corresponding to the Event ReadyToAct.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Launch actions corresponding to the Event Think
	 */
	@Override
	protected void onEvtReadyToAct()
	{
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}
	
	/**
	 * Launch actions corresponding to the Event Arrived. <b><u>Actions</u>:</b><br>
	 * <li>If the Intention was {@link CtrlIntentionType#MOVE_TO}, set the Intention to {@link CtrlIntentionType#ACTIVE}
	 * <li>Launch actions corresponding to the Event Think</li>
	 */
	@Override
	protected void onEvtArrived()
	{
		// Launch an explore task if necessary
		if (getActor() instanceof L2PcInstance)
		{
			if (((L2PcInstance) getActor()).isPendingSitting())
			{
				((L2PcInstance) getActor()).sitDown();
			}
		}
		getActor().revalidateZone(true);
		
		if (getActor().moveToNextRoutePoint())
		{
			return;
		}
		
		if (getActor() instanceof L2Attackable)
		{
			((L2Attackable) getActor()).setIsReturningToSpawnPoint(false);
		}
		
		clientStoppedMoving();
		
		// If the Intention was MOVE_TO, set the Intention to ACTIVE
		if (getIntention() == CtrlIntentionType.MOVE_TO)
		{
			setIntention(CtrlIntentionType.ACTIVE);
		}
		
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}
	
	/**
	 * Launch actions corresponding to the Event ArrivedBlocked.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
	 * <li>If the Intention was {@link CtrlIntentionType#MOVE_TO}, set the Intention to {@link CtrlIntentionType#ACTIVE}
	 * <li>Launch actions corresponding to the Event Think</li>
	 */
	@Override
	protected void onEvtArrivedBlocked(LocationHolder blocked_at_pos)
	{
		// If the Intention was MOVE_TO, set the Intention to ACTIVE
		if ((getIntention() == CtrlIntentionType.MOVE_TO) || (getIntention() == CtrlIntentionType.CAST))
		{
			setIntention(CtrlIntentionType.ACTIVE);
		}
		
		// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(blocked_at_pos);
		
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}
	
	/**
	 * Launch actions corresponding to the Event ForgetObject.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>If the object was targeted and the Intention was AI_INTENTION_INTERACT or AI_INTENTION_PICK_UP, set the Intention to {@link CtrlIntentionType#ACTIVE}
	 * <li>If the object was targeted to attack, stop the auto-attack, cancel target and set the Intention to {@link CtrlIntentionType#ACTIVE}
	 * <li>If the object was targeted to cast, cancel target and set the Intention to {@link CtrlIntentionType#ACTIVE}
	 * <li>If the object was targeted to follow, stop the movement, cancel AI Follow Task and set the Intention to {@link CtrlIntentionType#ACTIVE}
	 * <li>If the targeted object was the actor , cancel AI target, stop AI Follow Task, stop the movement and set the Intention to {@link CtrlIntentionType#IDLE}</li>
	 */
	@Override
	protected void onEvtForgetObject(L2Object object)
	{
		// Check if the object was targeted to attack
		if (getTarget() == object)
		{
			// Cancel attack target
			setTarget(null);
			
			// Set the Intention of this AbstractAI to ACTIVE
			setIntention(CtrlIntentionType.ACTIVE);
		}
		
		// Check if the object was targeted to follow
		if (getFollowTarget() == object)
		{
			// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
			clientStopMoving(null);
			
			// Stop an AI Follow Task
			stopFollow();
			
			// Set the Intention of this AbstractAI to ACTIVE
			setIntention(CtrlIntentionType.ACTIVE);
		}
		
		// Check if the targeted object was the actor
		if (activeActor == object)
		{
			// Cancel AI target
			setTarget(null);
			
			// Stop an AI Follow Task
			stopFollow();
			
			// Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
			clientStopMoving(null);
			
			// Set the Intention of this AbstractAI to AI_INTENTION_IDLE
			changeIntention(CtrlIntentionType.IDLE, null, null);
		}
	}
	
	/**
	 * Launch actions corresponding to the Event Cancel.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Stop an AI Follow Task
	 * <li>Launch actions corresponding to the Event Think</li>
	 */
	@Override
	protected void onEvtCancel()
	{
		activeActor.abortCast();
		
		// Stop an AI Follow Task
		stopFollow();
		
		if (!AttackStanceTaskManager.getInstance().isInAttackStance(activeActor))
		{
			activeActor.broadcastPacket(new AutoAttackStop(activeActor));
		}
		
		// Launch actions corresponding to the Event Think
		onEvtThink();
	}
	
	/**
	 * Launch actions corresponding to the Event Dead.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Stop an AI Follow Task
	 * <li>Kill the actor client side by sending Server->Client packet AutoAttackStop, StopMove/StopRotation, Die (broadcast)
	 */
	@Override
	protected void onEvtDead()
	{
		// Stop an AI Tasks
		stopAITask();
		
		// Kill the actor client side by sending Server->Client packet AutoAttackStop, StopMove/StopRotation, Die (broadcast)
		clientNotifyDead();
		
		if (!(activeActor instanceof L2Playable))
		{
			activeActor.setWalking();
		}
	}
	
	/**
	 * Launch actions corresponding to the Event Fake Death.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Stop an AI Follow Task</li>
	 */
	@Override
	protected void onEvtFakeDeath()
	{
		// Stop an AI Follow Task
		stopFollow();
		
		// Stop the actor movement and send Server->Client packet StopMove/StopRotation (broadcast)
		clientStopMoving(null);
		
		// Init AI
		setIntention(CtrlIntentionType.IDLE);
		setTarget(null);
	}
	
	@Override
	protected void onEvtFinishCasting()
	{
		// do nothing
	}
	
	/**
	 * Manage the Move to Pawn action in function of the distance and of the Interact area.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Get the distance between the current position of the L2Character and the target (x,y)
	 * <li>If the distance > offset+20, move the actor (by running) to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
	 * <li>If the distance <= offset+20, Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast) <b><u> Example of use </u> :</b><br>
	 * <li>L2PLayerAI, L2SummonAI
	 * @param  target The targeted L2Object
	 * @param  offset The Interact area radius
	 * @return        True if a movement must be done
	 */
	public boolean maybeMoveToPawn(L2Object target, int offset)
	{
		// Get the distance between the current position of the L2Character and the target (x,y)
		if ((target == null) || (offset < 0))
		{
			return false;
		}
		
		offset += (int) activeActor.getTemplate().getCollisionRadius();
		if (target instanceof L2Character)
		{
			offset += (int) ((L2Character) target).getTemplate().getCollisionRadius();
		}
		
		if (!activeActor.isInsideRadius(target, offset, false, false))
		{
			// Caller should be L2Playable and thinkAttack/thinkCast/thinkInteract/thinkPickUp
			if (getFollowTarget() != null)
			{
				// int foffset = offset + (((L2Character) target).isMoving() ? 100 : 0);
				
				// allow larger hit range when the target is moving (check is run only once per second)
				if (!activeActor.isInsideRadius(target, offset + 100, false, false))
				{
					if (!activeActor.isAttackingNow() || (activeActor instanceof L2Summon))
					{
						moveToPawn(target, offset);
					}
					return true;
				}
				
				stopFollow();
				return false;
			}
			
			if (activeActor.isMovementDisabled() && !(activeActor instanceof L2Attackable))
			{
				if (getIntention() == CtrlIntentionType.ATTACK)
				{
					setIntention(CtrlIntentionType.IDLE);
					clientActionFailed();
				}
				return true;
			}
			
			// If not running, set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
			if (!(this instanceof PlayerAI) && !(this instanceof SummonAI))
			{
				activeActor.setRunning();
			}
			
			if ((target instanceof L2Character) && !(target instanceof L2DoorInstance))
			{
				if (((L2Character) target).isMoving())
				{
					offset -= 30;
				}
				
				if (offset < 5)
				{
					offset = 5;
				}
				
				startFollow((L2Character) target, offset);
			}
			else
			{
				// Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
				moveToPawn(target, offset);
			}
			return true;
		}
		
		stopFollow();
		return false;
	}
	
	/**
	 * Modify current Intention and actions if the target is lost or dead.<br>
	 * <b><u> Actions</u> :<br>
	 * <I>If the target is lost or dead</I></b><br>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
	 * <li>Set the Intention of this AbstractAI to {@link CtrlIntentionType#ACTIVE} <b><u> Example of use </u> :</b><br>
	 * <li>L2PLayerAI, L2SummonAI
	 * @param  target The targeted L2Object
	 * @return        True if the target is lost or dead (false if fake death)
	 */
	protected boolean checkTargetLostOrDead(L2Character target)
	{
		if ((target == null) || target.isAlikeDead())
		{
			// check if player is fake death
			if ((target instanceof L2PcInstance) && ((L2PcInstance) target).isFakeDeath())
			{
				target.stopFakeDeath(true);
				return false;
			}
			
			// Set the Intention of this AbstractAI to ACTIVE
			setIntention(CtrlIntentionType.ACTIVE);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Modify current Intention and actions if the target is lost.<br>
	 * <b><u> Actions</u> :<br>
	 * <I>If the target is lost</I></b><br>
	 * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
	 * <li>Set the Intention of this AbstractAI to {@link CtrlIntentionType#ACTIVE} <b><u> Example of use </u> :</b><br>
	 * <li>L2PLayerAI, L2SummonAI
	 * @param  target The targeted L2Object
	 * @return        True if the target is lost
	 */
	protected boolean checkTargetLost(L2Object target)
	{
		// check if player is fakedeath
		if (target instanceof L2PcInstance)
		{
			L2PcInstance victim = (L2PcInstance) target;
			
			if (victim.isFakeDeath())
			{
				victim.stopFakeDeath(true);
				return false;
			}
		}
		
		if (target == null)
		{
			// Set the Intention of this AbstractAI to ACTIVE
			setIntention(CtrlIntentionType.ACTIVE);
			return true;
		}
		
		return false;
	}
	
	public void stopAITask()
	{
		stopFollow();
	}
}
