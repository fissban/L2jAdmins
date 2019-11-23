package l2j.gameserver.model.actor.ai;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2StaticObjectInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.AutoAttackStart;
import l2j.gameserver.task.continuous.AttackStanceTaskManager;

public class PlayerAI extends PlayableAI
{
	private boolean thinking; // to prevent recursive thinking
	private IntentionCommand nextIntention = null;
	
	/**
	 * @param actor
	 */
	public PlayerAI(L2PcInstance actor)
	{
		super(actor);
	}
	
	void setNextIntention(CtrlIntentionType intention, Object arg0, Object arg1)
	{
		nextIntention = new IntentionCommand(intention, arg0, arg1);
	}
	
	@Override
	public IntentionCommand getNextIntention()
	{
		return nextIntention;
	}
	
	/**
	 * Saves the current Intention for this L2PlayerAI if necessary and calls changeIntention in AbstractAI.
	 * @param intention The new Intention to set to the AI
	 * @param arg0      The first parameter of the Intention
	 * @param arg1      The second parameter of the Intention
	 */
	@Override
	public synchronized void changeIntention(CtrlIntentionType intention, Object arg0, Object arg1)
	{
		// do nothing unless CAST intention
		// however, forget interrupted actions when starting to use an offensive skill
		if ((intention != CtrlIntentionType.CAST) || ((arg0 != null) && ((Skill) arg0).isOffensive()))
		{
			nextIntention = null;
			super.changeIntention(intention, arg0, arg1);
			return;
		}
		
		// do nothing if next intention is same as current one.
		if ((currentIntention == intention) && (arg0 == intentionArg0) && (arg1 == intentionArg1))
		{
			return;
		}
		
		// save current intention so it can be used after cast
		setNextIntention(currentIntention, intentionArg0, intentionArg1);
		super.changeIntention(intention, arg0, arg1);
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
		if (nextIntention != null)
		{
			setIntention(nextIntention.crtlIntention, nextIntention.arg0, nextIntention.arg1);
			nextIntention = null;
		}
		super.onEvtReadyToAct();
	}
	
	/**
	 * Launch actions corresponding to the Event Cancel.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Stop an AI Follow Task
	 * <li>Launch actions corresponding to the Event Think
	 */
	@Override
	protected void onEvtCancel()
	{
		nextIntention = null;
		super.onEvtCancel();
	}
	
	/**
	 * Finalize the casting of a skill. This method overrides L2CharacterAI method.<br>
	 * <b>What it does:</b> Check if actual intention is set to CAST and, if so, retrieves latest intention before the actual CAST and set it as the current intention for the player
	 */
	@Override
	protected void onEvtFinishCasting()
	{
		if (hasIntention(CtrlIntentionType.CAST))
		{
			if ((nextIntention != null) && (nextIntention.crtlIntention != CtrlIntentionType.CAST)) // previous state shouldn't be casting
			{
				setIntention(nextIntention.crtlIntention, nextIntention.arg0, nextIntention.arg1);
			}
			else
			{
				setIntention(CtrlIntentionType.IDLE);
			}
		}
	}
	
	@Override
	protected void onIntentionRest()
	{
		if (getIntention() != CtrlIntentionType.REST)
		{
			changeIntention(CtrlIntentionType.REST, null, null);
			setTarget(null);
			clientStopMoving(null);
		}
	}
	
	@Override
	protected void onIntentionActive()
	{
		setIntention(CtrlIntentionType.IDLE);
	}
	
