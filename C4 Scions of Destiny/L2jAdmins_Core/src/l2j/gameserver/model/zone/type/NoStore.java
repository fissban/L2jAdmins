package l2j.gameserver.model.zone.type;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.model.zone.enums.ZoneType;

/**
 * @author fissban
 */
public class NoStore extends Zone
{
	
	public NoStore(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneType.NO_STORE, true);
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneType.NO_STORE, false);
	}
}
