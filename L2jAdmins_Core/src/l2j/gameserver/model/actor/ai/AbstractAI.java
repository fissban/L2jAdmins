package l2j.gameserver.model.actor.ai;

import java.util.concurrent.Future;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.AutoAttackStart;
import l2j.gameserver.network.external.server.AutoAttackStop;
import l2j.gameserver.network.external.server.CharMoveToLocation;
import l2j.gameserver.network.external.server.Die;
import l2j.gameserver.network.external.server.MoveToPawn;
import l2j.gameserver.network.external.server.StopMove;
import l2j.gameserver.network.external.server.StopRotation;
import l2j.gameserver.task.continuous.AttackStanceTaskManager;

/**
 * Mother class of all objects AI in the world.<br>
 * AbastractAI :<br>
 * <li>L2CharacterAI</li>
 */
public abstract class AbstractAI
{
	protected static final Logger LOG = Logger.getLogger(AbstractAI.class.getName());
	
	private NextAction nextAction;
	
	/**
	 * @param nextAction the nextAction to set
	 */
	public void setNextAction(NextAction nextAction)
	{
		this.nextAction = nextAction;
	}
	
	class FollowTask implements Runnable
	{
		int range = 70;
		
		public FollowTask()
		{
		}
		
		public FollowTask(int range)
		{
			this.range = range;
		}
		
		@Override
		public void run()
		{
			// get target
			L2Character follow = followTarget;
			
			// target does not exist
			if (follow == null)
			{
				setIntention(CtrlIntentionType.IDLE);
				return;
			}
			
			// target is not in range, trigger proper AI
			if (!activeActor.isInsideRadius(follow, range, true, false))
			{
				moveToPawn(follow, range);
			}
		}
	}
	
	private static final int FOLLOW_INTERVAL = 1000;
	private static final int ATTACK_FOLLOW_INTERVAL = 500;
	
	protected Future<?> followTask = null;
	
	/** The character that this AI manages */
	final L2Character activeActor;
	
	/** Current long-term intention */
	protected CtrlIntentionType currentIntention = CtrlIntentionType.IDLE;
	/** Current long-term intention parameter */
	protected Object intentionArg0 = null;
	/** Current long-term intention parameter */
	protected Object intentionArg1 = null;
	
	/** Flags about client's state, in order to know which messages to send */
	protected volatile boolean clientMoving;
	
	/** Different targets this AI maintains */
	private L2Object target;
	
	protected L2Character followTarget;
	
	/** The skill we are currently casting by INTENTION_CAST */
	protected Skill currentSkill;
	
	/** Different internal state flags */
	private long moveToPawnTimeout;
	protected int clientMovingToPawnOffset;
	
	/**
	 * Constructor of AbstractAI.
	 * @param actor
	 */
	protected AbstractAI(L2Character actor)
	{
		activeActor = actor;
	}
	
	/**
	 * Return the L2Character managed by this Accessor AI.
	 * @return
	 */
	public L2Character getActor()
	{
		return activeActor;
	}
	
	/**
	 * Return the current Intention.
	 * @return
	 */
	public CtrlIntentionType getIntention()
	{
		return currentIntention;
	}
	
	public boolean hasIntention(CtrlIntentionType ctrlIntentionType)
	{
		return currentIntention == ctrlIntentionType;
	}
	
	/**
	 * Set the Intention of this AbstractAI.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method is USED by AI classes</B></FONT><br>
	 * <B><U> Overridden in </U> : </B><br>
	 * <B>L2AttackableAI</B> : Create an AI Task executed every 1s (if necessary)<br>
	 * <B>L2PlayerAI</B> : Stores the current AI intention parameters to later restore it if necessary<br>
	 * @param intention The new Intention to set to the AI
	 * @param arg0      The first parameter of the Intention
	 * @param arg1      The second parameter of the Intention
	 */
	synchronized void changeIntention(CtrlIntentionType intention, Object arg0, Object arg1)
	{
		currentIntention = intention;
		intentionArg0 = arg0;
		intentionArg1 = arg1;
	}
	
	/**
	 * Launch the L2CharacterAI onIntention method corresponding to the new Intention.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Stop the FOLLOW mode if necessary</B></FONT>
	 * @param intention The new Intention to set to the AI
	 */
	public final void setIntention(CtrlIntentionType intention)
	{
		setIntention(intention, null, null);
	}
	
	/**
	 * Launch the L2CharacterAI onIntention method corresponding to the new Intention.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Stop the FOLLOW mode if necessary</B></FONT><br>
	 * @param intention The new Intention to set to the AI
	 * @param arg0      The first parameter of the Intention (optional target)
	 */
	public final void setIntention(CtrlIntentionType intention, Object arg0)
	{
		setIntention(intention, arg0, null);
	}
	
