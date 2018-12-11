package l2j.gameserver.model.actor.knownlist;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeGuardInstance;

public class DoorKnownList extends CharKnownList
{
	public DoorKnownList(L2DoorInstance activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		if (object instanceof L2SiegeGuardInstance)
		{
			return 600;
		}
		
		if (!(object instanceof L2PcInstance))
		{
			return 0;
		}
		
		return 2000;
	}
	
	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		if (object instanceof L2SiegeGuardInstance)
		{
			return 900;
		}
		
		if (!(object instanceof L2PcInstance))
		{
			return 0;
		}
		
		return 3000;
	}
}
