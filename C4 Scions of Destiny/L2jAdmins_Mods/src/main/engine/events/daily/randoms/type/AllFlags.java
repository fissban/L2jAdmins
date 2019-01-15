package main.engine.events.daily.randoms.type;

import l2j.gameserver.model.actor.instance.L2PcInstance.FlagType;
import l2j.gameserver.model.world.L2World;
import main.engine.AbstractMod;
import main.holders.objects.PlayerHolder;

/**
 * @author fissban
 */
public class AllFlags extends AbstractMod
{
	public AllFlags()
	{
		registerMod(false);
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
				L2World.getInstance().getAllPlayers().forEach(p -> p.updatePvPFlag(FlagType.PURPLE));
				break;
			case END:
				L2World.getInstance().getAllPlayers().forEach(p -> p.updatePvPFlag(FlagType.NON_PVP));
				break;
		}
	}
	
	@Override
	public void onEnterWorld(PlayerHolder ph)
	{
		ph.getInstance().updatePvPFlag(FlagType.PURPLE);
	}
}
