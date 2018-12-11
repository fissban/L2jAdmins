package l2j.gameserver.model.zone.type;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.enums.ZoneType;

/**
 * The Monster Derby Track Zone
 * @author durgus
 */
public class DerbyTrackZone extends PeaceZone
{
	public DerbyTrackZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.setInsideZone(ZoneType.MONSTERTRACK, true);
		}
		super.onEnter(character);
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			character.setInsideZone(ZoneType.MONSTERTRACK, false);
		}
		super.onExit(character);
	}
}
