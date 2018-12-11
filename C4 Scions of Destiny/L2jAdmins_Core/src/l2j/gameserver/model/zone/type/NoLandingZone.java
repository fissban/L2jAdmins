package l2j.gameserver.model.zone.type;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.MountType;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * A no landing zone
 * @author durgus
 */
public class NoLandingZone extends Zone
{
	public NoLandingZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.setInsideZone(ZoneType.NOLANDING, true);
			
			if (((L2PcInstance) character).getMountType() == MountType.WYVERN)
			{
				character.sendPacket(SystemMessage.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN);
				((L2PcInstance) character).enteredNoLanding();
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.setInsideZone(ZoneType.NOLANDING, false);
			if (((L2PcInstance) character).getMountType() == MountType.WYVERN)
			{
				((L2PcInstance) character).exitedNoLanding();
			}
		}
	}
}
