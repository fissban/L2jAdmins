package l2j.gameserver.model.zone.type;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * A jail zone
 * @author durgus
 */
public class JailZone extends Zone
{
	public JailZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.setInsideZone(ZoneType.JAIL, true);
			character.setInsideZone(ZoneType.PVP, true);
			((L2PcInstance) character).sendPacket(SystemMessage.ENTERED_COMBAT_ZONE);
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.setInsideZone(ZoneType.JAIL, false);
			character.setInsideZone(ZoneType.PVP, false);
			((L2PcInstance) character).sendPacket(SystemMessage.LEFT_COMBAT_ZONE);
		}
	}
}