	/**
	 * Launch the L2CharacterAI onIntention method corresponding to the new Intention.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Stop the FOLLOW mode if necessary</B></FONT><br>
	 * @param intention The new Intention to set to the AI
	 * @param arg0      The first parameter of the Intention (optional target)
	 * @param arg1      The second parameter of the Intention (optional target)
	 */
	public final void setIntention(CtrlIntentionType intention, Object arg0, Object arg1)
	{
		if (activeActor instanceof L2PcInstance)
		{
			if (((L2PcInstance) activeActor).isPendingSitting())
			{
				((L2PcInstance) activeActor).setIsPendingSitting(false);
			}
		}
		
		// Stop the follow mode if necessary
		if ((intention != CtrlIntentionType.FOLLOW) && (intention != CtrlIntentionType.ATTACK))
		{
			stopFollow();
		}
		
		// Launch the onIntention method of the L2CharacterAI corresponding to the new Intention
		switch (intention)
		{
			case IDLE:
				onIntentionIdle();
				break;
			case ACTIVE:
				onIntentionActive();
				break;
			case REST:
				onIntentionRest();
				break;
			case ATTACK:
				onIntentionAttack((L2Character) arg0);
				break;
			case CAST:
				onIntentionCast((Skill) arg0, (L2Object) arg1);
				break;
			case MOVE_TO:
				onIntentionMoveTo((LocationHolder) arg0);
				break;
			case FOLLOW:
				onIntentionFollow((L2Character) arg0);
				break;
			case PICK_UP:
				onIntentionPickUp((L2Object) arg0);
				break;
			case INTERACT:
				onIntentionInteract((L2Object) arg0);
				break;
		}
		
		// If do move or follow intention drop next action.
		if ((nextAction != null) && (nextAction.getIntention() == intention))
		{
			nextAction = null;
		}
	}
	
	/**
	 * Launch the L2CharacterAI onEvt method corresponding to the Event.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : The current general intention won't be change (ex : If the character attack and is stunned, he will attack again after the stunned periode)</B></FONT><br>
	 * @param evt The event whose the AI must be notified
	 */
	public final void notifyEvent(CtrlEventType evt)
	{
		notifyEvent(evt, null, null);
	}
	
	/**
	 * Launch the L2CharacterAI onEvt method corresponding to the Event.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : The current general intention won't be change (ex : If the character attack and is stunned, he will attack again after the stunned periode)</B></FONT><br>
	 * @param evt  The event whose the AI must be notified
	 * @param arg0 The first parameter of the Event (optional target)
	 */
	public final void notifyEvent(CtrlEventType evt, Object arg0)
	{
		notifyEvent(evt, arg0, null);
	}
	
	/**
	 * Launch the L2CharacterAI onEvt method corresponding to the Event.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : The current general intention won't be change (ex : If the character attack and is stunned, he will attack again after the stunned periode)</B></FONT><br>
	 * @param evt  The event whose the AI must be notified
	 * @param arg0 The first parameter of the Event (optional target)
	 * @param arg1 The second parameter of the Event (optional target)
	 */
	public final void notifyEvent(CtrlEventType evt, Object arg0, Object arg1)
	{
		if (!activeActor.isVisible() || !activeActor.hasAI())
		{
			return;
		}
		
		switch (evt)
		{
			case THINK:
				onEvtThink();
				break;
			case ATTACKED:
				onEvtAttacked((L2Character) arg0);
				break;
			case AGGRESSION:
				onEvtAggression((L2Character) arg0, ((Number) arg1).intValue());
				break;
			case STUNNED:
				onEvtStunned((L2Character) arg0);
				break;
			case PARALYZED:
				onEvtParalyzed((L2Character) arg0);
				break;
			case SLEEPING:
				onEvtSleeping((L2Character) arg0);
				break;
			case ROOTED:
				onEvtRooted((L2Character) arg0);
				break;
			case CONFUSED:
				onEvtConfused((L2Character) arg0);
				break;
			case MAGICAL_MUTED:
				onEvtMuted((L2Character) arg0);
				break;
			case READY_TO_ACT:
				if (!activeActor.isCastingNow())
				{
					onEvtReadyToAct();
				}
				break;
			case ARRIVED:
				if (!activeActor.isCastingNow())
				{
					onEvtArrived();
				}
				break;
			case ARRIVED_BLOCKED:
				onEvtArrivedBlocked((LocationHolder) arg0);
				break;
			case FORGET_OBJECT:
				onEvtForgetObject((L2Object) arg0);
				break;
			case CANCEL:
				onEvtCancel();
				break;
			case DEAD:
				onEvtDead();
				break;
			case FAKE_DEATH:
				onEvtFakeDeath();
				break;
			case FINISH_CASTING:
				onEvtFinishCasting();
				break;
		}
		
		// Do next action.
		if ((nextAction != null) && (nextAction.getEvent() == evt))
		{
			nextAction.run();
		}
	}
	
