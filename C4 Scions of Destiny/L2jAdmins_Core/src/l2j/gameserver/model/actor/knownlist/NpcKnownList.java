package l2j.gameserver.model.actor.knownlist;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2NpcInstance;

public class NpcKnownList extends CharKnownList
{
	public NpcKnownList(L2Npc activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		// object is not L2Character or object is L2NpcInstance, skip
		if ((object instanceof L2NpcInstance) || !(object instanceof L2Character))
		{
			return 0;
		}
		
		if (object instanceof L2Playable)
		{
			// known list owner if L2FestivalGuide, use extended range
			// if (activeObject instanceof L2FestivalGuideInstance)
			// {
			// return 4000;
			// }
			
			// default range to keep players
			return 1500;
		}
		
		return 500;
	}
	
	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		// distance to watch + 50%
		return (int) Math.round(1.5 * getDistanceToWatchObject(object));
	}
}
