package l2j.gameserver.model.actor.knownlist;

import l2j.Config;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2GuardInstance;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;

public class GuardKnownList extends AttackableKnownList
{
	public GuardKnownList(L2GuardInstance activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public boolean addObject(L2Object object)
	{
		if (!super.addObject(object))
		{
			return false;
		}
		
		// get guard
		final L2GuardInstance guard = (L2GuardInstance) activeObject;
		
		if (object instanceof L2PcInstance)
		{
			// Check if the object added is a L2PcInstance that owns Karma
			if (((L2PcInstance) object).getKarma() > 0)
			{
				// Set the L2GuardInstance Intention to ACTIVE
				if (guard.getAI().getIntention() == CtrlIntentionType.IDLE)
				{
					guard.getAI().setIntention(CtrlIntentionType.ACTIVE, null);
				}
			}
		}
		else if ((Config.GUARD_ATTACK_AGGRO_MOB && guard.isInActiveRegion()) && (object instanceof L2MonsterInstance))
		{
			// Check if the object added is an aggressive L2MonsterInstance
			if (((L2MonsterInstance) object).isAggressive())
			{
				// Set the L2GuardInstance Intention to ACTIVE
				if (guard.getAI().getIntention() == CtrlIntentionType.IDLE)
				{
					guard.getAI().setIntention(CtrlIntentionType.ACTIVE, null);
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean removeObject(L2Object object)
	{
		if (!super.removeObject(object))
		{
			return false;
		}
		
		// get guard
		final L2GuardInstance guard = (L2GuardInstance) activeObject;
		
		// If the aggroList of the L2GuardInstance is empty, set to IDLE
		if (guard.getTarget() == null)
		{
			if (guard.hasAI())
			{
				guard.getAI().setIntention(CtrlIntentionType.IDLE, null);
			}
		}
		return true;
	}
}
