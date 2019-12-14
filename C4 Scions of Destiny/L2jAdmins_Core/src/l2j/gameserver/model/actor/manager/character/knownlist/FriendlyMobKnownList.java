package l2j.gameserver.model.actor.manager.character.knownlist;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2FriendlyMobInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;

public class FriendlyMobKnownList extends AttackableKnownList
{
	public FriendlyMobKnownList(L2FriendlyMobInstance activeChar)
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
		
		// object is player
		if (object instanceof L2PcInstance)
		{
			// get friendly monster
			final L2FriendlyMobInstance monster = (L2FriendlyMobInstance) activeObject;
			
			// AI is idle, set AI
			if (monster.getAI().getIntention() == CtrlIntentionType.IDLE)
			{
				monster.getAI().setIntention(CtrlIntentionType.ACTIVE, null);
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
		
		if (!(object instanceof L2Character))
		{
			return true;
		}
		
		// get friendly monster
		final L2FriendlyMobInstance monster = (L2FriendlyMobInstance) activeObject;
		
		if (monster.hasAI())
		{
			monster.getAI().notifyEvent(CtrlEventType.FORGET_OBJECT, object);
			if (monster.getTarget() == (L2Character) object)
			{
				monster.setTarget(null);
			}
		}
		
		if (monster.isVisible() && getObjectType(L2PcInstance.class).isEmpty())
		{
			monster.clearAggroList();
			if (monster.hasAI())
			{
				monster.getAI().setIntention(CtrlIntentionType.IDLE, null);
			}
		}
		
		return true;
	}
}
