package l2j.gameserver.model.zone.type;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.model.zone.enums.ZoneType;

public class WaterZone extends Zone
{
	public WaterZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneType.WATER, true);
		
		if (character instanceof L2PcInstance)
		{
			((L2PcInstance) character).broadcastUserInfo();
		}
		else if (character instanceof L2Npc)
		{
			for (L2PcInstance player : character.getKnownList().getObjectType(L2PcInstance.class))
			{
				if (player != null)
				{
					((L2Npc) character).sendInfo(player);
				}
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneType.WATER, false);
		
		// TODO: update to only send speed status when that packet is known
		if (character instanceof L2PcInstance)
		{
			((L2PcInstance) character).broadcastUserInfo();
		}
		else if (character instanceof L2Npc)
		{
			for (L2PcInstance player : character.getKnownList().getObjectType(L2PcInstance.class))
			{
				if (player != null)
				{
					((L2Npc) character).sendInfo(player);
				}
			}
		}
	}
	
	public int getWaterZ()
	{
		return getZone().getHighZ();
	}
}
