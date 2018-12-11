package l2j.gameserver.model.actor.ai;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;

public class SummonAI extends CharacterAI
{
	private boolean thinking; // to prevent recursive thinking
	private boolean previousFollowStatus = getActiveSummon().getFollowStatus();
	
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
			setIntention(CtrlIntentionType.FOLLOW, getActiveSummon().getOwner());
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
		
		if (maybeMoveToPawn(target, getActiveSummon().getStat().getPhysicalAttackRange()))
		{
			getActiveSummon().breakAttack();
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
		
		if (maybeMoveToPawn(getTarget(), getActiveSummon().getStat().getMagicalAttackRange(currentSkill)))
		{
			return;
		}
		
		clientStopMoving(null);
		getActiveSummon().setFollowStatus(false);
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
		((L2Summon) getActor()).doPickupItem(getTarget());
	}
	
	private void thinkInteract()
	{
		if (getActiveSummon().isAllSkillsDisabled())
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
	protected void onEvtThink()
	{
		if (thinking || activeActor.isCastingNow() || getActiveSummon().isAllSkillsDisabled())
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
		if (getActiveSummon().getAI().getIntention() != CtrlIntentionType.ATTACK)
		{
			getActiveSummon().setFollowStatus(previousFollowStatus);
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
				getActiveSummon().setFollowStatus(previousFollowStatus);
				break;
		}
	}
	
	public void setStartFollowController(boolean val)
	{
		previousFollowStatus = val;
	}
	
	private L2Summon getActiveSummon()
	{
		return (L2Summon) activeActor;
	}
}
