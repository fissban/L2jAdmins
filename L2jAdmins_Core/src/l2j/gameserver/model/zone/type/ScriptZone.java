package l2j.gameserver.model.zone.type;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.zone.Zone;

/**
 * A scripted zone... Creation of such a zone should require somekind of jython script reference which can handle onEnter() / onExit()
 * @author durgus
 */
public class ScriptZone extends Zone
{
	public ScriptZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
	}
	
	@Override
	protected void onExit(L2Character character)
	{
	}
}