	protected abstract void onIntentionIdle();
	
	protected abstract void onIntentionActive();
	
	protected abstract void onIntentionRest();
	
	protected abstract void onIntentionAttack(L2Character target);
	
	protected abstract void onIntentionCast(Skill skill, L2Object target);
	
	protected abstract void onIntentionMoveTo(LocationHolder destination);
	
	protected abstract void onIntentionFollow(L2Character target);
	
	protected abstract void onIntentionPickUp(L2Object item);
	
	protected abstract void onIntentionInteract(L2Object object);
	
	protected abstract void onEvtThink();
	
	protected abstract void onEvtAttacked(L2Character attacker);
	
	protected abstract void onEvtAggression(L2Character target, int aggro);
	
	protected abstract void onEvtStunned(L2Character attacker);
	
	protected abstract void onEvtParalyzed(L2Character attacker);
	
	protected abstract void onEvtSleeping(L2Character attacker);
	
	protected abstract void onEvtRooted(L2Character attacker);
	
	protected abstract void onEvtConfused(L2Character attacker);
	
	protected abstract void onEvtMuted(L2Character attacker);
	
	protected abstract void onEvtReadyToAct();
	
	protected abstract void onEvtArrived();
	
	protected abstract void onEvtArrivedBlocked(LocationHolder blocked_at_pos);
	
	protected abstract void onEvtForgetObject(L2Object object);
	
	protected abstract void onEvtCancel();
	
	protected abstract void onEvtDead();
	
	protected abstract void onEvtFakeDeath();
	
	protected abstract void onEvtFinishCasting();
	
