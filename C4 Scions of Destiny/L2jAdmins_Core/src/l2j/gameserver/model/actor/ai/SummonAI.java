package l2j.gameserver.model.actor.ai;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;

public class SummonAI extends PlayableAI
{
	private boolean thinking; // to prevent recursive thinking
	private boolean previousFollowStatus = getActor().getFollowStatus();
	
	/**
	 * @param actor
	 */
	public SummonAI(L2Summon actor)
	{
		super(actor);
	}
	
	@Override
	protected void onIntentionIdle()
	{
		stopFollow();
		previousFollowStatus = false;
		onIntentionActive();
	}
	
	@Override
	protected void onIntentionActive()
	{
		if (previousFollowStatus)
		{
			setIntention(CtrlIntentionType.FOLLOW, getActor().getOwner());
		}
		else
		{
			super.onIntentionActive();
		}
	}
	
	private void thinkAttack()
	{
		L2Character target = (L2Character) getTarget();
		
		if (target == null)
		{
			return;
		}
		
		if (checkTargetLostOrDead(target))
		{
			setTarget(null);
			return;
		}
		
		if (maybeMoveToPawn(target, getActor().getStat().getPhysicalAttackRange()))
		{
			getActor().breakAttack();
			return;
		}
		
		clientStopMoving(null);
		getActor().doAttack(target);
	}
	
	private void thinkCast()
	{
		if (checkTargetLost(getTarget()))
		{
			setTarget(null);
			return;
		}
		
		boolean val = previousFollowStatus;
		
		if (maybeMoveToPawn(getTarget(), getActor().getStat().getMagicalAttackRange(currentSkill)))
		{
			return;
		}
		
		clientStopMoving(null);
		getActor().setFollowStatus(false);
		setIntention(CtrlIntentionType.IDLE);
		previousFollowStatus = val;
		getActor().doCast(currentSkill);
	}
	
	private void thinkPickUp()
	{
		if (activeActor.isAllSkillsDisabled())
		{
			return;
		}
		if (checkTargetLost(getTarget()))
		{
			return;
		}
		if (maybeMoveToPawn(getTarget(), 36))
		{
			return;
		}
		setIntention(CtrlIntentionType.IDLE);
		getActor().doPickupItem(getTarget());
	}
	
	private void thinkInteract()
	{
		if (getActor().isAllSkillsDisabled())
		{
			return;
		}
		if (checkTargetLost(getTarget()))
		{
			return;
		}
		if (maybeMoveToPawn(getTarget(), 36))
		{
			return;
		}
		setIntention(CtrlIntentionType.IDLE);
	}
	
	@Override
	public void clientStartAutoAttack()
	{
		activeActor.getActingPlayer().getAI().clientStartAutoAttack();
	}
	
	@Override
	protected void onEvtThink()
	{
		if (thinking || activeActor.isCastingNow() || getActor().isAllSkillsDisabled())
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
	
	@Override
	protected void onEvtFinishCasting()
	{
		if (getActor().getAI().getIntention() != CtrlIntentionType.ATTACK)
		{
			getActor().setFollowStatus(previousFollowStatus);
		}
	}
	
	public void notifyFollowStatusChange()
	{
		previousFollowStatus = !previousFollowStatus;
		switch (getIntention())
		{
			case ACTIVE:
			case FOLLOW:
			case IDLE:
				getActor().setFollowStatus(previousFollowStatus);
				break;
		}
	}
	
	public void setStartFollowController(boolean val)
	{
		previousFollowStatus = val;
	}
	
	/**
	 * get the actual actor
	 * @return
	 */
	@Override
	public L2Summon getActor()
	{
		return (L2Summon) activeActor;
	}
}
