package l2j.gameserver.model.actor.knownlist;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Summon;

public class SummonKnownList extends PlayableKnownList
{
	public SummonKnownList(L2Summon activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		return 1500;
	}
	
	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		// get summon
		final L2Summon summon = (L2Summon) activeObject;
		
		// object is owner or taget, use extended range
		if ((object == summon.getOwner()) || (object == summon.getTarget()))
		{
			return 6000;
		}
		
		return 3000;
	}
}