	/**
	 * Manage the Move To Intention : Stop current Attack and Launch a Move to Location Task.<br>
	 * <b><u> Actions</u> : </b><br>
	 * <li>Stop the actor auto-attack server side AND client side by sending Server->Client packet AutoAttackStop (broadcast)
	 * <li>Set the Intention of this AI to CtrlIntentionType.MOVE_TO
	 * <li>Move the actor to LocationHolder (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)</li>
	 */
	@Override
	protected void onIntentionMoveTo(LocationHolder pos)
	{
		if (hasIntention(CtrlIntentionType.REST))
		{
			// Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
			activeActor.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeActor.isAllSkillsDisabled() || activeActor.isCastingNow() || activeActor.isAttackingNow())
		{
			activeActor.sendPacket(ActionFailed.STATIC_PACKET);
			setNextIntention(CtrlIntentionType.MOVE_TO, pos, null);
			return;
		}
		
		// Set the Intention of this AbstractAI to CtrlIntentionType.MOVE_TO
		changeIntention(CtrlIntentionType.MOVE_TO, pos, null);
		
		// Abort the attack of the L2Character and send Server->Client ActionFailed packet
		activeActor.abortAttack();
		
		// Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
		moveTo(pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	protected void clientNotifyDead()
	{
		clientMovingToPawnOffset = 0;
		clientMoving = false;
		super.clientNotifyDead();
	}
	
	@Override
	public void clientStartAutoAttack()
	{
		if (!AttackStanceTaskManager.getInstance().isInAttackStance(activeActor))
		{
			var summon = ((L2PcInstance) activeActor).getPet();
			if (summon != null)
			{
				summon.broadcastPacket(new AutoAttackStart(summon));
			}
			
			activeActor.broadcastPacket(new AutoAttackStart(activeActor));
		}
		AttackStanceTaskManager.getInstance().add(activeActor);
	}
	
	private void thinkAttack()
	{
		L2Character target = (L2Character) getTarget();
		
		if (target == null)
		{
			setTarget(null);
			setIntention(CtrlIntentionType.ACTIVE);
			return;
		}
		
		if (maybeMoveToPawn(target, activeActor.getStat().getPhysicalAttackRange()))
		{
			activeActor.breakAttack();
			return;
		}
		
		clientStopMoving(null);
		getActor().doAttack(target);
	}
	
	private void thinkCast()
	{
		L2Character target = (L2Character) getTarget();
		
		if (checkTargetLost(target))
		{
			if (currentSkill.isOffensive() && (target != null))
			{
				setTarget(null);
			}
			
			activeActor.setIsCastingNow(false);
			return;
		}
		
		if ((target != null) && maybeMoveToPawn(target, activeActor.getStat().getMagicalAttackRange(currentSkill)))
		{
			activeActor.setIsCastingNow(false);
			return;
		}
		
		if (currentSkill.getHitTime() > 50)
		{
			clientStopMoving(null);
		}
		
		if (!currentSkill.isToggle() || (currentSkill.getId() == 60))
		{
			clientStopMoving(null);
		}
		
		getActor().doCast(currentSkill);
	}
	
	private void thinkPickUp()
	{
		if (activeActor.isAllSkillsDisabled() || activeActor.isCastingNow())
		{
			return;
		}
		
		L2Object target = getTarget();
		if (target == null)
		{
			return;
		}
		
		if (checkTargetLost(target))
		{
			return;
		}
		
		if (maybeMoveToPawn(target, 36))
		{
			return;
		}
		
		setIntention(CtrlIntentionType.IDLE);
		getActor().doPickupItem(target);
	}
	
	private void thinkInteract()
	{
		if (activeActor.isAllSkillsDisabled() || activeActor.isCastingNow())
		{
			return;
		}
		
		L2Object target = getTarget();
		if (target == null)
		{
			return;
		}
		
		if (checkTargetLost(target))
		{
			return;
		}
		
		if (maybeMoveToPawn(target, 36))
		{
			return;
		}
		
		if (!(target instanceof L2StaticObjectInstance))
		{
			getActor().doInteract((L2Character) target);
		}
		setIntention(CtrlIntentionType.IDLE);
	}
	
	@Override
	protected void onEvtThink()
	{
		if (thinking && (getIntention() != null && getIntention() != CtrlIntentionType.CAST))
		{
			return;
		}
		
		thinking = true;
		
		try
		{
			switch (getIntention())
			{
				case ATTACK:
					thinkAttack();
					break;
				case CAST:
					thinkCast();
					break;
				case PICK_UP:
					thinkPickUp();
					break;
				case INTERACT:
					thinkInteract();
					break;
			}
		}
		finally
		{
			thinking = false;
		}
	}
	
	/**
	 * get the actual actor
	 * @return
	 */
	@Override
	public L2PcInstance getActor()
	{
		return (L2PcInstance) activeActor;
	}
}