	/**
	 * Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT><br>
	 * @param pawn
	 * @param offset
	 */
	protected void moveToPawn(L2Object pawn, int offset)
	{
		// Check if actor can move
		if (activeActor.isMovementDisabled())
		{
			activeActor.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		var time = System.currentTimeMillis();
		
		if (offset < 10)
		{
			offset = 10;
		}
		
		if (clientMoving && (target == pawn))
		{
			// prevent possible extra calls to this function (there is none?).
			if (clientMovingToPawnOffset == offset)
			{
				if (time < moveToPawnTimeout)
				{
					activeActor.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
			else if (activeActor.isOnGeodataPath())
			{
				// minimum time to calculate new route is 2 seconds
				if (time < moveToPawnTimeout + 1000)
				{
					activeActor.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		// Set AI movement data
		clientMoving = true;
		target = pawn;
		moveToPawnTimeout = time + 1000;
		clientMovingToPawnOffset = offset;
		
		if (pawn == null)
		{
			activeActor.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Calculate movement data for a move to location action and add the actor to movingObjects of GameTimeController
		activeActor.moveToLocation(pawn.getX(), pawn.getY(), pawn.getZ(), offset);
		
		if (!activeActor.isMoving())
		{
			activeActor.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		AServerPacket send = null;
		// Broadcast MoveToPawn/MoveToLocation packet
		if (target instanceof L2Character)
		{
			if (activeActor.isOnGeodataPath())
			{
				clientMovingToPawnOffset = 0;
			}
			send = activeActor.isOnGeodataPath() ? new CharMoveToLocation(activeActor) : new MoveToPawn(activeActor, pawn, offset);
		}
		else
		{
			send = new CharMoveToLocation(activeActor);
		}
		
		// Send a Server->Client packet CharMoveToLocation/MoveToPawn to the actor and all L2PcInstance in its knownPlayers
		activeActor.broadcastPacket(send);
	}
	
	protected void moveTo(int x, int y, int z)
	{
		moveTo(x, y, z, 0);
	}
	
	/**
	 * Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
	 * @param x
	 * @param y
	 * @param z
	 * @param offset
	 */
	protected void moveTo(int x, int y, int z, int offset)
	{
		if (activeActor.isMovementDisabled())
		{
			activeActor.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Set AI movement data
		clientMoving = true;
		clientMovingToPawnOffset = 0;
		
		// Calculate movement data for a move to location action and add the actor to movingObjects of GameTimeController
		activeActor.moveToLocation(x, y, z, offset);
		
		// Send a Server->Client packet CharMoveToLocation/MoveToPawn to the actor and all L2PcInstance in its knownPlayers
		activeActor.broadcastPacket(new CharMoveToLocation(activeActor));
	}
	
	/**
	 * Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT><br>
	 * @param pos
	 */
	protected void clientStopMoving(LocationHolder pos)
	{
		// Stop movement of the L2Character
		if (activeActor.isMoving())
		{
			activeActor.stopMove(pos);
		}
		
		clientMovingToPawnOffset = 0;
		
		if (clientMoving || (pos != null))
		{
			clientMoving = false;
			
			// Send a Server->Client packet StopMove to the actor and all L2PcInstance in its knownPlayers
			activeActor.broadcastPacket(new StopMove(activeActor));
			
			if (pos != null)
			{
				// Send a Server->Client packet StopRotation to the actor and all L2PcInstance in its knownPlayers
				activeActor.broadcastPacket(new StopRotation(activeActor, pos.getHeading(), 0));
			}
		}
	}
	
	// Client has already arrived to target, no need to force StopMove packet
	protected void clientStoppedMoving()
	{
		if (clientMovingToPawnOffset > 0) // movetoPawn needs to be stopped
		{
			clientMovingToPawnOffset = 0;
			activeActor.broadcastPacket(new StopMove(activeActor));
		}
		
		clientMoving = false;
	}
	
	/**
	 * Start the actor Auto Attack client side by sending Server->Client packet AutoAttackStart <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT><br>
	 */
	public void clientStartAutoAttack()
	{
		if (!AttackStanceTaskManager.getInstance().isInAttackStance(activeActor))
		{
			activeActor.broadcastPacket(new AutoAttackStart(activeActor));
		}
		
		AttackStanceTaskManager.getInstance().add(activeActor);
	}
	
	/**
	 * Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT><br>
	 */
	public void clientStopAutoAttack()
	{
		activeActor.broadcastPacket(new AutoAttackStop(activeActor));
		AttackStanceTaskManager.getInstance().remove(activeActor);
	}
	
	/**
	 * Kill the actor client side by sending Server->Client packet AutoAttackStop, StopMove/StopRotation, Die <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT><br>
	 */
	protected void clientNotifyDead()
	{
		// Send a Server->Client packet Die to the actor and all L2PcInstance in its knownPlayers
		activeActor.broadcastPacket(new Die(activeActor));
		
		// Init AI
		currentIntention = CtrlIntentionType.IDLE;
		target = null;
		
		// Cancel the follow task if necessary
		stopFollow();
	}
	
	/**
	 * Update the state of this actor client side by sending Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the L2PcInstance player.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT><br>
	 * @param player The L2PcIstance to notify with state of this L2Character
	 */
	public void describeStateToPlayer(L2PcInstance player)
	{
		if (getIntention() == CtrlIntentionType.MOVE_TO)
		{
			if (clientMovingToPawnOffset != 0 && followTarget != null)
			{
				player.sendPacket(new MoveToPawn(activeActor, followTarget, clientMovingToPawnOffset));
			}
			else
			{
				player.sendPacket(new CharMoveToLocation(activeActor));
			}
		}
	}
	
	/**
	 * Create and Launch an AI Follow Task to execute every 1s.<br>
	 * @param target The L2Character to follow
	 */
	public synchronized void startFollow(L2Character target)
	{
		stopFollow();
		
		// Create and Launch an AI Follow Task to execute every 1s
		followTarget = target;
		followTask = ThreadPoolManager.scheduleAtFixedRate(new FollowTask(), 5, FOLLOW_INTERVAL);
	}
	
	/**
	 * Create and Launch an AI Follow Task to execute every 0.5s, following at specified range.
	 * @param target The L2Character to follow
	 * @param range
	 */
	public synchronized void startFollow(L2Character target, int range)
	{
		stopFollow();
		
		followTarget = target;
		followTask = ThreadPoolManager.scheduleAtFixedRate(new FollowTask(range), 5, ATTACK_FOLLOW_INTERVAL);
	}
	
	/**
	 * Stop an AI Follow Task.
	 */
	public synchronized void stopFollow()
	{
		if (followTask != null)
		{
			// Stop the Follow Task
			followTask.cancel(false);
			followTask = null;
		}
		followTarget = null;
	}
	
	protected L2Character getFollowTarget()
	{
		return followTarget;
	}
	
	public L2Object getTarget()
	{
		return target;
	}
	
	protected synchronized void setTarget(L2Object target)
	{
		this.target = target;
	}
}
