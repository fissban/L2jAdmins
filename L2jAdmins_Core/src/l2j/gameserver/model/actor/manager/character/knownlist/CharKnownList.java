package l2j.gameserver.model.actor.manager.character.knownlist;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;

public class CharKnownList extends ObjectKnownList
{
	public CharKnownList(L2Character activeChar)
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
		
		// get character
		final L2Character character = (L2Character) activeObject;
		
		// If object is targeted by the L2Character, cancel Attack or Cast
		if (object == character.getTarget())
		{
			character.setTarget(null);
		}
		
		return true;
	}
	
	/**
	 * Remove all objects from known list, cancel target and inform AI.
	 */
	@Override
	public final void removeAllObjects()
	{
		super.removeAllObjects();
		
		// get character
		final L2Character character = (L2Character) activeObject;
		
		// set target to null
		character.setTarget(null);
		
		// cancel AI task
		if (character.hasAI())
		{
			character.setAI(null);
		}
	}
}
