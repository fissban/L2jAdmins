package l2j.gameserver.model.actor.manager.character.knownlist;

import l2j.gameserver.instancemanager.race.MonsterRace;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2RaceManagerInstance;
import l2j.gameserver.network.external.server.DeleteObject;

public class RaceManagerKnownList extends NpcKnownList
{
	public RaceManagerKnownList(L2RaceManagerInstance activeChar)
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
		
		if (object instanceof L2PcInstance)
		{
			((L2PcInstance) object).sendPacket(MonsterRace.getInstance().getRacePacket());
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
		
		if (object instanceof L2PcInstance)
		{
			// get player
			final L2PcInstance player = ((L2PcInstance) object);
			
			// for all monster race NPCs
			for (L2Npc npc : MonsterRace.getInstance().getMonsters())
			{
				player.sendPacket(new DeleteObject(npc));
			}
		}
		
		return true;
	}
}
