package l2j.gameserver.model.actor.knownlist;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.CharacterAI;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;

public class MonsterKnownList extends AttackableKnownList
{
	public MonsterKnownList(L2MonsterInstance activeChar)
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
			// get monster AI
			final CharacterAI ai = ((L2MonsterInstance) activeObject).getAI();
			
			// AI exists and is idle, set active
			if ((ai != null) && (ai.getIntention() == CtrlIntentionType.IDLE))
			{
				ai.setIntention(CtrlIntentionType.ACTIVE, null);
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
		
		// get monster
		final L2MonsterInstance monster = (L2MonsterInstance) activeObject;
		
		// monster has AI, inform about lost object
		if (monster.hasAI())
		{
			monster.getAI().notifyEvent(CtrlEventType.FORGET_OBJECT, object);
		}
		
		// clear agro list
		if (monster.isVisible() && getObjectType(L2PcInstance.class).isEmpty())
		{
			monster.clearAggroList();
		}
		
		return true;
	}
}
