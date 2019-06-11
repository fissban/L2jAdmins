package l2j.gameserver.model.zone.type;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.ZoneSpawn;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * An arena
 * @author durgus
 */
public class ArenaZone extends ZoneSpawn
{
	@SuppressWarnings("unused")
	private String arenaName;
	
	public ArenaZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("Name"))
		{
			arenaName = value;
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneType.PVP, true);
		
		if (character instanceof L2PcInstance)
		{
			character.sendPacket(SystemMessage.ENTERED_COMBAT_ZONE);
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneType.PVP, false);
		
		if (character instanceof L2PcInstance)
		{
			character.sendPacket(SystemMessage.LEFT_COMBAT_ZONE);
		}
	}
}
