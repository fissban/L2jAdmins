package l2j.gameserver.handler.say;

import l2j.Config;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.handler.SayHandler.ISayHandler;
import l2j.gameserver.model.actor.enums.FloodProtectorType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.util.Broadcast;

/**
 * @author fissban
 */
public class SayShout implements ISayHandler
{
	@Override
	public SayType[] getSayTypeList()
	{
		return new SayType[]
		{
			SayType.SHOUT
		};
	}
	
	@Override
	public void handleSay(SayType type, L2PcInstance activeChar, String target, String text)
	{
		if (!activeChar.tryToUseAction(FloodProtectorType.GLOBAL_CHAT))
		{
			activeChar.sendMessage("Do not spam shout channel.");
			return;
		}
		
		if (Config.DEFAULT_GLOBAL_CHAT.equalsIgnoreCase("on") || (Config.DEFAULT_GLOBAL_CHAT.equalsIgnoreCase("gm") && activeChar.isGM()))
		{
			var region = MapRegionData.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
			for (var player : L2World.getInstance().getAllPlayers())
			{
				if (region == MapRegionData.getInstance().getMapRegion(player.getX(), player.getY()))
				{
					player.sendPacket(new CreatureSay(activeChar, type, activeChar.getName(), text));
				}
			}
			return;
		}
		
		if (Config.DEFAULT_GLOBAL_CHAT.equalsIgnoreCase("global"))
		{
			Broadcast.toAllOnlinePlayers(new CreatureSay(activeChar, type, activeChar.getName(), text));
			return;
		}
	}
}
