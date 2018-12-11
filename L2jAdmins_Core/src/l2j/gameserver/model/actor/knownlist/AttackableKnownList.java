package l2j.gameserver.model.actor.knownlist;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2NpcInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;

public class AttackableKnownList extends NpcKnownList
{
	public AttackableKnownList(L2Attackable activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public boolean removeObject(L2Object object)
	{
		if (!super.removeObject(object))
		{
			return false;
		}
		
		// get attackable
		final L2Attackable attackable = (L2Attackable) activeObject;
		
		// remove object from agro list
		if (object instanceof L2Character)
		{
			attackable.getAggroList().remove(object);
		}
		
		// check AI for players and set AI to idle
		if (attackable.hasAI() && getObjectType(L2PcInstance.class).isEmpty())
		{
			attackable.getAI().setIntention(CtrlIntentionType.IDLE, null);
		}
		
		return true;
	}
	
	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		if ((object instanceof L2NpcInstance) || !(object instanceof L2Character))
		{
			return 0;
		}
		
		if (object instanceof L2Playable)
		{
			return object.getKnownList().getDistanceToWatchObject(activeObject);
		}
		
		// get attackable
		final L2Attackable attackable = (L2Attackable) activeObject;
		
		return Math.max(300, Math.max(attackable.getAggroRange(), attackable.getFactionRange()));
	}
}
