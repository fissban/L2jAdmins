package main.engine.events.daily.randoms.type;

import l2j.gameserver.model.actor.instance.L2PcInstance.FlagType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.util.Broadcast;
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
				Broadcast.toAllOnlinePlayers("Event: All Flag has ben Started!");
				L2World.getInstance().getAllPlayers().forEach(p -> p.updatePvPFlag(FlagType.PURPLE));
				break;
			case END:
				Broadcast.toAllOnlinePlayers("Event: All Flag has ben Finished!");
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
